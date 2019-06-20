package cn.edu.sustc.recoder.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.jtransforms.fft.DoubleFFT_1D;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import static cn.edu.sustc.recoder.Utils.Filtfilt.doFiltfilt;
import static java.lang.Math.abs;

public class TrackPeak {

  private static List<List<Double>> peaks= new ArrayList<>();

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

  public static List<Double> getBestCurve(){
    double maxScore = 0.0;
    boolean flag = true;
    List<Double> best = null;
    for (List<Double> peak:peaks) {
      double[]arr = new double[peak.size()];
      for (int i = 0; i < peak.size(); i++) {
        arr[i] = peak.get(i);
      }
      double score = get_score(arr);
      if (flag) {
        flag = !flag;
        maxScore = score;
        best = peak;
      } else if (score > maxScore){
        maxScore = score;
        best = peak;
      }
    }
    return best;
  }

  public static double distSeq(double[][] M, int[] range){
//    long start = System.currentTimeMillis();
//    long finished;
//    System.out.println(Arrays.toString(range));
    boolean first = true;
//    System.out.println(M[0].length);
    for (int i = 0; i < M[0].length; i++) {
      double[] portion = new double[range.length];
      for (int j = 0; j < range.length; j++) {
        portion[j] = M[range[j]][i];
      }
      double[] xData = new double[range.length];
      for (int j = 0; j < range.length; j++) {
        xData[j] = (double) range[j];
      }

//      System.out.println(Arrays.toString(portion));
//      System.out.println(Arrays.toString(xData));

      Spline spline = new Spline(xData, portion, 5*range.length);
      double[] xInt = spline.getIntX();
      double[] yInt = spline.getIntY();
//      if (first) {
//        XYChart chart1 = QuickChart.getChart("Raw Data", "X", "Y", "y(x)", xData, portion);
//
//        XYChart chart2 = QuickChart.getChart("Spline Data", "X", "Y", "y(x)", xInt, yInt);
//
//        try {
//          BitmapEncoder.saveBitmapWithDPI(chart1, "./img/Before_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
//          BitmapEncoder.saveBitmapWithDPI(chart2, "./img/After_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
      List<Integer> pline_peaks = findPeaks(yInt,0.001).getMax();
      List<Double> peak_index = new ArrayList<Double>();
      for (Integer ii:pline_peaks) {
        peak_index.add(xInt[ii]);
      }
      if (first){
        first = false;
        for (Double p_i:peak_index){
          List<Double> tempt = new ArrayList<>();
          tempt.add(p_i);
          peaks.add(tempt);
        }
      } else{
        for (Double p_i:peak_index) {
          for (List<Double> peak:peaks) {
            double last_peak = peak.get(peak.size()-1);
            if (Math.abs(last_peak-p_i)<3.0){
              peak.add(p_i);
              break;
            }
          }
        }
      }
    }
//    finished = System.currentTimeMillis();
//    System.out.println("Process time: " + (finished-start) + " ms");
    XYChart chart3;
//    for (int i = 0; i < peaks.size(); i++) {
      List<Double> tempt_peak = getBestCurve();
//          (ArrayList) peaks.get(i);
      double[] x = new double[tempt_peak.size()];
      for (int j = 0; j < x.length; j++) {
        x[j] = (double) j;
      }
      double[] y = new double[tempt_peak.size()];
      for (int j = 0; j < y.length; j++) {
        y[j] = tempt_peak.get(j);
      }
      double[] filt_y = IIRFilter(y);

//      chart3 = QuickChart.getChart("Peak best", "X", "Y", "y(x)", x, filt_y);
//
//      try {
//        BitmapEncoder.saveBitmapWithDPI(chart3, "./img/peek_best", BitmapEncoder.BitmapFormat.PNG, 300);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
////    }

//    double[] peak = new double[tempt_peak.size()];
//    for (int i = 0; i < tempt_peak.size(); i++) {
//      peak[i] = tempt_peak.get(i);
//    }
//    Complex[] result = FFT.fft(peak);
//    double[] abs = new double[result.length];
//    double max = 0.0;
//    for (int i = 0; i < result.length; i++) {
//      abs[i] = result[i].abs();
//      if (abs[i] > max) {
//        max = abs[i];
//      }
//    }
    Result res = findPeaks(filt_y, 0);
    /*
    first case:  \/
    second case: \/\
    third case: /\
    fourth case: /\/
     */
    if (abs(res.max.size() - res.min.size()) > 1) {
      return -1;
    }else{
      int min = res.max.size()>res.min.size() ? res.min.size():res.max.size();
      double count = min + 0.5*abs(res.max.size() - res.min.size());
      return count;
    }


  }
  public static double[][] distSeqPlot(double[][] M, int[] range){
//    long start = System.currentTimeMillis();
//    long finished;
//    System.out.println(Arrays.toString(range));
    boolean first = true;
//    System.out.println(M[0].length);
    for (int i = 0; i < M[0].length; i++) {
      double[] portion = new double[range.length];
      for (int j = 0; j < range.length; j++) {
        portion[j] = M[range[j]][i];
      }
      double[] xData = new double[range.length];
      for (int j = 0; j < range.length; j++) {
        xData[j] = (double) range[j];
      }

//      System.out.println(Arrays.toString(portion));
//      System.out.println(Arrays.toString(xData));

      Spline spline = new Spline(xData, portion, 5*range.length);
      double[] xInt = spline.getIntX();
      double[] yInt = spline.getIntY();
//      if (first) {
//        XYChart chart1 = QuickChart.getChart("Raw Data", "X", "Y", "y(x)", xData, portion);
//
//        XYChart chart2 = QuickChart.getChart("Spline Data", "X", "Y", "y(x)", xInt, yInt);
//
//        try {
//          BitmapEncoder.saveBitmapWithDPI(chart1, "./img/Before_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
//          BitmapEncoder.saveBitmapWithDPI(chart2, "./img/After_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
      List<Integer> pline_peaks = findPeaks(yInt,0.001).getMax();
      List<Double> peak_index = new ArrayList<Double>();
      for (Integer ii:pline_peaks) {
        peak_index.add(xInt[ii]);
      }
      if (first){
        first = false;
        for (Double p_i:peak_index){
          List<Double> tempt = new ArrayList<>();
          tempt.add(p_i);
          peaks.add(tempt);
        }
      } else{
        for (Double p_i:peak_index) {
          for (List<Double> peak:peaks) {
            double last_peak = peak.get(peak.size()-1);
            if (Math.abs(last_peak-p_i)<3.0){
              peak.add(p_i);
              break;
            }
          }
        }
      }
    }
//    finished = System.currentTimeMillis();
//    System.out.println("Process time: " + (finished-start) + " ms");
    XYChart chart3;
//    for (int i = 0; i < peaks.size(); i++) {
    List<Double> tempt_peak = getBestCurve();
//          (ArrayList) peaks.get(i);
    double[] x = new double[tempt_peak.size()];
    for (int j = 0; j < x.length; j++) {
      x[j] = (double) j;
    }
    double[] y = new double[tempt_peak.size()];
    for (int j = 0; j < y.length; j++) {
      y[j] = tempt_peak.get(j);
    }
    double[] filt_y = IIRFilter(y);

//      chart3 = QuickChart.getChart("Peak best", "X", "Y", "y(x)", x, filt_y);
//
//      try {
//        BitmapEncoder.saveBitmapWithDPI(chart3, "./img/peek_best", BitmapEncoder.BitmapFormat.PNG, 300);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
////    }

//    double[] peak = new double[tempt_peak.size()];
//    for (int i = 0; i < tempt_peak.size(); i++) {
//      peak[i] = tempt_peak.get(i);
//    }
//    Complex[] result = FFT.fft(peak);
//    double[] abs = new double[result.length];
//    double max = 0.0;
//    for (int i = 0; i < result.length; i++) {
//      abs[i] = result[i].abs();
//      if (abs[i] > max) {
//        max = abs[i];
//      }
//    }
    Result res = findPeaks(filt_y, 0);
    double count = 0;
    /*
    first case:  \/
    second case: \/\
    third case: /\
    fourth case: /\/
     */
    if (abs(res.max.size() - res.min.size()) > 1) {

    }else{
      int min = res.max.size()>res.min.size() ? res.min.size():res.max.size();
      count = min + 0.5*abs(res.max.size() - res.min.size());
    }
    double[][] arrs= {filt_y,{count}};
    return arrs;


  }
  static class Result{

