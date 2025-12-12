package by.it.group410971.kozich.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerB {

    // Класс для хранения информации о файле
    static class FileInfo implements Comparable<FileInfo> {
        String path;     // относительный путь от src
        long size;       // размер в байтах
        String content;  // обработанное содержимое

        FileInfo(String path, long size, String content) {
            this.path = path;
            this.size = size;
            this.content = content;
        }

        @Override
        public int compareTo(FileInfo other) {
            // Сначала сравниваем по размеру
            int sizeCompare = Long.compare(this.size, other.size);
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            // Если размеры равны - лексикографически по пути
            return this.path.compareTo(other.path);
        }
    }

    // Проверка, является ли файл тестовым
    private static boolean isTestFile(String content) {
        return content.contains("@Test") || content.contains("org.junit.Test");
    }

    // Обработка содержимого файла за O(n)
    private static String processContent(String content) {
        if (content.isEmpty()) {
            return content;
        }

        StringBuilder result = new StringBuilder();
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inString = false;
        boolean inChar = false;
        char prevChar = 0;

        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
            char next = (i + 1 < content.length()) ? content.charAt(i + 1) : 0;

            // Обработка строковых литералов и символьных литералов
            if (!inBlockComment && !inLineComment) {
                if (!inChar && current == '"' && prevChar != '\\') {
                    inString = !inString;
                } else if (!inString && current == '\'' && prevChar != '\\') {
                    inChar = !inChar;
                }
            }

            if (!inString && !inChar) {
                // Обработка комментариев
                if (!inBlockComment && !inLineComment && current == '/' && next == '*') {
                    inBlockComment = true;
                    i++; // Пропускаем следующий символ
                    prevChar = current;
                    continue;
                } else if (inBlockComment && current == '*' && next == '/') {
                    inBlockComment = false;
                    i++; // Пропускаем следующий символ
                    prevChar = current;
                    continue;
                } else if (!inBlockComment && !inLineComment && current == '/' && next == '/') {
                    inLineComment = true;
                    i++; // Пропускаем следующий символ
                    prevChar = current;
                    continue;
                } else if (inLineComment && current == '\n') {
                    inLineComment = false;
                }
            }

            // Если мы не в комментарии, добавляем символ
            if (!inBlockComment && !inLineComment) {
                // Пропускаем управляющие символы < 32, но оставляем пробел (32)
                if (current >= 32 || current == '\n' || current == '\t') {
                    result.append(current);
                }
            }

            prevChar = current;
        }

        // Удаление строк package и import
        String[] lines = result.toString().split("\n");
        StringBuilder filteredLines = new StringBuilder();
        boolean firstNonImportPackageLine = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Пропускаем строки package и import, но только до первой не package/import строки
            if (!firstNonImportPackageLine &&
                    (trimmedLine.startsWith("package ") ||
                            trimmedLine.startsWith("import "))) {
                continue;
            }

            if (!trimmedLine.isEmpty() &&
                    !trimmedLine.startsWith("package ") &&
                    !trimmedLine.startsWith("import ")) {
                firstNonImportPackageLine = true;
            }

            if (firstNonImportPackageLine) {
                // Удаляем пустые строки
                if (!trimmedLine.isEmpty()) {
                    filteredLines.append(line).append("\n");
                }
            }
        }

        String processed = filteredLines.toString();

        // Удаляем символы с кодом < 33 в начале
        int start = 0;
        while (start < processed.length() && processed.charAt(start) < 33) {
            start++;
        }

        // Удаляем символы с кодом < 33 в конце
        int end = processed.length() - 1;
        while (end >= start && processed.charAt(end) < 33) {
            end--;
        }

        if (start > end) {
            return "";
        }

        return processed.substring(start, end + 1);
    }

    // Чтение файла с обработкой ошибок кодировки
    private static String readFile(Path filePath) {
        // Пробуем несколько кодировок
        Charset[] charsets = {
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                Charset.forName("Windows-1251"),
                StandardCharsets.US_ASCII
        };

        for (Charset charset : charsets) {
            try {
                byte[] bytes = Files.readAllBytes(filePath);
                return new String(bytes, charset);
            } catch (MalformedInputException e) {
                // Пробуем следующую кодировку
                continue;
            } catch (IOException e) {
                return "";
            }
        }

        // Если все кодировки не подошли, читаем как UTF-8 с заменой некорректных символов
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes, StandardCharsets.UTF_8)
                    .replaceAll("[\\uFFFD\\u0000-\\u001F]", "");
        } catch (IOException e) {
            return "";
        }
    }

    // Сбор всех .java файлов
    private static List<Path> collectJavaFiles(String srcDir) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Path srcPath = Paths.get(srcDir);

        if (!Files.exists(srcPath) || !Files.isDirectory(srcPath)) {
            return javaFiles;
        }

        Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().toLowerCase().endsWith(".java")) {
                    javaFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // Игнорируем ошибки доступа к файлам
                return FileVisitResult.CONTINUE;
            }
        });

        return javaFiles;
    }

    @SuppressWarnings("unused")
    static void main(String[] args) {
        String srcDir = System.getProperty("user.dir")
                + File.separator + "src" + File.separator;
        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            // Собираем все .java файлы
            List<Path> javaFiles = collectJavaFiles(srcDir);

            // Обрабатываем каждый файл
            for (Path filePath : javaFiles) {
                // Читаем содержимое файла
                String content = readFile(filePath);

                // Пропускаем тестовые файлы
                if (content.isEmpty() || isTestFile(content)) {
                    continue;
                }

                // Обрабатываем содержимое
                String processedContent = processContent(content);

                // Пропускаем пустые файлы после обработки
                if (processedContent.trim().isEmpty()) {
                    continue;
                }

                // Получаем относительный путь от src
                Path srcPath = Paths.get(srcDir);
                Path relativePath;
                try {
                    relativePath = srcPath.relativize(filePath);
                } catch (IllegalArgumentException e) {
                    // Если файл не находится внутри src, пропускаем его
                    continue;
                }

                // Преобразуем разделители пути в системные
                String normalizedPath = relativePath.toString()
                        .replace(File.separator, "/");

                // Считаем размер в байтах (используем UTF-8 для консистентности)
                long size = processedContent.getBytes(StandardCharsets.UTF_8).length;

                // Сохраняем информацию о файле
                fileInfos.add(new FileInfo(normalizedPath, size, processedContent));
            }

            // Сортируем файлы
            Collections.sort(fileInfos);

            // Выводим результаты
            for (FileInfo fileInfo : fileInfos) {
                System.out.println(fileInfo.size + " " + fileInfo.path);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при обходе каталогов: " + e.getMessage());
        }
    }
}