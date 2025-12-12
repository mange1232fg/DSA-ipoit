package by.it.group410971.kozich.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerC {

    // Класс для хранения информации о файле
    static class FileInfo {
        String path;     // относительный путь от src
        String content;  // обработанное содержимое
        int length;      // длина содержимого (для оптимизации)

        FileInfo(String path, String content) {
            this.path = path;
            this.content = content;
            this.length = content.length();
        }
    }

    // Проверка, является ли файл тестовым
    private static boolean isTestFile(String content) {
        return content.contains("@Test") || content.contains("org.junit.Test");
    }

    // Обработка содержимого файла за O(n)
    private static String processContent(String content) {
        if (content.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder(content.length());
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inString = false;
        boolean inChar = false;
        char prevChar = 0;
        boolean skipLine = false;
        boolean hasContentAfterPackageImport = false;

        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
            char next = (i + 1 < content.length()) ? content.charAt(i + 1) : 0;

            // Обработка перевода строки
            if (current == '\n') {
                inLineComment = false;
                skipLine = false;
                continue;
            }

            // Если мы пропускаем строку (package/import), продолжаем до конца строки
            if (skipLine) {
                continue;
            }

            // Обработка строковых и символьных литералов
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
                }

                // Проверка на package и import в начале строки
                if (!inBlockComment && !inLineComment && !hasContentAfterPackageImport) {
                    // Проверяем, начинается ли строка с "package " или "import "
                    if (i == 0 || content.charAt(i - 1) == '\n') {
                        int remaining = content.length() - i;
                        if (remaining >= 8 && content.startsWith("package ", i)) {
                            skipLine = true;
                            continue;
                        } else if (remaining >= 7 && content.startsWith("import ", i)) {
                            skipLine = true;
                            continue;
                        } else {
                            hasContentAfterPackageImport = true;
                        }
                    }
                }
            }

            // Если мы не в комментарии и не пропускаем строку, добавляем символ
            if (!inBlockComment && !inLineComment && !skipLine && hasContentAfterPackageImport) {
                // Заменяем символы с кодом < 33 на пробел (32)
                if (current < 33) {
                    result.append(' ');
                } else {
                    result.append(current);
                }
            }

            prevChar = current;
        }

        String processed = result.toString();

        // Выполняем trim()
        processed = processed.trim();

        // Удаляем лишние пробелы (опционально, для лучшей оптимизации)
        return processed.replaceAll("\\s+", " ");
    }

    // Оптимизированное расстояние Левенштейна с ограничением по максимальному расстоянию
    private static int levenshteinDistance(String s1, String s2, int maxDistance) {
        int len1 = s1.length();
        int len2 = s2.length();

        // Быстрая проверка на большие различия в длине
        if (Math.abs(len1 - len2) > maxDistance) {
            return maxDistance + 1;
        }

        // Используем два массива для экономии памяти
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        // Инициализация первого ряда
        for (int j = 0; j <= len2; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            curr[0] = i;
            int minInRow = i;

            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(
                                curr[j - 1] + 1,     // вставка
                                prev[j] + 1          // удаление
                        ),
                        prev[j - 1] + cost       // замена
                );
                minInRow = Math.min(minInRow, curr[j]);
            }

            // Ранний выход если минимальное расстояние в строке уже больше maxDistance
            if (minInRow > maxDistance) {
                return maxDistance + 1;
            }

            // Меняем массивы местами
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[len2];
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

                // Пропускаем тестовые файлы и пустые файлы
                if (content.isEmpty() || isTestFile(content)) {
                    continue;
                }

                // Обрабатываем содержимое
                String processedContent = processContent(content);

                // Пропускаем пустые файлы после обработки
                if (processedContent.isEmpty()) {
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

                // Преобразуем разделители пути
                String normalizedPath = relativePath.toString()
                        .replace(File.separator, "/");

                // Сохраняем информацию о файле
                fileInfos.add(new FileInfo(normalizedPath, processedContent));
            }

            // Сортируем файлы по пути для лексикографического вывода
            fileInfos.sort(Comparator.comparing(f -> f.path));

            // Находим копии
            Map<String, List<String>> copiesMap = new TreeMap<>();
            int threshold = 10; // максимальное расстояние для считания копией

            // Используем оптимизацию: сравниваем только файлы схожей длины
            for (int i = 0; i < fileInfos.size(); i++) {
                FileInfo file1 = fileInfos.get(i);
                List<String> copies = new ArrayList<>();

                for (int j = i + 1; j < fileInfos.size(); j++) {
                    FileInfo file2 = fileInfos.get(j);

                    // Быстрая проверка: если разница в длине больше threshold, пропускаем
                    if (Math.abs(file1.length - file2.length) > threshold) {
                        continue;
                    }

                    // Вычисляем расстояние Левенштейна с ограничением
                    int distance = levenshteinDistance(file1.content, file2.content, threshold);

                    if (distance < threshold) {
                        copies.add(file2.path);
                    }
                }

                if (!copies.isEmpty()) {
                    copiesMap.put(file1.path, copies);
                }
            }

            // Выводим результаты
            for (Map.Entry<String, List<String>> entry : copiesMap.entrySet()) {
                System.out.println(entry.getKey());
                List<String> copies = entry.getValue();
                copies.sort(String::compareTo);
                for (String copy : copies) {
                    System.out.println(copy);
                }
                // Пустая строка между группами
                if (!copiesMap.isEmpty()) {
                    System.out.println();
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при обходе каталогов: " + e.getMessage());
        }
    }
}