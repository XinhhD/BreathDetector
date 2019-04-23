package cn.edu.sustc.recoder.Utils;

/**
 * This class can add and delete at the same time
 */
public interface ConcurrentList {
    /**
     * remove one element of the head
     */
    public void removeOne();

    /**
     * note: internal arr should be double
     * append a buffer to this list
     * @param buf input buffer (should be cast to double)
     */
    public void addBuffer(short[] buf);
    /**
     * note: internal arr should be double
     * append a buffer to this list
     * @param buf input buffer (should be cast to double)
     */
    public void addBuffer(byte[] buf);
    /**
     * add one element
     * @param e
     */
    public void add(short e);
    /**
     * add one element
     * @param e
     */
    public void add(byte e);
    /**
     * get the element of index
     * @param index
     * @return
     */
    public double get(int index);

    /**
     * get ALL element from [start,end]
     * @param start
     * @param end
     * @return
     */
    public double getAll(int start, int end);

    /**
     * total number in this list
     * @return
     */
    public int count();
}
