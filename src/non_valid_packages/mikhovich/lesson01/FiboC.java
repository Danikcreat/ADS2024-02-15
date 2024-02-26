package non_valid_packages.mikhovich.lesson01;

/*
 * Даны целые числа 1<=n<=1E18 и 2<=m<=1E5,
 * необходимо найти остаток от деления n-го числа Фибоначчи на m.
 * время расчета должно быть не более 2 секунд
 */

public class FiboC {

    private long startTime = System.currentTimeMillis();

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        int n = 10;
        int m = 2;
        System.out.printf("fasterC(%d)=%d \n\t time=%d \n\n", n, fibo.fasterC(n, m), fibo.time());
    }

    public static long getPisanoPeriod(int ni) {
        long previous = 0;
        long current = 1;
        long pisano = 0;
        for (int i = 0; i < ni * ni; i++) {
            long temp = current;
            current = (previous + current) % ni;
            previous = temp;

            if (previous == 0 && current == 1) {
                return i + 1;
            }
        }
        return pisano;
    }

    public static long findMod(long n, int ni) {
        long previous = 0;
        long current = 1;
        for (int i = 0; i < n; i++) {
            long temp = current;
            current = (previous + current) % ni;
            previous = temp;
        }
        return previous % ni;
    }

    public static long fasterC(long n, int ni) {
        //Решение сложно найти интуитивно
        //возможно потребуется дополнительный поиск информации
        //см. период Пизано
        long pisano = getPisanoPeriod(ni);
        n %= pisano;
        if (n == 0L || n == 1L) {
            return n;
        }
        return findMod(n, ni);
    }
}

