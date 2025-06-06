package by.it.group410971.kozich.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
https://ru.wikipedia.org/wiki/Расстояние_Левенштейна

Дано:
Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
Итерационно вычислить алгоритм преобразования двух данных непустых строк
Вывести через запятую редакционное предписание в формате:
операция("+" вставка, "-" удаление, "~" замена, "#" копирование)
символ замены или вставки
*/

public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int n = one.length();
        int m = two.length();
        int[][] dp = new int[n + 1][m + 1];

        // Заполнение базовых случаев
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        // Основная динамика
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (one.charAt(i - 1) == two.charAt(j - 1)) ? 0 : 1;
                int del = dp[i - 1][j] + 1;      // удаление
                int ins = dp[i][j - 1] + 1;      // вставка
                int rep = dp[i - 1][j - 1] + cost; // замена или копирование
                dp[i][j] = Math.min(Math.min(del, ins), rep);
            }
        }

        // Восстановление предписания
        StringBuilder result = new StringBuilder();
        int i = n, j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                // удаление
                result.insert(0, "-" + one.charAt(i - 1) + ",");
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                // вставка
                result.insert(0, "+" + two.charAt(j - 1) + ",");
                j--;
            } else {
                // либо замена, либо копирование
                if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    result.insert(0, "#,");
                } else {
                    result.insert(0, "~" + two.charAt(j - 1) + ",");
                }
                i--;
                j--;
            }
        }
        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}
