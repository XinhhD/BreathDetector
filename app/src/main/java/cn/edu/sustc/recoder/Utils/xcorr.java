package cn.edu.sustc.recoder.Utils;

import android.util.Log;

import org.jtransforms.fft.*;

import java.util.LinkedList;
import java.util.Queue;

import static android.content.ContentValues.TAG;

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
}
