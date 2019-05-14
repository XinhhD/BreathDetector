package cn.edu.sustc.recoder.Utils;
import java.io.*;

import static cn.edu.sustc.recoder.Utils.FFT.complex2real;
import static cn.edu.sustc.recoder.Utils.FFT.fft;
import static cn.edu.sustc.recoder.Utils.FFT.ifft;

public class WavReader {

    public static void main(String[] args)
    {
        try
        {

            // Open the wav file specified as the first argument
            WavFile wavFile1 = WavFile.openWavFile(new File("D:\\studioProject\\Recoder\\app\\src\\main\\java\\cn\\edu\\sustc\\recoder\\Utils\\sig18k_5s.wav"));
//            WavFile wavFile2 = WavFile.openWavFile(new File(args[1]));

            // Display information about the wav file
            wavFile1.display();
//            wavFile2.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile1.getNumChannels();

            // Create a buffer of 100 frames
            double[] buffer = new double[19200 * numChannels];

            int framesRead1;
//            int framesRead2;
//            double min = Double.MAX_VALUE;
//            double max = Double.MIN_VALUE;

            do
            {
                // Read frames into buffer
                framesRead1 = wavFile1.readFrames(buffer, 19200);

                // Loop through frames and look for minimum and maximum value
//                for (int s=0 ; s<framesRead * numChannels ; s++)
//                {
//                    if (buffer[s] > max) max = buffer[s];
//                    if (buffer[s] < min) min = buffer[s];
//                }
            }
            while (framesRead1 != 0);
//            Complex[] result = fft()
            long startTime = System.currentTimeMillis();
            double[] x = buffer.clone();
            double[] y = buffer.clone();
            double[][] cir = xcorr.CIR(x, y);

            long endTime = System.currentTimeMillis();
            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
            // Close the wavFile
            wavFile1.close();

            // Output the minimum and maximum value
//            System.out.printf("Min: %f, Max: %f\n", min, max);
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
}