    ArrayList<Integer> max;
    ArrayList<Integer> min;

    Result(ArrayList<Integer> max, ArrayList<Integer> min){
      this.max = max;
      this.min = min;
    }

    public ArrayList<Integer> getMax() {
      return max;
    }

    public ArrayList<Integer> getMin() {
      return min;
    }

    public int[] getMaxArray() {
      int[] max_arr = new int[max.size()];
      for (int i = 0; i < max.size(); i++) {
        max_arr[i] = max.get(i);
      }
      return max_arr;
    }

    public int[] getMinArray() {
      int[] min_arr = new int[min.size()];
      for (int i = 0; i < min.size(); i++) {
        min_arr[i] = min.get(i);
      }
      return min_arr;
    }

  }
  // //参数：数组，数组大小
  public static Result findPeaks(double[] num, double minDistance) {

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
    List<Integer> indMax = new LinkedList<>();
    List<Integer> indMin = new LinkedList<>();
    for (int j = 1; j < sign.size(); j++) {
      int diff = sign.get(j) - sign.get(j - 1);
      if (diff < 0) {
        indMax.add(j);
      } else if (diff > 0) {
        indMin.add(j);
      }
    }


    ArrayList<Integer> max = new ArrayList<>();
    ArrayList<Integer> min = new ArrayList<>();
    int minIndex = 0;
    if(indMax.size()>indMin.size()){
      minIndex = indMin.size();
    }else{
      minIndex = indMax.size();
    }
    for (int m = 0; m < minIndex; m++) {
      int maxInd = ((LinkedList<Integer>) indMax).pop();
      int minInd = ((LinkedList<Integer>) indMin).pop();
      if (num[maxInd] - num[minInd] >= minDistance){
        max.add(maxInd);
        min.add(minInd);
      }
    }
    while(indMax.size()!=0){
      max.add(((LinkedList<Integer>) indMax).pop());
    }

    while(indMin.size()!=0){
      min.add(((LinkedList<Integer>) indMin).pop());
    }
    return new Result(max, min);

  }

