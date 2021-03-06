# A Basic Fractal Music Generator
Robyn Moscowitz<br>
Rice University, Comp 280<br>
May 5, 2010 (updated July 26, 2014)

## Goal
The goal for this project was to create a small Java program that would generate fractal music. The program takes a simple mathematical sequence with fractal properties, maps it to a musical language, then plays it. The method for doing so is modeled off of a similar program by Lars Kinderman as described on his [website](http://reglos.de/musinum/) and in a paper by his mentor in the book *Fractal Horizons* ([amazon](http://www.amazon.com/Fractal-Horizons-Clifford-A-Pickover/dp/0312125992#)).

## The Sequence
The sequence that Kindermann works with, and I chose to work with as well, is the ones-counting sequence, which is more commonly known as the Morse-Thue sequence. The sequence is generated by taking positive integers (0, 1, 2, 3, 4, …), converting each integer to its binary representation (0, 1, 10, 11, 100…), then counting the number of ones for each integer (0,1,1,2,1, …). The ones-counting sequence can also be generated recursively. Starting with the leading 0, you use the following recursive rule: for the first 2m terms in the sequence, the next 2m terms are the same as the first 2m terms, plus one. I chose to generate numbers using the first method for reasons I will describe later. The ones-counting sequence is interesting because it is self-similar, meaning that the sequence is similar to a part of itself. In the case of the ones-counting sequence, taking every other integer (starting with the leading zero) will yield the exact same sequence.
>**0** 1 **1** 2 **1** 2 **2** 3 **1** 2 **2** 3 **2** 3 **3** 4 …

## Undersampling
In order to produce music from this simple sequence, each number is mapped to a musical pitch. However, simply mapping the original sequence would produce one, not-so-interesting melody. In Fractal Horizons, Kindermann’s mentor, Manfred Shroeder suggests undersampling (i.e. taking every nth term, instead of every term). My program allows for the user to decide the value of n. Based on this value, and how many notes the user wants to generate, the actual sequence is generated as follows:

```
for (int i = 0; i < l; i++) {
seq[i] = Integer.bitCount((i*r));
}
```

This line of code exploits the fact that the ones-counting sequence is originally generated from integers. For example, if you wanted to generate the ones-counting sequence, and undersample every other value, instead of every value, you would count the ones in every other integer (0, 2, 4, 6, …). You can see that the loop goes from 0 to l (which is the number of samples, say 100). Then, to get the value, the index is multiplied by r (the under-sample rate). So, as in the example before, r=2, so the sequence will use the method bitCount to count the number of ones in (0*2, 1*2, 2*2, 3*2, … ). I chose to use this method instead of recursively generating the entire sequence then undersampling to save time and memory. For example, if I wanted the first 1000 notes when undersampling at a rate of 100, I would have to generate the first 100,000 notes in order to do that, and that seemed impractical. Instead, I count every 100th integer 1000 times.

## Mapping to Tones
I experimented with a few different mappings to musical notes. First, I tried mapping to a simple C-major scale. I do this by taking the value mod 7. If  value%7 is 0, that becomes a C4, or middle C (MIDI number 60 ... read more about MIDI numbers [here](http://www.phys.unsw.edu.au/jw/notes.html)). value%7 is 1 = D4 (MIDI number 62), value%7 is 2 = E2 (MIDI number 64), etc. This is the mapping I eventually decided to use, however, I tried other mappings including the chromatic scale, mapping to values in the C major tonic chord (C4, E4, G4, C5, E5, G5, …) and perfect fourths starting on C3 (C3, F3, Bb3, Eb4, Ab4, …). Each of these yielded a slightly different sound and I encourage the reader to go into fractalMusic.java and uncomment a different mapping that you would like to hear. I chose not to make the mapping a runtime argument because I wanted to make running my program relatively easy. I do include an option for building chords on top of the sequence in order to make the melodies more interesting.

## Rhythm
The ones-counting sequence has now been undersampled and mapped to the C major scale. In order to make the new melody just a little more interesting, I add an element of rhythm. In order to simulate note length, I count the number of times a note (number) repeats in the sequence, and I combine all repetitions into one note with a length equal to the number of repetitions. For example, if I don’t undersample (or undersample at a rate of 2, because they are the same), I will get the sequence C D D E D E E F D … I then assign note-lengths in increments of 10. So the above sequence will become (C, 10), (D, 20), (E, 10), (D, 10), (E, 20), (F, 10), (D, 10)… The note and note lengths are wrapped in a Java class called noteAndLength. As a side note, calculating the note length actually occurs in the same loop that maps from an integer to a note. In the list mapToMusic, I traverse the ones-counting sequence and continually compare the current value to the next value in order to see whether a note will be repeated (and thus the note length increased). If the notes are different, a new noteAndLength object is added to the list of notes to be played, otherwise, the note length is increased and a note is not added to the list. An important part of this process is not exceeding the length of the original list of integers when trying to get the “next note.” Bounds checking at the end of the loop takes care of this.

## Play Some Music!
 The final step in the program is to use Java’s MIDI library (a part of the Sound API) to play the final sequence. 

First a Synthesizer object must be initialized and opened:

```
Synthesizer synthesizer = MidiSystem.getSynthesizer();
synthesizer.open(); 
```

Then a MidiChannel must be initialized: 

```
MidiChannel channel = synthesizer.getChannels()[0];
```

Then, for every note in our final sequence, play the note:

```
for (noteAndLength note : notes) {
	channel.noteOn(note.getNote(), 20);
	try { 
		Thread.sleep(note.getLength() * 20);
	} 
	catch (InterruptedException e) {
		break;
	} 
	finally {
		channel.noteOff(note.getNote());
	}
}
```

An extra element that I added to my program was the option to build chords on top of each note. The program can play major, minor, diminished and augmented chords. For each chord, it calculates the correct pitch for the other notes in the chord (which is some number of half steps above the bass note, which varies for each chord-type). Then, instead of just playing one note, the MIDI player plays four (the bass, or original note, the third, the fifth and an octave above the bass):

```
channel.noteOn(note.getNote(), 20);
channel.noteOn(note.getNote()+third, 20);
channel.noteOn(note.getNote()+fifth, 20);
channel.noteOn(note.getNote()+12, 20);
```

A note about chords: This was just an extra element I added in for fun. It does not change the sequence. It may, however, change the sound of the composition. If you decide to use major chords, your composition may sound happier than if you use minor chords. Diminished chords may sound more mysterious while augmented chords may sound more whimsical. Since these sequences do not produce the most riveting music every written, I thought adding some color with chords would be fun.

## Running FractalMusic
Compile then with the appropriate arguments (usage instructions will print if your arguments do not make sense). Here is an example:

```
% javac fractalMusic.java
% java fractalMusic 1000 63 –min true
```

The arguments are:

java fractalMusic [sample size] [sampling rate] [chord type] [debug]

**sample size**<br>an integer specifying how many notes the program should initially sample, i.e. 1000 <br>
**sampling rate**<br>an integer greater than 0, specifying which notes to sample (i.e. 2 will play every other note in the sequence, 3 will play every third)<br>
**chord type**
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-maj : Major
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-min : Minor
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-dim : Diminished
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-aug : Augmented
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-none : No chord, just single note<br>
**debug**<br>if "true", debugging messages statements will be printed

## Conclusion
Thanks for trying my fractal music generator! I enjoyed interpreting what I learned about fractal sequences into Java and producing some really interesting sounds.

### Some of my personal favorites:
fractalMusic 1000 5 -maj
<br>fractalMusic 1000 35 –aug 
<br>fractalMusic 1000 63 –maj (Schroeder mentions this in Fractal Horizons because you can clearly hear a lot fractal patterns)

Enjoy!

