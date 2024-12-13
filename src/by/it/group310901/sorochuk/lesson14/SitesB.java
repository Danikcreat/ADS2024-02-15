package by.it.group310901.sorochuk.lesson14;

import java.util.*;
import java.util.stream.Collectors;

public class SitesB {

    public static void main(String[] args) {

        List<Set<String>> dsu = new ArrayList<>(); // Список множеств для представления групп связанных сайтов
        Set<String> links = new HashSet<>(); // Множество всех пар ссылок site1+site2

        try (Scanner scanner = new Scanner(System.in)) {
            String line;

            // Читаем вводимые строки до появления "end"
            while (!(line = scanner.nextLine()).equals("end")) {
                links.add(line); // Сохраняем связь между сайтами
                String[] sites = line.split("\\+"); // Разбиваем строку на два сайта
                Set<String> set = new HashSet<>(Arrays.asList(sites)); // Создаем множество для связи
                dsu.add(set); // Добавляем множество в список групп
            }
        }

        // Основной цикл объединения групп связанных сайтов
        for (int i = 0; i < dsu.size(); i++) { // Проходим по всем группам
            for (Set<String> set : dsu) { // Для каждой группы проверяем пересечение с текущей группой
                boolean union = false; // Флаг объединения групп

                label:
                if (dsu.get(i) != set) { // Проверяем только разные группы
                    for (String site1 : dsu.get(i)) { // Для каждого сайта в текущей группе
                        for (String site2 : set) { // Проверяем каждый сайт в другой группе
                            // Если сайты связаны, объединяем группы
                            if (!site1.equals(site2) && checkLink(links, site1, site2)) {
                                union = true;
                                break label; // Прерываем вложенные циклы
                            }
                        }
                    }
                }

                if (union) { // Если группы нужно объединить
                    dsu.get(i).addAll(set); // Добавляем элементы из другой группы в текущую
                    set.clear(); // Очищаем другую группу (она будет удалена позже)
                    i = 0; // Начинаем заново, так как список изменился
                }
            }
        }

        // Удаляем пустые группы
        dsu.removeIf(Set::isEmpty);

        // Формируем строку с размерами групп, отсортированными по убыванию
        String output = dsu.stream()
                .map(Set::size) // Преобразуем группы в их размеры
                .sorted((n, m) -> m - n) // Сортируем размеры по убыванию
                .map(String::valueOf) // Преобразуем размеры в строки
                .collect(Collectors.joining(" ")) // Объединяем в одну строку через пробел
                .trim(); // Удаляем лишние пробелы

        // Выводим результат
        System.out.println(output);
    }

    // Проверяем, есть ли связь между site1 и site2
    private static boolean checkLink(Set<String> links, String site1, String site2) {
        // Проверяем оба возможных направления связи (site1+site2 и site2+site1)
        return links.contains(String.format("%s+%s", site1, site2)) ||
                links.contains(String.format("%s+%s", site2, site1));
    }
}
