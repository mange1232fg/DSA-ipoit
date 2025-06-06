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
    Рекурсивно вычислить расстояние редактирования двух данных непустых строк
*/

public class A_EditDist {

    int getDistanceEdinting(String one, String two) {
        int n = one.length();
        int m = two.length();
        // dp[i][j] - расстояние Левенштейна для первых i символов one и первых j символов two
        int[][] dp = new int[n + 1][m + 1];

        // Заполнение базовых случаев
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i; // преобразовать i символов в пустую строку - i удалений
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j; // преобразовать пустую строку в j символов - j вставок
        }

        // Динамика
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (one.charAt(i - 1) == two.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,      // удаление
                                dp[i][j - 1] + 1       // вставка
                        ),
                        dp[i - 1][j - 1] + cost      // замена
                );
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_EditDist.class.getResourceAsStream("dataABC.txt");
        A_EditDist instance = new A_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}
