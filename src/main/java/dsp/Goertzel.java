package dsp;

public class Goertzel {
	public static double transform(int[] block, int searched_f, int sampling_f) {

		int k = (int) (0.5 + block.length * searched_f / sampling_f);
		double omega = 2 * k * Math.PI / block.length;
		double cosine = Math.cos(omega);
		double coeff = 2.0 * cosine;
		double Q0 = 0;
		double Q1 = 0;
		double Q2 = 0;

		for (int i = 0; i < block.length; i++) {
			Q0 = coeff * Q1 - Q2 + block[i];
			Q2 = Q1;
			Q1 = Q0;
		}
		return Math.sqrt(Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
	}
}