package by.it.group410971.kozich.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: рюкзак без повторов

Первая строка входа содержит целые числа
    1<=W<=100000     вместимость рюкзака
    1<=n<=300        число золотых слитков
                    (каждый можно использовать только один раз).
Следующая строка содержит n целых чисел, задающих веса каждого из слитков:
  0<=w[1]<=100000 ,..., 0<=w[n]<=100000

Найдите методами динамического программирования
максимальный вес золота, который можно унести в рюкзаке.

Sample Input:
10 3
1 4 8
Sample Output:
9
*/

public class B_Knapsack {

    int getMaxWeight(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int W = scanner.nextInt();
        int n = scanner.nextInt();
        int[] gold = new int[n];
        for (int i = 0; i < n; i++) {
            gold[i] = scanner.nextInt();
        }

        // dp[x] = true, если можно набрать вес x
        boolean[] dp = new boolean[W + 1];
        dp[0] = true;

        for (int i = 0; i < n; i++) {
            int weight = gold[i];
            // Идём в обратном порядке, чтобы не использовать один элемент несколько раз
            for (int currentWeight = W - weight; currentWeight >= 0; currentWeight--) {
                if (dp[currentWeight]) {
                    dp[currentWeight + weight] = true;
                }
            }
        }

        // Найдем максимальный вес, который можно набрать
        for (int maxWeight = W; maxWeight >= 0; maxWeight--) {
            if (dp[maxWeight]) {
                return maxWeight;
            }
        }

        return 0;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_Knapsack.class.getResourceAsStream("dataB.txt");
        B_Knapsack instance = new B_Knapsack();
        int res = instance.getMaxWeight(stream);
        System.out.println(res);
    }
}
