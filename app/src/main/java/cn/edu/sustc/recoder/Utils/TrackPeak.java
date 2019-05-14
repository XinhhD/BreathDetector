package cn.edu.sustc.recoder.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;

public class TrackPeak {

    public void distSeq(int[][] M, int[] range) {

        int rowLength = M.length;
        for (int i = 0; i < rowLength; i++) {
            int[] portion = Arrays.copyOfRange(M[i], range[0], range[1]);


        }


    }

    static class Result{

        int[] max;
        int[] min;

        Result(int[] max, int[] min){
            this.max = max;
            this.min = min;
        }

    }
    // //参数：数组，数组大小
    public static Result findPeaks(double[] num, int minDistance) {
        List<Integer> sign = new ArrayList<Integer>();
        for (int i = 1; i < num.length; i++) {
            /* 相邻值做差： *小于0，，赋-1 *大于0，赋1 *等于0，赋0 */
            double diff = num[i] - num[i - 1];
            if (diff > 0) {
                sign.add(1);
            } else if (diff < 0) {
                sign.add(-1);
            } else {
                sign.add(0);
            }
        }
        // 再对sign相邻位做差
        // 保存极大值和极小值的位置
        List<Integer> indMax = new ArrayList<Integer>();
        List<Integer> indMin = new ArrayList<Integer>();
        for (int j = 1; j < sign.size(); j++) {
            int diff = sign.get(j) - sign.get(j - 1);
            if (diff < 0) {
                indMax.add(j);
            } else if (diff > 0) {
                indMin.add(j);
            }
        }


        int[] max = new int[indMax.size()];
        for (int m = 0; m < indMax.size(); m++) {
            max[m] = indMax.get(m);
        }

        int[] min = new int[indMin.size()];
        for (int n = 0; n < indMin.size(); n++) {
            min[n] = indMin.get(n);
        }

        return new Result(max, min);

    }

    public static double get_score(double[] arr) {
        arr = IIRFilter(arr);
        Result res = findPeaks(arr, 0);
        int[] max = res.max;
        int[] min = res.min;
        if (max.length < 1 || min.length < 1) {
            System.out.println("xx");
        } else {
            int ind1 = 0;
            int ind2 = 0;
            int sum = 0;
            while (ind1 < max.length) {
                if (max[ind1] <= min[ind2]) {
                    ind1++;
                } else if (ind2 >= min.length - 1) {
                    sum += arr[max[ind1]] - arr[min[ind2]];
                    break;
                } else if (min[ind2 + 1] <= max[ind1]) {
                    ind2++;
                } else {
                    sum += arr[max[ind1]] - arr[min[ind2]];
                    ind2++;
                }
            }
            return sum;
        }
        return -1;
    }

    /**
     * just for 200*600
     * @param signal
     * @return
     */
    public static double[] IIRFilter(double[] signal) {
        double[] b = {0.0201, 0.0402, 0.0201};
        double[] a = {1, -1.5610, 0.6414};
        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            double y = 0.0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){

                y -= a[j + 1] * out[j];

            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;


        }
        return outData;
    }

}
