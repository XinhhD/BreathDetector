package cn.edu.sustc.recoder.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadCsv {
    public static double[][] ReadCsvFile(String filePath) {
        double[][] result = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));//换成你的文件名
            String line = null;
            ArrayList<String[]> store = new ArrayList<String[]>();

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                store.add(item);
            }
            result = new double[store.get(0).length][store.size()];
            for (int i = 0; i < store.size(); i++) {
                String[] temp = store.get(i);
                for (int j = 0; j < temp.length; j++) {
                    result[j][i] = Double.valueOf(temp[j]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double[] ReadCsv(String filePath) {
        double[] result = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));//换成你的文件名
            String line = null;
            ArrayList<String[]> store = new ArrayList<String[]>();

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                store.add(item);
            }
            if (store.size() == 1) {
                result = new double[store.get(0).length];
                String[] temp = store.get(0);
                for (int i = 0; i < temp.length; i++) {
                    result[i] = Double.valueOf(temp[i]);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
