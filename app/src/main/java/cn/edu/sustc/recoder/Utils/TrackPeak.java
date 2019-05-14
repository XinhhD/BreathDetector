package cn.edu.sustc.recoder.Utils;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

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


  public static void distSeq(double[][] M, int[] range){
    long start = System.currentTimeMillis();
    long finished;
//    int rowLength = M.length;
    boolean first = true;
    for (double[] aM : M) {
      double[] portion = Arrays.copyOfRange(aM, range[0], range[range.length-1]);
      double[] xData = new double[range.length-1];
      for (int i = 0; i < range.length-1; i++) {
        xData[i] = (double) range[i];
      }

//      System.out.println(portion.length);
//      System.out.println(range.length);

      Spline spline = new Spline(xData, portion, 5*range.length);
      double[] xInt = spline.getIntX();
      double[] yInt = spline.getIntY();
//
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
//      List<Integer> pre_peaks = findPeaks(portion);
      List<Integer> pline_peaks = findPeaks(yInt);
      List<Double> peak_index = new ArrayList<Double>();
      for (Integer i:pline_peaks) {
        peak_index.add(xInt[i]);
//        System.out.print(xInt[i]);
//        System.out.print(',');
//        System.out.print(yInt[i]);
//        System.out.println();
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

//      break;
    }
    finished = System.currentTimeMillis();
    System.out.println("Process time: " + (finished-start) + " ms");
    XYChart chart3;
    XYChart chart4;
    for (int i = 0; i < peaks.size(); i++) {
      ArrayList<Double> tempt_peak = (ArrayList) peaks.get(i);
      double[] x = new double[tempt_peak.size()];
      for (int j = 0; j < x.length; j++) {
        x[j] = (double) j;
      }
      double[] y = new double[tempt_peak.size()];
      for (int j = 0; j < y.length; j++) {
        y[j] = tempt_peak.get(j);
      }
//      Spline spline = new Spline(x, y, 10*range.length);
//      double[] xInt = spline.getIntX();
//      double[] yInt = spline.getIntY();
      chart3 = QuickChart.getChart("Peak "+i, "X", "Y", "y(x)", x, y);
//      chart4 = QuickChart.getChart("Interpolated Peak "+i, "X", "Y", "y(x)", xInt, yInt);


      try {
        BitmapEncoder.saveBitmapWithDPI(chart3, "./img/peek" + i, BitmapEncoder.BitmapFormat.PNG, 300);
//        BitmapEncoder.saveBitmapWithDPI(chart4, "./img/peek_Interpolation" + i, BitmapEncoder.BitmapFormat.PNG, 300);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
//    XYChart chart3 = QuickChart.getChart("Spline Data", "X", "Y", "y(x)", xInt, yInt);
  }

   static class Result{

    ArrayList<Double> max;
    ArrayList<Double> min;

    Result(ArrayList<Double> max, ArrayList<Double> min){
      this.max = max;
      this.min = min;
    }

    public ArrayList<Double> getMax() {
      return max;
    }

    public ArrayList<Double> getMin() {
      return min;
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


    ArrayList<Double> max = new ArrayList<>();
    ArrayList<Double> min = new ArrayList<>();
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
        max.add(num[maxInd]);
        min.add(num[minInd]);
      }
    }
    while(indMax.size()!=0){
      max.add(num[((LinkedList<Integer>) indMax).pop()]);
    }

    while(indMin.size()!=0){
      min.add(num[((LinkedList<Integer>) indMin).pop()]);
    }
    System.out.println("max"+max.size());
    System.out.println("min"+min.size());
    return new Result(max, min);

  }


}
