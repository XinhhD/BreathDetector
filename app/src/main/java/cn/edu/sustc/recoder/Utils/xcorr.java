package cn.edu.sustc.recoder.Utils;

import android.util.Log;

import org.jtransforms.fft.*;

import java.util.LinkedList;
import java.util.Queue;

import static android.content.ContentValues.TAG;
import static cn.edu.sustc.recoder.Utils.FFT.complex2real;
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


}
