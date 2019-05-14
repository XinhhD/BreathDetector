package cn.edu.sustc.recoder.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackPeak {

  public void distSeq(int[][] M, int[] range){

    int rowLength = M.length;
    for (int i = 0; i <rowLength ; i++) {
      int[] portion = Arrays.copyOfRange(M[i], range[0], range[1]);


    }


  }

   static class Result{

    double[] max;
    double[] min;

    Result(double[] max, double[] min){
      this.max = max;
      this.min = min;
    }

    public double[] getMax() {
      return max;
    }

    public double[] getMin() {
      return min;
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


    double[] max = new double[indMax.size()];
    for (int m = 0; m < indMax.size(); m++) {
      max[m] = num[indMax.get(m)];
    }

    double[] min = new double[indMin.size()];
    for (int n = 0; n < indMin.size(); n++) {
      min[n] = num[indMin.get(n)];
    }

    return new Result(max, min);

  }


}