    public static double get_score(double[] arr) {
//        new Grawer(arr, "fig1", "arr").show();
        arr = IIRFilter(arr);
//        new Grawer(arr, "fig1", "arr").show();
        Result res = findPeaks(arr, 0);
        int[] max = res.getMaxArray();
        int[] min = res.getMinArray();
        if (max.length < 1 || min.length < 1) {

        } else {
            int ind1 = 0;
            int ind2 = 0;
            double sum = 0;
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
        return 0;
    }

    /**
     * just for 200*600
     *
     * @param signal
     * @return
     */
    public static double[] IIRFilter(double[] signal) {
        double[] b = {0.0201, 0.0402, 0.0201};
        double[] a = {1, -1.5610, 0.6414};
        ArrayList<Double> B = toAL(b);
        ArrayList<Double> A = toAL(a);
        ArrayList<Double> input = toAL(signal);
        Object[] outData = doFiltfilt(B, A, input).toArray();
        double[] out = new double[signal.length];
        for (int i = 0; i <signal.length ; i++) {
            out[i] = (double)outData[i];
        }
        return out;
    }

    private static ArrayList<Double> toAL(double[] arr) {
        ArrayList<Double> B = new ArrayList<Double>();
        for (int i = 0; i < arr.length; i++) {
            B.add(arr[i]);
        }
        return B;
    }


}
