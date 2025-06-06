package by.it.group410971.kozich.lesson01;

public class FiboC {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        long n = 55555L;
        int m = 1000;
        System.out.printf("fasterC(%d, %d)=%d \n\t time=%d \n\n", n, m, fibo.fasterC(n, m), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    long fasterC(long n, int m) {
        if (n <= 1) return n;

        // Найдем период Пизано для m
        int pisanoPeriod = getPisanoPeriod(m);

        // Сократим n по модулю периода
        n = n % pisanoPeriod;

        // Вычислим n-е число Фибоначчи по модулю m
        return fibonacciMod(n, m);
    }

    // Метод для вычисления периода Пизано
    private int getPisanoPeriod(int m) {
        int prev = 0;
        int curr = 1;
        int period = 0;

        for (int i = 0; i < m * m; i++) {
            int temp = (prev + curr) % m;
            prev = curr;
            curr = temp;

            // Период начинается с 0, 1
            if (prev == 0 && curr == 1) {
                period = i + 1;
                break;
            }
        }
        return period;
    }

    // Быстрое вычисление n-го числа Фибоначчи по модулю m (итеративно)
    private long fibonacciMod(long n, int m) {
        if (n <= 1) return n;

        long prev = 0;
        long curr = 1;

        for (long i = 2; i <= n; i++) {
            long temp = (prev + curr) % m;
            prev = curr;
            curr = temp;
        }
        return curr;
    }
}
