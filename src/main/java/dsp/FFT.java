package dsp;

/*******************************************************************************
 * Compilation: javac FFT.java<br>
 * Execution: java FFT N<br>
 * Dependencies: Complex.java<br>
 * <br>
 * Compute the FFT and inverse FFT of a length N complex sequence.<br>
 * Bare bones implementation that runs in O(N log N) time. Our goal<br>
 * is to optimize the clarity of the code, rather than performance.<br>
 * <br>
 * Limitations<br>
 * -----------<br> - assumes N is a power of 2<br>
 * <br> - not the most memory efficient algorithm (because it uses<br>
 * an object type for representing complex numbers and because<br>
 * it re-allocates memory for the subarray, instead of doing<br>
 * in-place or reusing a single temporary array)<br>
 * <br>
 ******************************************************************************/

public class FFT {

	// compute the FFT of x[], assuming its length is a power of 2
	public static Complex[] fft(Complex[] x) {
		int N = x.length;

		// base case
		if (N == 1)
			return new Complex[] { x[0] };

		// radix 2 Cooley-Tukey FFT
		if (N % 2 != 0)
			throw new RuntimeException("N is not a power of 2");

		// fft of even terms
		Complex[] even = new Complex[N / 2];
		for (int k = 0; k < N / 2; k++)
			even[k] = x[2 * k];
		Complex[] q = fft(even);

		// fft of odd terms
		Complex[] odd = even; // reuse the array
		for (int k = 0; k < N / 2; k++)
			odd[k] = x[2 * k + 1];
		Complex[] r = fft(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N / 2; k++) {
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].plus(wk.times(r[k]));
			y[k + N / 2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}

	// compute the inverse FFT of x[], assuming its length is a power of 2
	public static Complex[] ifft(Complex[] x) {
		int N = x.length;
		Complex[] y = new Complex[N];

		// take conjugate
		for (int i = 0; i < N; i++)
			y[i] = x[i].conjugate();

		// compute forward FFT
		y = fft(y);

		// take conjugate again
		for (int i = 0; i < N; i++)
			y[i] = y[i].conjugate();

		// divide by N
		for (int i = 0; i < N; i++)
			y[i] = y[i].times(1.0 / N);

		return y;

	}

	// compute the circular convolution of x and y
	public static Complex[] cconvolve(Complex[] x, Complex[] y) {

		// should probably pad x and y with 0s so that they have same length
		// and are powers of 2
		if (x.length != y.length)
			throw new RuntimeException("Dimensions don't agree");

		int N = x.length;

		// compute FFT of each sequence
		Complex[] a = fft(x);
		Complex[] b = fft(y);

		// point-wise multiply
		Complex[] c = new Complex[N];
		for (int i = 0; i < N; i++)
			c[i] = a[i].times(b[i]);

		// compute inverse FFT
		return ifft(c);
	}

	// compute the linear convolution of x and y
	public static Complex[] convolve(Complex[] x, Complex[] y) {
		Complex ZERO = new Complex(0, 0);

		Complex[] a = new Complex[2 * x.length];
		for (int i = 0; i < x.length; i++)
			a[i] = x[i];
		for (int i = x.length; i < 2 * x.length; i++)
			a[i] = ZERO;

		Complex[] b = new Complex[2 * y.length];
		for (int i = 0; i < y.length; i++)
			b[i] = y[i];
		for (int i = y.length; i < 2 * y.length; i++)
			b[i] = ZERO;

		return cconvolve(a, b);
	}

	// test client
	public static void main(String[] args) {
		int N = Integer.parseInt(args[0]);
		Complex[] x = new Complex[N];

		// original data
		for (int i = 0; i < N; i++) {
			x[i] = new Complex(i, 0);
			x[i] = new Complex(-2 * Math.random() + 1, 0);
		}
		System.out.println("x");
		System.out.println("-------------------");
		for (int i = 0; i < N; i++)
			System.out.println(x[i]);
		System.out.println();

		// FFT of original data
		Complex[] y = fft(x);
		System.out.println("y = fft(x)");
		System.out.println("-------------------");
		for (int i = 0; i < N; i++)
			System.out.println(y[i]);
		System.out.println();

		// take inverse FFT
		Complex[] z = ifft(y);
		System.out.println("z = ifft(y)");
		System.out.println("-------------------");
		for (int i = 0; i < N; i++)
			System.out.println(z[i]);
		System.out.println();

		// circular convolution of x with itself
		Complex[] c = cconvolve(x, x);
		System.out.println("c = cconvolve(x, x)");
		System.out.println("-------------------");
		for (int i = 0; i < N; i++)
			System.out.println(c[i]);
		System.out.println();

		// linear convolution of x with itself
		Complex[] d = convolve(x, x);
		System.out.println("d = convolve(x, x)");
		System.out.println("-------------------");
		for (int i = 0; i < d.length; i++)
			System.out.println(d[i]);
		System.out.println();

	}

	/***************************************************************************
	 * Compilation: javac Complex.java<br>
	 * Execution: java Complex<br>
	 * <br>
	 * Data type for complex numbers.<br>
	 * <br>
	 * The data type is "immutable" so once you create and initialize<br>
	 * a Complex object, you cannot change it. The "final" keyword<br>
	 * when declaring re and im enforces this rule, making it a<br>
	 * compile-time error to change the .re or .im fields after<br>
	 * they've been initialized.<br>
	 * <br> % java Complex<br>
	 * a = 5.0 + 6.0i<br>
	 * b = -3.0 + 4.0i<br>
	 * Re(a) = 5.0<br>
	 * Im(a) = 6.0<br>
	 * b + a = 2.0 + 10.0i<br>
	 * a - b = 8.0 + 2.0i<br>
	 * a * b = -39.0 + 2.0i<br>
	 * b * a = -39.0 + 2.0i<br>
	 * a / b = 0.36 - 1.52i<br>
	 * (a / b) * b = 5.0 + 6.0i<br>
	 * conj(a) = 5.0 - 6.0i<br>
	 * |a| = 7.810249675906654<br>
	 * tan(a) = -6.685231390246571E-6 + 1.0000103108981198i<br>
	 * 
	 **************************************************************************/

	public static class Complex {
		private final double re; // the real part
		private final double im; // the imaginary part

		// create a new object with the given real and imaginary parts
		public Complex(double real, double imag) {
			re = real;
			im = imag;
		}

		// return a string representation of the invoking Complex object
		public String toString() {
			if (im == 0)
				return re + "";
			if (re == 0)
				return im + "i";
			if (im < 0)
				return re + " - " + (-im) + "i";
			return re + " + " + im + "i";
		}

		// return abs/modulus/magnitude and angle/phase/argument
		public double abs() {
			return Math.hypot(re, im);
		} // Math.sqrt(re*re + im*im)

		public double phase() {
			return Math.atan2(im, re);
		} // between -pi and pi

		// return a new Complex object whose value is (this + b)
		public Complex plus(Complex b) {
			Complex a = this; // invoking object
			double real = a.re + b.re;
			double imag = a.im + b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this - b)
		public Complex minus(Complex b) {
			Complex a = this;
			double real = a.re - b.re;
			double imag = a.im - b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this * b)
		public Complex times(Complex b) {
			Complex a = this;
			double real = a.re * b.re - a.im * b.im;
			double imag = a.re * b.im + a.im * b.re;
			return new Complex(real, imag);
		}

		// scalar multiplication
		// return a new object whose value is (this * alpha)
		public Complex times(double alpha) {
			return new Complex(alpha * re, alpha * im);
		}

		// return a new Complex object whose value is the conjugate of this
		public Complex conjugate() {
			return new Complex(re, -im);
		}

		// return a new Complex object whose value is the reciprocal of this
		public Complex reciprocal() {
			double scale = re * re + im * im;
			return new Complex(re / scale, -im / scale);
		}

		// return the real or imaginary part
		public double Re() {
			return re;
		}

		public double Im() {
			return im;
		}

		// return a / b
		public Complex divides(Complex b) {
			Complex a = this;
			return a.times(b.reciprocal());
		}

		// return a new Complex object whose value is the complex exponential of
		// this
		public Complex exp() {
			return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
		}

		// return a new Complex object whose value is the complex sine of this
		public Complex sin() {
			return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
		}

		// return a new Complex object whose value is the complex cosine of this
		public Complex cos() {
			return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
		}

		// return a new Complex object whose value is the complex tangent of
		// this
		public Complex tan() {
			return sin().divides(cos());
		}

		// a static version of plus
		public static Complex plus(Complex a, Complex b) {
			double real = a.re + b.re;
			double imag = a.im + b.im;
			Complex sum = new Complex(real, imag);
			return sum;
		}

		// sample client for testing
		// public static void main(String[] args) {
		// Complex a = new Complex(5.0, 6.0);
		// Complex b = new Complex(-3.0, 4.0);
		//
		// System.out.println("a = " + a);
		// System.out.println("b = " + b);
		// System.out.println("Re(a) = " + a.Re());
		// System.out.println("Im(a) = " + a.Im());
		// System.out.println("b + a = " + b.plus(a));
		// System.out.println("a - b = " + a.minus(b));
		// System.out.println("a * b = " + a.times(b));
		// System.out.println("b * a = " + b.times(a));
		// System.out.println("a / b = " + a.divides(b));
		// System.out.println("(a / b) * b = " + a.divides(b).times(b));
		// System.out.println("conj(a) = " + a.conjugate());
		// System.out.println("|a| = " + a.abs());
		// System.out.println("tan(a) = " + a.tan());
		// }

	}
}