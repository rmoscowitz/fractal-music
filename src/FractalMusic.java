import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.midi.*;

public class FractalMusic {
  static boolean debug = false;

  /**
   * Get a subset of the ones-counting sequence (starting with 0).
   * 
   * i.e. seqLength = 10, sampleRate = 2
   * ones-counting sequence: 0 1 1 2 1 2 2 3 1 2 2 3 2 3 3 4 1 2 2 3 ...
   * 10 elements when under-sampled with rate 2: 0 1 1 2 1 2 2 3 1 2
   * 6 elements when under-sampled with rate 3: 0 2 2 2 4 2
   * 
   * @param seqLength Number of elements in the sequence
   * @param sampleRate Rate to sample the sequence
   * @return Ones-counting sequence as an array of integers.
   */
  public static int[] onesCount(int seqLength, int sampleRate) {
    int[] seq = new int[seqLength];

    for (int i = 0; i < seqLength; i++) {
      seq[i] = Integer.bitCount((i * sampleRate));
    }

    if (debug) {
      System.out.println("Ones-counting sequence: " + Arrays.toString(seq));
      System.out.println("Ones-counting length: " + seq.length);
    }

    return seq;
  }

  /** 
   * Map an integer sequence to a list of {@link NoteAndLength} objects. The note lengths
   * are calculated by the number of times a note appears consecutively in the sequence.
   */
  public static ArrayList<NoteAndLength> mapToMusic(int[] seq) {
    ArrayList<NoteAndLength> notes = new ArrayList<NoteAndLength>();
    int noteLength = 10;
    int nextNote = seq[1];

    for (int n = 0; n < seq.length; n++) {
      if (seq[n] != nextNote) {

        /*
        //CHROMATIC
				if(seq[n]%12 == 1) notes.add(new noteAndLength(60,noteLength)); // C
				else if(seq[n]%12 == 2) notes.add(new noteAndLength(61,noteLength)); // C# / Db
				else if(seq[n]%12 == 3) notes.add(new noteAndLength(62,noteLength)); // D
				else if(seq[n]%12 == 4) notes.add(new noteAndLength(63,noteLength)); // D# / Eb
				else if(seq[n]%12 == 5) notes.add(new noteAndLength(64,noteLength)); // E
				else if(seq[n]%12 == 6) notes.add(new noteAndLength(65,noteLength)); // F
				else if(seq[n]%12 == 7) notes.add(new noteAndLength(66,noteLength)); // F# / Gb
				else if(seq[n]%12 == 8) notes.add(new noteAndLength(67,noteLength)); // G
				else if(seq[n]%12 == 9) notes.add(new noteAndLength(68,noteLength)); // G# / Ab
				else if(seq[n]%12 == 10) notes.add(new noteAndLength(69,noteLength)); // A
				else if(seq[n]%12 == 11) notes.add(new noteAndLength(70,noteLength)); // A
				else if(seq[n]%12 == 0) notes.add(new noteAndLength(71,noteLength)); // B
				else notes.add(new noteAndLength(0,0));
         */

        // C MAJOR
        if(seq[n]%7 == 0) notes.add(new NoteAndLength(60,noteLength)); // C
        else if(seq[n]%7 == 1) notes.add(new NoteAndLength(62,noteLength)); // D
        else if(seq[n]%7 == 2) notes.add(new NoteAndLength(64,noteLength)); // E
        else if(seq[n]%7 == 3) notes.add(new NoteAndLength(65,noteLength)); // F
        else if(seq[n]%7 == 4) notes.add(new NoteAndLength(67,noteLength)); // G
        else if(seq[n]%7 == 5) notes.add(new NoteAndLength(69,noteLength)); // A
        else if(seq[n]%7 == 6) notes.add(new NoteAndLength(71,noteLength)); // B
        else notes.add(new NoteAndLength(0,0));


        /*
        // C MAJOR TONIC CHORD
				if(seq[n]%8 == 1) notes.add(new noteAndLength(60,noteLength)); // C4
				else if(seq[n]%8 == 2) notes.add(new noteAndLength(64,noteLength)); // E4
				else if(seq[n]%8 == 3) notes.add(new noteAndLength(67,noteLength)); // G4
				else if(seq[n]%8 == 4) notes.add(new noteAndLength(72,noteLength)); // C5
				else if(seq[n]%8 == 5) notes.add(new noteAndLength(76,noteLength)); // E5
				else if(seq[n]%8 == 6) notes.add(new noteAndLength(79,noteLength)); // G5
				else if(seq[n]%8 == 7) notes.add(new noteAndLength(84,noteLength)); // G5
				else if(seq[n]%8 == 0) notes.add(new noteAndLength(88,noteLength)); // C6
				else notes.add(new noteAndLength(0,0));
         */

        /* 
        // Perfect Fourths Starting on C3
				if(seq[n]%8 == 1) notes.add(new noteAndLength(48,noteLength)); // C3
				else if(seq[n]%8 == 2) notes.add(new noteAndLength(52,noteLength)); // F3
				else if(seq[n]%8 == 3) notes.add(new noteAndLength(58,noteLength)); // Bb3
				else if(seq[n]%8 == 4) notes.add(new noteAndLength(62,noteLength)); // Eb4
				else if(seq[n]%8 == 5) notes.add(new noteAndLength(68,noteLength)); // Ab4
				else if(seq[n]%8 == 6) notes.add(new noteAndLength(72,noteLength)); // Db5
				else if(seq[n]%8 == 7) notes.add(new noteAndLength(78,noteLength)); // Gb5
				else if(seq[n]%8 == 0) notes.add(new noteAndLength(82,noteLength)); // Cb6
				else notes.add(new noteAndLength(0,0));
         */

        noteLength = 10;
      }
      else {
        noteLength += 10;
      }

      nextNote = (n < seq.length-2) ? seq[n+2] : 0;
    }

    if (debug) {
      System.out.println("\nRepeated notes in ones-counting have been combined...");
      System.out.println("\nSequence mapped to notes: " + notes.toString());
      System.out.println("Mapped sequence length: " + notes.size());
    }

    return notes;
  }

