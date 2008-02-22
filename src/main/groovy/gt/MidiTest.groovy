package gt

import javax.sound.midi.Instrument
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiSystem
import javax.sound.midi.Synthesizer

class MidiTest {
	static final boolean DEBUG = true

	static void main(String[] argv)
	{
		MidiTest midi = new MidiTest()
		midi.playScale(Instruments.ROCK_ORGAN, Notes.E4, Scales.arpeggio(Scales.MAJOR), 1)
//		midi.playScale(Instruments.NYLON_STR_GUITAR, Notes.E4, Scales.MAJOR, 1)
//		midi.playScale(Instruments.NYLON_STR_GUITAR, Notes.E2, Scales.GUITAR_STANDARD, 1)
//		midi.playScale(Instruments.FINGERED_BASS, Notes.E1, Scales.BASS_STANDARD, 1)
	}

	void playScale(int instrument, int startingNote, int[] scale, int repeatCount)
	{
		try
		{
			// get the synth & open it
			Synthesizer syn = MidiSystem.getSynthesizer()
			syn.open()
			// get the midi channels
			MidiChannel[] mc = syn.getChannels()
			// get the instruments list and select the one to play
			Instrument[] instrs = syn.getDefaultSoundbank().getInstruments()
			Instrument instr = instrs[instrument]
			syn.loadInstrument(instr)
			mc[0].programChange(instr.getPatch().getProgram())
			// generate the standard tuning notes
			int[] notes = generateNotes(startingNote, scale)
			// repeat the notes for easier tuning
			int[] repeatNotes = repeat(notes, repeatCount)
			// play the notes
			playChannel(mc[0], repeatNotes, 600, 1500)
		}
		catch (Exception e)
		{
			e.printStackTrace()
		}
	}

	List generateNotes(int startingNote, int[] scale)
	{
		List notes = [];
		scale.each
		{
			inc ->
			notes.add(startingNote + inc)
		}
		return notes
	}

	void playChannel(MidiChannel channel, int[] notes, int velocity, int duration)
	{
		notes.each
		{
			note ->
			channel.noteOn(note, velocity)
			sleep(duration)
			channel.noteOff(note)
		}
	}

	/**
	 * Repeat <code>notes</code>, <code>repeatCount</code> times.
	 */
	List repeat(int[] notes, int repeatCount)
	{
		List newNotes = [];
		
		notes.each
		{
			note ->
			repeatCount.times
			{
				newNotes.add(note)
			}
		}
		return newNotes
	}

	void sleep(int duration)
	{
			try
			{
				Thread.sleep(duration)
			}
			catch (InterruptedException e) {}
	}
}
