package cn.edu.sustc.recoder.Utils;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import static cn.edu.sustc.recoder.Utils.TrackPeak.IIRFilter;
import static cn.edu.sustc.recoder.Utils.TrackPeak.findPeaks;
import static cn.edu.sustc.recoder.Utils.xcorr.get_range;

public class Grawer {
    private XYChart chart;
    public Grawer(double[] values,String FigName,String funcName) {

        double[] yData = values;
        double[] xData = new double[yData.length];
        for (int i = 0; i < xData.length; i++) {
            xData[i] = i;
        }
        // Create Chart
        chart = QuickChart.getChart(FigName, "X", "Y", funcName, xData, yData);
    }
    public Grawer(double[] values,String FigName,String xName,String yName,String funcName) {

        double[] yData = values;
        double[] xData = new double[yData.length];
        for (int i = 0; i < xData.length; i++) {
            xData[i] = i;
        }
        // Create Chart
        chart = QuickChart.getChart(FigName, xName, yName, funcName, xData, yData);
    }

    public void show() {
        // Show it
//        new SwingWrapper(chart).displayChart();
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        double[] s11 = ReadCsv.ReadCsv("D:\\studioProject\\Recoder\\app\\src\\test\\java\\cn\\edu\\sustc\\recoder\\rcv.csv");
        double[] s22 = ReadCsv.ReadCsv("D:\\studioProject\\Recoder\\app\\src\\test\\java\\cn\\edu\\sustc\\recoder\\org.csv");
        new Grawer(s11, "fig1", "rec").show();
        new Grawer(s22, "fig2", "org").show();
        double[] rel = FFT.xcorr(s11, s22, true);
        int index = Util.max(rel).index;
        System.out.println(index);
        System.out.println(System.currentTimeMillis()-time);
    }
}
