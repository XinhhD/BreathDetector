package cn.edu.sustc.recoder.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadCsv {
  public static double[][] ReadCsvFile(String filePath){
      double[][] result = null;
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));//换成你的文件名
      String line = null;
      ArrayList<String[]> store = new ArrayList<String[]>();

      while((line=reader.readLine())!=null){
        String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
        store.add(item);
      }
      result = new double[store.get(0).length][store.size()];
      for(int i=0; i<store.size(); i++){
        String[] temp = store.get(i);
        for (int j = 0; j <temp.length ; j++) {
          result[j][i] = Double.valueOf(temp[j]);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

//    System.out.println(result[2][3]);
    return result;

  }
}
