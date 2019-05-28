package cn.edu.sustc.recoder.Utils;
import static cn.edu.sustc.recoder.Utils.TrackPeak.distSeq;
import static org.junit.Assert.*;

import org.junit.Test;

public class ReadCsvTest {

  @Test
  public void readCsvFile() {
    double [][] matrix = ReadCsv.ReadCsvFile("/Users/aaron/Documents/GitHub/BreathDetector/app/src/main/java/cn/edu/sustc/recoder/Utils/matrix.csv");
    int [] range = new int[31];
    for (int i = 0; i < 31; i++) {
      range[i] = i+292;
    }
    distSeq(matrix,range);
  }
}