package gt

/**
 * Common scales for music.
 * 
 * @author Carl Hall (carl.hall@gmail.com)
 */
class Scales
{
	// Standard Scales
	static final int[] CHROMATIC = [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ]
	static final int[] MAJOR = [ 0, 2, 4, 5, 7, 9, 11 ]
	static final int[] MINOR = [ 0, 2, 3, 5, 7, 8, 10 ]
	static final int[] HARMONIC_MINOR = [ 0, 2, 3, 5, 7, 8, 11 ]
	static final int[] MELODIC_MINOR = [ 0, 2, 3, 5, 7, 8, 9, 10, 11 ]
	static final int[] NATURAL_MINOR = [ 0, 2, 3, 5, 7, 8, 10 ]
	static final int[] DIATONIC_MINOR = [ 0, 2, 3, 5, 7, 8, 10 ]
	static final int[] AEOLIAN = [ 0, 2, 3, 5, 7, 8, 10 ]
	static final int[] DORIAN = [ 0, 2, 3, 5, 7, 9, 10 ]
	static final int[] LYDIAN = [ 0, 2, 4, 6, 7, 9, 11 ]
	static final int[] MIXOLYDIAN = [ 0, 2, 4, 5, 7, 9, 10 ]
	static final int[] PENTATONIC = [ 0, 2, 4, 7, 9 ]
	static final int[] BLUES = [ 0, 2, 3, 4, 5, 7, 9, 10, 11 ]
	static final int[] TURKISH = [ 0, 1, 3, 5, 7, 10, 11 ]
	static final int[] INDIAN = [ 0, 1, 1, 4, 5, 8, 10 ]
	// Instrument Tunings
	static final int[] GUITAR_STANDARD = [ 0, 5, 10, 15, 19, 24 ]
	static final int[] BASS_STANDARD = [ 0, 5, 10, 15 ]

	static int[] arpeggio(int[] scale)
	{
		int[] arp = new int[3]
		arp[0] = scale[0]
		arp[1] = scale[2]
		arp[2] = scale[4]
		return arp
	}
}
