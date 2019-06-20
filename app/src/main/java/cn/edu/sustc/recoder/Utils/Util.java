package cn.edu.sustc.recoder.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Util {
    public static class couple {
        public double maxValue;
        public int index;
        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        couple(double a, int b) {
            this.maxValue = a;
            this.index = b;
        }


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }


    }
    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    public static couple max(double[] arr) {
        double max = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                index = i;
            }
        }
        return new couple(max,index);
    }

}