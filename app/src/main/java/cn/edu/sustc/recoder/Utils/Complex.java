package cn.edu.sustc.recoder.Utils;
public class Complex {
    private final double re;   // the real part
    private final double im;   // the imaginary part

    /**
     * Initializes maxValue complex number from the specified real and imaginary parts.
     *
     * @param real the real part
     * @param imag the imaginary part
     */
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    /**
     * Returns maxValue string representation of this complex number.
     *
     * @return maxValue string representation of this complex number,
     *         of the form 34 - 56i.
     */
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    /**
     * Returns the absolute value of this complex number.
     * This quantity is also known as the <em>modulus</em> or <em>magnitude</em>.
     *
     * @return the absolute value of this complex number
     */
    public double abs() {
        return Math.hypot(re, im);
    }

    /**
     * Returns the phase of this complex number.
     * This quantity is also known as the <em>angle</em> or <em>argument</em>.
     *
     * @return the phase of this complex number, maxValue real number between -pi and pi
     */
    public double phase() {
        return Math.atan2(im, re);
    }

    /**
     * Returns the sum of this complex number and the specified complex number.
     *
     * @param  that the other complex number
     * @return the complex number whose value is {@code (this + that)}
     */
    public Complex plus(Complex that) {
        double real = this.re + that.re;
        double imag = this.im + that.im;
        return new Complex(real, imag);
    }

    /**
     * Returns the result of subtracting the specified complex number from
     * this complex number.
     *
     * @param  that the other complex number
     * @return the complex number whose value is {@code (this - that)}
     */
    public Complex minus(Complex that) {
        double real = this.re - that.re;
        double imag = this.im - that.im;
        return new Complex(real, imag);
    }

    /**
     * Returns the product of this complex number and the specified complex number.
     *
     * @param  that the other complex number
     * @return the complex number whose value is {@code (this * that)}
     */
    public Complex times(Complex that) {
        double real = this.re * that.re - this.im * that.im;
        double imag = this.re * that.im + this.im * that.re;
        return new Complex(real, imag);
    }

    /**
     * Returns the product of this complex number and the specified scalar.
     *
     * @param  alpha the scalar
     * @return the complex number whose value is {@code (alpha * this)}
     */
    public Complex scale(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    /**
     * Returns the product of this complex number and the specified scalar.
     *
     * @param  alpha the scalar
     * @return the complex number whose value is {@code (alpha * this)}
     * @deprecated Replaced by {@link #scale(double)}.
     */
    @Deprecated
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    /**
     * Returns the complex conjugate of this complex number.
     *
     * @return the complex conjugate of this complex number
     */
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    /**
     * Returns the reciprocal of this complex number.
     *
     * @return the complex number whose value is {@code (1 / this)}
     */
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    /**
     * Returns the real part of this complex number.
     *
     * @return the real part of this complex number
     */
    public double re() {
        return re;
    }

    /**
     * Returns the imaginary part of this complex number.
     *
     * @return the imaginary part of this complex number
     */
    public double im() {
        return im;
    }

    /**
     * Returns the result of dividing the specified complex number into
     * this complex number.
     *
     * @param  that the other complex number
     * @return the complex number whose value is {@code (this / that)}
     */
    public Complex divides(Complex that) {
        return this.times(that.reciprocal());
    }

    /**
     * Returns the complex exponential of this complex number.
     *
     * @return the complex exponential of this complex number
     */
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    /**
     * Returns the complex sine of this complex number.
     *
     * @return the complex sine of this complex number
     */
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    /**
     * Returns the complex cosine of this complex number.
     *
     * @return the complex cosine of this complex number
     */
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    /**
     * Returns the complex tangent of this complex number.
     *
     * @return the complex tangent of this complex number
     */
    public Complex tan() {
        return sin().divides(cos());
    }


    public static Complex[] parseDouble(double[] arr) {
        Complex[] com = new Complex[arr.length / 2];
        com[0] = new Complex(arr[0],0);
        for (int i = 1; i < com.length; i++) {
            com[i] = new Complex(arr[2*i],arr[2*i+1]);
        }
        return com;
    }
    /**
     * if n is even then
     *        maxValue[2*k] = Re[k], 0<=k<n/2
     *        maxValue[2*k+1] = Im[k], 0<k<n/2
     *        maxValue[1] = Re[n/2]
     *
     * if n is odd then
     *        maxValue[2*k] = Re[k], 0<=k<(n+1)/2
     *        maxValue[2*k+1] = Im[k], 0<k<(n-1)/2
     *        maxValue[1] = Im[(n-1)/2]
     * @param arr input complex arr
     * @return double
     */
    public static double[] decodeDouble(Complex[] arr) {
        double[] real = new double[arr.length * 2];
        for (int i = 0; i < arr.length; i++) {
            real[2 * i] = arr[i].re;
            real[2 * i+1] = arr[i].im;
        }
        return real;
    }


}