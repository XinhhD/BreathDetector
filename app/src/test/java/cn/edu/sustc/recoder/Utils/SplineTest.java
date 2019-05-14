package cn.edu.sustc.recoder.Utils;

import org.junit.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.IOException;

import static org.junit.Assert.*;

public class SplineTest {

    @Test
    public void drawTest(){

        double[] xData = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
        double[] yData = new double[]{0.677091943847625,-0.362834072225715,0.109645995499479,0.194986274078876,0.19038879853591,0.190847357273335,-0.161370351877833,-0.112041467135265,0.147410109494432,};

        Spline spline = new Spline(xData, yData, 90);
        double[] xInt = spline.getIntX();
        double[] yInt = spline.getIntY();

        System.out.print(xInt.length);

        // Create Chart
        XYChart chart1 = QuickChart.getChart("Raw Data", "X", "Y", "y(x)", xData, yData);

        XYChart chart2 = QuickChart.getChart("Raw Data", "X", "Y", "y(x)", xInt, yInt);

        try{
            BitmapEncoder.saveBitmapWithDPI(chart1, "./img/Before_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
            BitmapEncoder.saveBitmapWithDPI(chart2, "./img/After_Interpolation", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}