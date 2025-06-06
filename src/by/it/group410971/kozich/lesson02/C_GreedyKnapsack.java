package by.it.group410971.kozich.lesson02;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class C_GreedyKnapsack {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = C_GreedyKnapsack.class.getResourceAsStream("greedyKnapsack.txt");
        double costFinal = new C_GreedyKnapsack().calc(inputStream);
        long finishTime = System.currentTimeMillis();
        System.out.printf("Общая стоимость %f (время %d)\n", costFinal, finishTime - startTime);
    }

    double calc(InputStream inputStream) throws FileNotFoundException {
        Scanner input = new Scanner(inputStream);
        int n = input.nextInt();      // количество предметов
        int W = input.nextInt();      // вес рюкзака
        Item[] items = new Item[n];   // массив предметов
        for (int i = 0; i < n; i++) {
            items[i] = new Item(input.nextInt(), input.nextInt());
        }

        // Показываем предметы
        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);

        // Сортируем предметы по убыванию стоимости за единицу веса
        Arrays.sort(items);

        double result = 0;  // итоговая стоимость
        int remainingWeight = W;

        for (Item item : items) {
            if (remainingWeight == 0) break; // рюкзак заполнен

            if (item.weight <= remainingWeight) {
                // берем весь предмет
                result += item.cost;
                remainingWeight -= item.weight;
            } else {
                // берем часть предмета
                double fraction = (double) remainingWeight / item.weight;
                result += item.cost * fraction;
                remainingWeight = 0;
            }
        }

        System.out.printf("Удалось собрать рюкзак на сумму %f\n", result);
        return result;
    }

    private static class Item implements Comparable<Item> {
        int cost;
        int weight;
        double costPerWeight;

        Item(int cost, int weight) {
            this.cost = cost;
            this.weight = weight;
            this.costPerWeight = (double) cost / weight;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "cost=" + cost +
                    ", weight=" + weight +
                    ", costPerWeight=" + costPerWeight +
                    '}';
        }

        @Override
        public int compareTo(Item o) {
            // сортируем по убыванию стоимости за единицу веса
            return Double.compare(o.costPerWeight, this.costPerWeight);
        }
    }
}