  /**
   * Prints usage instructions.
   */
  private static void printUsage() {
    System.out.println("");
    System.out.println("================================================================================");	
    System.out.println("Welcome to the Fractal Music Generator!" + '\n' +
        "In order to use this program, you must compile with the following arguments:" + '\n'  + '\n' +
        "java fractalMusic [sample size] [sampling rate] [chord type] [debug]" + '\n' + '\n' +
        '\t' + "sample size: an integer specifiying how many notes the program " + '\n' + 
        '\t' + '\t' + "should initially sample, i.e. 1000." + '\n' +
        '\t' + "sampling rate: an integer greate than 0, specifying which notes" + '\n' +
        '\t' + '\t' + "to sample (i.e. 2 will play every other note in the sequence, + '\n' + " +
        '\t' + '\t' + "3 will play every third)." + '\n' +
        '\t' + "chord type:  " + '\n' + 
        '\t' + '\t' + "-maj	:	Major" + '\n' +
        '\t' + '\t' + "-min	: 	Minor" + '\n' +
        '\t' + '\t' + "-dim	: 	Diminished" + '\n' +
        '\t' + '\t' + "-aug	: 	Augmented" + '\n' +
        '\t' + '\t' + "-none	: 	No Chord, just the base note" + '\n' +
        '\t' + "debug: if this argument is 'true', debugging print statements will show. " + '\n');
    System.out.println("For example, \"java fractalMusic 1000 50 -min -p\" will sample every 50th note " + '\n' +
        "from the ones-counting sequence until there are 1000 notes. It will then map those" + '\n' +
        "notes to pitches, and build minor chords on top of each of the notes. You'll also see" + '\n' +
        "my fun debugging print statements (which are useful if you want to actually see the" + '\n' + '\n' +
        "sequence you are listening to." + '\n' + '\n' +
        "See the project write-up for more details. Enjoy your fractal composition!");
    System.out.println('\n' + "================================================================================");
  }

  /**
   * Interprets arguments, generates the sequence and opens the MIDI player.
   */
  public static void main(String args[]) {
    int third, fifth;

    if ((args.length == 3) || (args.length == 4)) {
      int sampleSize = Integer.valueOf(args[0]);
      int sampleRate = Integer.valueOf(args[1]);
      String chordType = args[2].substring(1);

      if ((sampleSize <= 20) || (sampleRate < 1)){
        System.out.println("Either your sample size or sampling rate is too small.");
        System.out.println("A sample size of 500-1000 and a sample rate between 1-100 is recommended.");
        return;
      }

      if (chordType.equalsIgnoreCase("MAJ")){
        third = 4;
        fifth = 7;
      }
      else if (chordType.equalsIgnoreCase("MIN")){
        third = 3;
        fifth = 7;
      }
      else if (chordType.equalsIgnoreCase("DIM")) {
        third = 3;
        fifth = 6;
      }
      else if (chordType.equalsIgnoreCase("AUG")) {
        third = 4;
        fifth = 8;
      }
      else if (chordType.equalsIgnoreCase("NONE")) {
        third = 0;
        fifth = 0;
      }
      else {
        System.out.println("Incompatible chord type.");
        printUsage();
        return;
      }

      if (args.length == 4 && args[3].equals("true")) {
        debug = true;
      }

      // Generate sequence
      ArrayList<NoteAndLength> notes = mapToMusic(onesCount(sampleSize, sampleRate));

      // Play sequence
      try {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        MidiChannel channel = synthesizer.getChannels()[0];

        for (NoteAndLength note : notes) {
          channel.noteOn(note.getNote(), 20);
          channel.noteOn(note.getNote()+third, 20);
          channel.noteOn(note.getNote()+fifth, 20);
          channel.noteOn(note.getNote()+12, 20);

          try { Thread.sleep(note.getLength() * 20); } 
          catch (InterruptedException e) { break; } 
          finally { channel.noteOff(note.getNote()); }
        }
      } catch (MidiUnavailableException e) { e.printStackTrace(); }
    }
    else {
      printUsage();
      return;
    }

    System.exit(0);
  }
}