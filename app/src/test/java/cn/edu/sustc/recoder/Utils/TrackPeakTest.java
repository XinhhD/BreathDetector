package cn.edu.sustc.recoder.Utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class TrackPeakTest {

  @Test
  public void findPeaks() {
    int a[] = { 1, 2, 10, 2, 4, 1, 8, 10, 23, 0 };
    TrackPeak.findPeaks(a, 10);
  }
}