package by.it.group310901.sorochuk.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

// Класс SourceScannerC предназначен для поиска схожих файлов в проекте
// и вывода информации о найденных клонах.
public class SourceScannerC {
    static final int NORMAL_DISTANCE = 9;  // Нормальное расстояние для определения схожести файлов

    // Метод для определения расстояния замены символов между двумя символами
    private static int areReplacementNumbers(char c1, char c2) {
        return c1 == c2 ? 0 : 1;
    }

    // Метод для получения минимального значения из массива чисел
    private static int getMinEdit(int... numbers) {
        return Arrays.stream(numbers).min().orElse(
                Integer.MAX_VALUE);
    }

    // Метод для сдвига массива символов (удаляет пустые символы с обоих концов)
    protected static char[] move(char[] array) {
        char[] temp;
        int i = 0, size;

        // Сдвигаем начало массива, пока не встретится непустой символ
        while(array[i] == 0)
            i++;

        size = array.length - i;
        temp = new char[size];
        System.arraycopy(array, i, temp, 0, size);
        array = temp;

        // Сдвигаем конец массива, пока не встретится непустой символ
        i = array.length - 1;
        while (array[i] == 0)
            i--;

        size = i + 1;
        temp = new char[size];
        System.arraycopy(array, 0, temp, 0, size);
        return temp;
    }

    // Метод для проверки схожести двух файлов по их содержимому
    private static boolean checkDistance(String file1, String file2) {
        int distance = Math.abs(file1.length() - file2.length());

        if (distance > NORMAL_DISTANCE)
            return false;

        String s1, s2;
        String[] array_s1 = file1.split(" "), array_s2 = file2.split(" ");

        // Сравниваем каждый элемент в строках
        for (int index = 0; index < array_s1.length; index++) {
            s1 = array_s1[index];
            s2 = array_s2[index];
            int length = s2.length() + 1;
            int[] currRow = new int[length];
            int[] prevRow;

            for (int i = 0; i <= s1.length(); i++) {
                prevRow = currRow;
                currRow = new int[length];

                // Вычисляем расстояние Левенштейна
                for (int j = 0; j <= s2.length(); j++) {
                    currRow[j] = i == 0 ? j : (j == 0 ? i : getMinEdit(prevRow[j - 1]
                                    + areReplacementNumbers(s1.charAt(i - 1), s2.charAt(j - 1)),
                            prevRow[j] + 1,
                            currRow[j - 1] + 1));
                }
            }
            distance += currRow[s2.length()];
            // Если расстояние больше нормального, возвращаем false
            if (distance > NORMAL_DISTANCE)
                return false;
        }
        return true;
    }

    // Компаратор для сортировки списка путей файлов
    protected static class MyArrayComparator implements Comparator<ArrayList<Path>> {
        @Override
        public int compare(ArrayList<Path> a1, ArrayList<Path> a2) {
            Collections.sort(a1);
            Collections.sort(a2);

            // Сортируем по первому пути в списке
            return a1.get(0).compareTo(a2.get(0));
        }
    }

    // Метод для поиска равных файлов по содержимому
    private static ArrayList<ArrayList<Path>> findEqualFiles(HashMap<Path, String> filePaths) {
        ArrayList<ArrayList<Path>> equalFiles = new ArrayList<>();
        ArrayList<Path> array, used = new ArrayList<>();

        for(Path filePath1 : filePaths.keySet()) {
            if (!used.contains(filePath1)) {
                array = new ArrayList<>();
                array.add(filePath1);

                // Проверяем все файлы на схожесть
                for (Path filePath2 : filePaths.keySet())
                    if (filePath1 != filePath2 && checkDistance(filePaths.get(filePath1), filePaths.get(filePath2))) {
                        array.add(filePath2);
                        used.add(filePath2);
                    }

                if (array.size() > 1)
                    equalFiles.add(array);
            }
        }
        return equalFiles;
    }

    // Метод для вывода информации о копиях классов
    private static void findCopies(HashMap<String, HashMap<Path, String>> classes) {
        ArrayList<ArrayList<Path>> equalFiles;
        Set<String> classNames = classes.keySet();

        int count;

        // Проходим по всем классам и выводим найденные клонированные файлы
        for (String className : classNames) {
            count = 0;
            equalFiles = findEqualFiles(classes.get(className));
            Collections.sort(equalFiles, new MyArrayComparator());

            if (!equalFiles.isEmpty()) {
                System.out.println("\n" + className + ":");
                for (ArrayList<Path> paths : equalFiles) {
                    System.out.println("\nКлон №" + ++count);
                    for (Path path : paths)
                        System.out.println(path);
                }
            }
        }
    }

    // Основной метод для получения и обработки информации о файлах
    protected static void getInformation() throws IOException {
        HashMap<String, HashMap<Path, String>> javaClasses = new HashMap<>();

        Path src = Path.of(System.getProperty("user.dir")
                + File.separator + "src" + File.separator);

        try (Stream<Path> fileTrees = Files.walk(src)) {
            fileTrees.forEach(
                    directory -> {
                        if (directory.toString().endsWith(".java")) {
                            try {
                                char[] charArr;
                                String str = Files.readString(directory);
                                if (!str.contains("@Test") && !str.contains("org.junit.Test")) {
                                    // Убираем пакеты, импорты и комментарии
                                    str = str.replaceAll("package.*;", "")
                                            .replaceAll("import.*;", "");

                                    str = str.replaceAll("/\\*[\\w\\W\r\n\t]*?\\*/", "")
                                            .replaceAll("//.*?\r\n\\s*", "");

                                    while (str.contains("\r\n\r\n"))
                                        str = str.replaceAll("\r\n\r\n", "\r\n");

                                    if (!str.isEmpty() && (str.charAt(0) < 33 || str.charAt(str.length() - 1) < 33)) {
                                        charArr = str.toCharArray();
                                        int indexF = 0, indexL = charArr.length - 1;

                                        while (indexF < charArr.length && charArr[indexF] < 33 && charArr[indexF] != 0)
                                            charArr[indexF++] = 0;

                                        while (indexL >= 0 && charArr[indexL] < 33 && charArr[indexL] != 0)
                                            charArr[indexL--] = 0;

                                        str = new String(move(charArr));
                                    }
                                    str = str.replaceAll("[\u0000- ]++", " ");

                                    // Добавляем файл в соответствующий класс
                                    if (!javaClasses.containsKey(directory.getFileName().toString()))
                                        javaClasses.put(directory.getFileName().toString(), new HashMap<>());
                                    javaClasses.get(directory.getFileName().toString()).put(src.relativize(directory), str);
                                }
                            } catch (IOException e) {
                                if (System.currentTimeMillis() < 0) {
                                    System.err.println(directory);
                                }
                            }
                        }
                    }
            );
            // Ищем и выводим копии классов
            findCopies(javaClasses);
        }
    }

    public static void main(String[] args) throws IOException {
        getInformation();
    }
}
