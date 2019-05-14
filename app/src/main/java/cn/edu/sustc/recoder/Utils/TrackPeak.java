package cn.edu.sustc.recoder.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TrackPeak {

  public void distSeq(int[][] M, int[] range){

    int rowLength = M.length;
    for (int i = 0; i <rowLength ; i++) {
      int[] portion = Arrays.copyOfRange(M[i], range[0], range[1]);


    }


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
