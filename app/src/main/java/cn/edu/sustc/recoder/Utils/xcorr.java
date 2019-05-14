package cn.edu.sustc.recoder.Utils;

import android.util.Log;

import org.jtransforms.fft.*;

import java.util.LinkedList;
import java.util.Queue;

import static android.content.ContentValues.TAG;
import static cn.edu.sustc.recoder.Utils.FFT.complex2real;
import static cn.edu.sustc.recoder.Utils.TrackPeak.get_score;

import cn.edu.sustc.recoder.Utils.FFT.*;

public class xcorr {
    public static void xcorr(LinkedList rec, LinkedList raw) {
        double[] ar1 = (double[]) rec.poll();
        double[] ar2 = (double[]) raw.poll();
        double[] rel = FFT.xcorr(ar1,ar2,true);
        int index = Util.max(rel).index;
        rec.subList(0,index).clear();
        Log.d(TAG, "xcorr: ");
        assert rec.size() == raw.size();
    }

    public static double[][] CIR(double[] x, double[] y) {
        double[][] CIR = new double[200][x.length];
        for (int j = 0; j <200; j++) {
            Complex[] X = FFT.fft(x);
            Complex[] Y = FFT.fft(y);
            Complex[] H = new Complex[X.length];
            for (int i = 0; i < X.length; i++) {
                H[i] = Y[i].divides(X[i]);
            }
            double[] h_raw = new double[H.length];
            Complex[] hh = FFT.ifft(H);
            for (int i = 0; i <H.length ; i++) {
                h_raw[i] = hh[i].re();
            }
            CIR[j] = h_raw;
        }
        return CIR;
    }

    public static int[] get_range(double[][] CIR_matrix) {
        double[] autocorr_score = new double[600];
        int index = 0;
        for (double[]row: CIR_matrix
             ) {
            autocorr_score[index++] = get_score(autoCorr(row));
        }
        return get_good_range(autocorr_score);
    }


    /***
     * only for real number
     * @param x
     * @return
     */
    public static double[] autoCorr(double[]x){
        double[] ans = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            int sum = 0;
            for (int j = i ; j < x.length; j++) {
                sum += x[j] * x[j - i];
            }
            ans[i]=sum;
        }
        return ans;
    }
    private static int GOOD_RANGE = 40;
    public static int[] get_good_range(double[] auto_score){
        int[] range = new int[GOOD_RANGE];
        int start = 0;
        int end = 0;
        double average = 0.0;
        double sum = 0.0;
        int count = 0;


        for (int i = 0; i < auto_score.length; i++) {
            if (auto_score[i] == 0)
                continue;
            else {
                if (i < 50) // 一般来说前50太近不可能是胸
                {
                    count++;
                    sum+=auto_score[i];
                    average=sum/((double)count);
                } else{
                    if (auto_score[i] > average * 3) {
                        if (start == 0)
                            start=i;
                        else
                            end = i;
                    } else{
                        count++;
                        sum+=auto_score[i];
                        average=sum/((double)count);
                    }
                    if (start!=0&&i-start>50){
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < GOOD_RANGE; i++) {
            range[i] = start;
            start++;
            if (start>=end)
                break;
        }

        return range;
    }


}
