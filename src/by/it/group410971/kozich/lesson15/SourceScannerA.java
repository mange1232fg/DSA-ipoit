package by.it.group410971.kozich.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerA {

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

    // Обработка содержимого файла
    private static String processContent(String content) {
        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            // Пропускаем строки package и import
            if (line.trim().startsWith("package") ||
                    line.trim().startsWith("import")) {
                continue;
            }
            result.append(line).append("\n");
        }

        // Удаляем символы с кодом < 33 в начале и конце
        String processed = result.toString();

        // Удаляем в начале
        int start = 0;
        while (start < processed.length() && processed.charAt(start) < 33) {
            start++;
        }

        // Удаляем в конце
        int end = processed.length() - 1;
        while (end >= start && processed.charAt(end) < 33) {
            end--;
        }

        return processed.substring(start, end + 1);
    }

    // Чтение файла с обработкой ошибок кодировки
    private static String readFile(Path filePath) throws IOException {
        List<Charset> charsets = Arrays.asList(
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                Charset.forName("Windows-1251")
        );

        for (Charset charset : charsets) {
            try {
                byte[] bytes = Files.readAllBytes(filePath);
                return new String(bytes, charset);
            } catch (MalformedInputException e) {
                // Пробуем следующую кодировку
                // continue; // Removed as it's unnecessary
            }
        }

        // Если все кодировки не подошли, читаем как байты и игнорируем ошибки
        byte[] bytes = Files.readAllBytes(filePath);
        return new String(bytes, StandardCharsets.UTF_8).replaceAll("[^\\x00-\\x7F]", "");
    }

    // Сбор всех .java файлов
    private static List<Path> collectJavaFiles(String srcDir) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Path srcPath = Paths.get(srcDir);

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
        String srcDir = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            // Собираем все .java файлы
            List<Path> javaFiles = collectJavaFiles(srcDir);

            // Обрабатываем каждый файл
            for (Path filePath : javaFiles) {
                try {
                    // Читаем содержимое файла
                    String content = readFile(filePath);

                    // Пропускаем тестовые файлы
                    if (isTestFile(content)) {
                        continue;
                    }

                    // Обрабатываем содержимое
                    String processedContent = processContent(content);

                    // Получаем относительный путь от src
                    Path srcPath = Paths.get(srcDir);
                    Path relativePath = srcPath.relativize(filePath);

                    // Преобразуем разделители пути в системные
                    String normalizedPath = relativePath.toString().replace("\\", "/");

                    // Считаем размер в байтах (используем UTF-8 для консистентности)
                    long size = processedContent.getBytes(StandardCharsets.UTF_8).length;

                    // Сохраняем информацию о файле
                    fileInfos.add(new FileInfo(normalizedPath, size, processedContent));

                } catch (IOException e) {
                    // Игнорируем файлы с ошибками чтения
                    System.err.println("Ошибка чтения файла: " + filePath);
                }
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