/**
 * Wrapper for a a musical note and the note's length.
 */
public class NoteAndLength {
  int note, length;
  String name;

  public NoteAndLength(int n, int l) {
    note = n;
    length = l;
  }

  public int getNote(){ return note; }

  public int getLength() { return length; }

  public String getName() {
    if (this.getNote() == 60) return "C";
    else if (this.getNote() == 62) return "D";
    else if (this.getNote() == 64) return "E";
    else if (this.getNote() == 65) return "F";
    else if (this.getNote() == 67) return "G";
    else if (this.getNote() == 69) return "A";
    else return "B";
  }

  public String toString() {
    return "( "+ this.getName() + ", " + this.getLength() + " )";
  }

  public boolean equals(NoteAndLength nal) {
    return nal.getNote() == this.note && nal.getLength() == this.length;
  }
}
