import java.util.Arrays;
import java.util.Random;

public class Main {
    /**
     * An O(n^2) version of `maxProduct` that gives the maximal value instead of the indexes
     *
     * @param an array of positive real numbers
     * @return the maximal value
     */

    public static int[] maxProduct(double[] x) {
        double[] tmp = new double[x.length];
        tmp[0] = x[0];

        double ans = tmp[0];
        int i = 0;
        int j = 0;
        int start = 0;

        for(int a = 1; a<x.length; a++) {
            tmp[a] = Math.max(x[a], tmp[a-1]*x[a]);
            if(tmp[a-1]*x[a] < x[a]) {
                start = a;
            }

            if(tmp[a]>ans) {
                ans = tmp[a];
                if(tmp[a-1]*x[a]>x[a]) {
                    i = start;
                    j = a;
                } else {
                    i = a;
                    j = a;
                }
            }
        }

        int[] output = new int[2];
        output[0] = i;
        output[1] = j;
        return output; // output = {i,j}
    }

    public static double maxProductBrute(double[] x) {
        double max = -1;
        for (int i = 0; i < x.length; i++) {
            double cur = 1;
            for (int j = i; j < x.length; j++) {
                cur *= x[j];
                max = Math.max(max, cur);
            }
        }
        return max;
    }


    /**
     * Internal
     */
    public static void test(double... x) {
        //System.out.println(Arrays.toString(x));
        int[] returned = maxProduct(x);
        double expected = maxProductBrute(x);
        double ans = 1;
        for (int i = returned[0]; i <= returned[1]; i++) {
            ans *= x[i];
        }
        if (ans == expected) {
//            System.out.println("PASSED");
        } else {
            System.out.println(Arrays.toString(x));
            System.out.println("expected product: " + expected);
            System.out.println("returned: " + Arrays.toString(returned));
            System.exit(1);
        }

    }

    public static void main(String[] args) {
        Random rand = new Random();
        while (true) {
            int size = 1 + rand.nextInt(5);
            double[] x = new double[size];
            for (int i = 0; i < size; i++) {
                x[i] = rand.nextDouble() * 3;
            }
            test(x);
        }
//        test(5, 2, 2, 0.1, 6, 10, 3, 0.2, 2);
//        test(2, 0.3, 3, 5, 6, 0.1, 2, 2, 5);
//        test(1, 0.2, 6, 3, 5, 0.5, 0.3, 2, 0.4, 6, 7, 3, 4, 6, 3, 0.2, 3, 6);
    }

}
