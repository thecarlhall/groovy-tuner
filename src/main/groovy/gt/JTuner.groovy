package gt

import java.io.File

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.SourceDataLine

/**
 * Code highjacked from http://jvalentino.blogspot.com/2005_10_01_archive.html
 */
class JTuner {
	void clipPlayer() {
		/* This code is based on the example found at:
		http://www.jsresources.org/examples/ClipPlayer.java.html */

		// Load the sound file
		File soundFile = new File("sin300.wav");

		// Object that will hold the audio input stream for the sound
		Object currentSound;

		// Object that will play the audio input stream of the sound
		Clip clip;

		// Object that contains format information about the audio input stream
		AudioFormat format;

		// Load the audio input stream
		try {
			currentSound = AudioSystem.getAudioInputStream(soundFile);
		} catch(Exception e1) {
			println("Error loading file");       
		}

		try {
			// Get the format information from the Audio Input Stream
			AudioInputStream stream = (AudioInputStream) currentSound;
			format = stream.getFormat();

			// Get information about the line
			DataLine.Info info = new DataLine.Info(Clip.class,
				stream.getFormat(),
			((int) stream.getFrameLength() * format.getFrameSize()));

			// Load clip information from the line information
			clip = (Clip) AudioSystem.getLine(info);

			// Write the stream onto the clip
			clip.open(stream);

			// make the current sound the clip
			currentSound = clip;

			// start the clip
			clip.start();

			// loop the clip continuously
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		} catch (Exception ex) {
			ex.printStackTrace();
			currentSound = null;
		}
	
		/**
		 * This code plays a given sound file over and over again by looping through the byte array
		 * of audio data that can be obtained through the  AudioFormat object, but this code by
		 * itself is of little use since no operations are performed on the sampled sound. In order
		 * to perform operations on the sampled sound the  FloatControl object has to be used. The
		 * FloatControl object provides control over a range of floating-point values,5 which are
		 * the types of data found in a sampled sound. The most straightforward method for frequency
		 * manipulation of a sampled sound is changing the sample rate. The sample rate is the rate
		 * at which periodic measurements are taken of instantaneous amplitudes of an electric
		 * signal. Doubling the sample rate of a sampled sound causes the frequency to double and
		 * the duration to be halved. Halving the sample rate of a sampled sound causes the
		 * frequency to be halved and the duration to double. Manipulating the sample rate using the
		 * FloatControl object can be done in the following way:
		 */
		// Get the sampling rate from the AudioFormat object in the code above and double it
		float newRate = format.getSampleRate() * 2;

		// Create the FloatControl object with the clip object from the code above
		FloatControl rateControl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);

		// change the sampling rate
		rateControl.setValue(newRate);

		/**
		 * If the  Clip and  AudioFormat objects are made global, this code can be called as a
		 * thread while the sampled sound is playing to change its frequency in real-time. The other
		 * sampled sound manipulation needed is the ability to change sound level. Sound level
		 * manipulation can also be done through using the  FloatControl object:
		 */
		// specify a new sound level from 0 to 100
		double gain = 80.0;

		// create the gain control object using the clip object from the code above
		FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

		// convert the given sound level to decibel
		gain = gain / 100;
		float dB = (float) (Math.log(gain == 0.0 ? 0.0001 :gain) / Math.log(10.0)*20.0);

		// change the sound level
		control.setValue(dB);
	}




	void simpleAudioPlayer() {
		/* This code is based on the example found at:
		http://www.jsresources.org/examples/SimpleAudioPlayer.java.html */

		//		Create a global buffer size
		private static final int EXTERNAL_BUFFER_SIZE = 128000;

		//		Get the location of the sound file
		//		File soundFile = new File("sin300.wav");

		//		Load the Audio Input Stream from the file     
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		//		Get Audio Format information
		AudioFormat audioFormat = audioInputStream.getFormat();

		//		Handle opening the line
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		//		Start playing the sound
		line.start();

		//		Write the sound to an array of bytes
		int nBytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
			    nBytesRead = audioInputStream.read(abData, 0, abData.length);
	
			} catch (IOException e) {
			    e.printStackTrace();
			}
			if (nBytesRead >= 0) {
			    int nBytesWritten = line.write(abData, 0, nBytesRead);
			}
		}

		// close the line   
		line.drain();
		line.close();
		
		
		/**
		 * With the sound objects such as  AudioFormat and  AudioInputStream initialized, some of
		 * the variables needed for DFT computations can be calculated:
		 */
		//		Calculate the sample rate
		float sample_rate = audioFormat.getSampleRate();
		println("sample rate = "+sample_rate);

		//		Calculate the length in seconds of the sample
		float T = audioInputStream.getFrameLength() / audioFormat.getFrameRate();
		println("T = "+T+ " (length of sampled sound in seconds)");

		//		Calculate the number of equidistant points in time
		int n = (int) (T * sample_rate) / 2;
		println("n = "+n+" (number of equidistant points)");

		//		Calculate the time interval at each equidistant point
		float h = (T / n);
		println("h = "+h+" (length of each time interval in seconds)");
		
		/**
		 * This array of bytes though does not directly contain signal data per time interval, which
		 * is needed in order to perform DFT. The array of bytes must be converted to Endian format
		 * where the specific type of Endian is dependant on the original encoding of the sampled
		 * sound. Each value in the byte array is 8 bits or 1 byte, and Endian values are stored in
		 * the format of 16 bits or 2 bytes, meaning that it takes 2 byte values from the byte array
		 * to represent the signal data at a given point in time, where the signal data at a given
		 * point in time is a single Endian value. The conversion can be done in the following way:
		 */
		//		Determine the original Endian encoding format
		boolean isBigEndian = audioFormat.isBigEndian();

		//		this array is the value of the signal at time i*h
		int[] x = new int[n];

		//		convert each pair of byte values from the byte array to an Endian value
		for (int i = 0; i < n*2; i+=2) {
			int b1 = abData[i];
			int b2 = abData[i + 1];
			if (b1 < 0) b1 += 0x100;
			if (b2 < 0) b2 += 0x100;

			int value;

			//Store the data based on the original Endian encoding format
			if (!isBigEndian) value = (b1 << 8) + b2;
			else value = b1 + (b2 << 8);
			x[i/2] = value;
		}
		
		/**
		 * The array  x now contains the signal values at each time interval, so the DFT can be
		 * performed on each value of x.
		 * 
		 * It should also be noted that only the first half of the equidistant points are useable
		 * due to the Nyquist Criterion, because data will only be reliable for half of the sampling
		 * frequency (sample rate). The frequency is also a combination of placement along the time
		 * domain, sample rate, the number of equidistant points, and the length of each equidistant
		 * point in seconds.
		 */
		// do the DFT for each value of x sub j and store as f sub j
		double[] f = new double[n/2];
		for (int j = 0; j < n/2; j++) {

			double firstSummation = 0;
			double secondSummation = 0;

			for (int k = 0; k < n; k++) {
				double twoPInjk = ((2 * Math.PI) / n) * (j * k);
				firstSummation +=  x[k] * Math.cos(twoPInjk);
				secondSummation += x[k] * Math.sin(twoPInjk);
			}

			f[j] = Math.abs( Math.sqrt(Math.pow(firstSummation,2) + 
					Math.pow(secondSummation,2)) );

			double amplitude = 2 * f[j]/n;
			double frequency = j * h / T * sample_rate;
			println("frequency = "+frequency+", amp = "+amplitude);
		}
	}
}
