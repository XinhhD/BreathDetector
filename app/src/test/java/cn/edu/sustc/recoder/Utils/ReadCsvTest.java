package cn.edu.sustc.recoder.Utils;

import static cn.edu.sustc.recoder.Utils.TrackPeak.distSeq;
import static org.junit.Assert.*;

import org.junit.Test;

public class ReadCsvTest {

  @Test
  public void readCsvFile() {
    double [][] matrix = ReadCsv.ReadCsvFile("/Users/aaron/Documents/GitHub/BreathDetector/app/src/main/java/cn/edu/sustc/recoder/Utils/matrix.csv");
    int [] range = {276,277,278	,279	,280	,281	,282	,283	,284	,285	,286	,287	,288	,289	,290	,291	,292	,293	,294	,295	,296	,297	,298	,299	,300	,301	,302	,303	,304	,305	,306	,307	,308	,309	,310	,311	,312	,313	,314	,315	,316};
    distSeq(matrix,range);
  }
}