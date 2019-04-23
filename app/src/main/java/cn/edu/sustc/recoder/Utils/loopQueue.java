package cn.edu.sustc.recoder.Utils;

public class loopQueue<T> {
    public Object[] arr;
    int first;
    int last;
    public int count;
    boolean isReading;
    boolean isWriting;
    public loopQueue(int size){
        arr = new Object[size];
        first = 0;
        last = 0;
        count = 0;
    }

    /**
     * add all element to this queue of arr.
     * @param arr input arr
     * @throws ArrayStoreException if arr is full
     */
    public void add(T[] arr) throws ArrayStoreException{
        synchronized (this.arr) {
            if (arr.length > this.arr.length-count) {
                throw new ArrayStoreException("limited size: "+count+"<"+arr.length);
            }

            for (int i = 0; i < arr.length ; i++) {
                this.arr[last] = arr[i];
                last++;
                if (last >= arr.length) {
                    last = last%this.arr.length;
                }
            }
            addCount(arr.length);
        }
    }

    /**
     * add one element to this queue
     * @param e element
     * @throws ArrayStoreException if arr is full
     */
    public void add(T e) throws ArrayStoreException {
        synchronized (this.arr) {
            if (1 > this.arr.length-count) {
                throw new ArrayStoreException("limited size: "+count+"<"+arr.length);
            }
            this.arr[last++] = e;
            addCount(1);
        }
    }

    /**
     * fetch elements from this queue, do not delete.
     * region: [start,end]
     * @param start start index
     * @param end  end index
     * @return result array
     * @throws ArrayIndexOutOfBoundsException if fetch too much
     */
    public T[] fetch(int start, int end) throws ArrayIndexOutOfBoundsException{
        synchronized (this.arr) {
            int fetchLength = end-start+1;
            if (fetchLength>count) {
                throw new ArrayIndexOutOfBoundsException("fetch:"+fetchLength+">count:"+count);
            }

            Object[] res = new Object[fetchLength];
            for (int i = 0; i <fetchLength ; i++) {
                res[i] = this.arr[(first + i) % this.arr.length];
            }
            return (T[]) res;
        }
    }

    /**
     * remove one element
     * @throws IllegalAccessException if arr is empty
     */
    public void removeOne() throws IllegalAccessException {
        synchronized (this.arr) {
            if (count == 0) {
                throw new IllegalAccessException();
            }
            last--;
            if (last < 0) {
                last += this.arr.length;
                last %= this.arr.length;
            }
            addCount(-1);
        }
    }

    public synchronized void addCount(int value) {
        count = count + value;
    }



}
