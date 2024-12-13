package by.it.group310901.sorochuk.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Класс SourceScannerA сканирует директорию исходных файлов, обрабатывает их и выводит размеры файлов.
public class SourceScannerA {
    public static void main(String[] args) {
        // Получаем путь к директории с исходными кодами.
        String srcDir = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileEntry> fileEntries = new ArrayList<>();

        try {
            // Проходим по всем файлам в директории src и её подкаталогах
            Files.walk(Path.of(srcDir))
                    // Фильтруем только файлы с расширением .java
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            // Читаем содержимое файла
                            String content = Files.readString(path);

                            // Исключаем файлы с тестами
                            if (!content.contains("@Test") && !content.contains("org.junit.Test")) {
                                String processed = processContent(content); // Обрабатываем содержимое
                                if (!processed.isEmpty()) {
                                    int sizeInBytes = processed.getBytes().length; // Вычисляем размер файла
                                    String relativePath = Path.of(srcDir).relativize(path).toString(); // Получаем относительный путь
                                    fileEntries.add(new FileEntry(relativePath, sizeInBytes)); // Добавляем файл в список
                                }
                            }
                        } catch (MalformedInputException e) {
                            // Обработка ошибки, если файл не может быть прочитан
                            System.err.println("Malformed input: " + path);
                        } catch (IOException e) {
                            // Обработка остальных ошибок ввода-вывода
                            e.printStackTrace();
                        }
                    });

            // Сортируем файлы сначала по размеру, затем по пути
            fileEntries.sort(Comparator.comparingInt((FileEntry f) -> f.size)
                    .thenComparing(f -> f.path));

            // Выводим результаты
            for (FileEntry entry : fileEntries) {
                System.out.println(entry.size + " " + entry.path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки содержимого файла
    private static String processContent(String content) {
        StringBuilder sb = new StringBuilder();
        String[] lines = content.split("\n");

        // Пропускаем строки с объявлениями пакетов и импортами
        for (String line : lines) {
            line = line.strip();

            if (line.startsWith("package") || line.startsWith("import")) {
                continue;
            }
            sb.append(line).append("\n");
        }

        String processed = sb.toString();
        return removeNonPrintable(processed); // Удаляем непечатаемые символы
    }

    // Метод для удаления непечатаемых символов из текста
    private static String removeNonPrintable(String text) {
        int start = 0, end = text.length();

        // Находим первое печатное символ
        while (start < end && text.charAt(start) < 33) {
            start++;
        }
        // Находим последний печатный символ
        while (end > start && text.charAt(end - 1) < 33) {
            end--;
        }

        return text.substring(start, end); // Возвращаем строку без лишних символов
    }

    // Класс для хранения информации о файле (путь и размер)
    static class FileEntry {
        String path; // Путь к файлу
        int size;    // Размер файла в байтах

        public FileEntry(String path, int size) {
            this.path = path;
            this.size = size;
        }
    }
}
