import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Song {

    private String fileName;
    private String songTitle;
    private String authorName;
    private Note[] notes;
    private int numOfNotes;
    private double duration;
    private boolean startAddingToDuration = false;

    public Song(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        Scanner kb = new Scanner(new File(fileName));
        songTitle = kb.nextLine();
        authorName = kb.nextLine();
        numOfNotes = Integer.parseInt(kb.nextLine());
        notes = new Note[numOfNotes];
        for (int i = 0; i < numOfNotes; i++) {
            String line = kb.nextLine();
            String[] vals = line.split(" ");
            double dur = Double.parseDouble(vals[0]);
            duration = duration + dur;
            String pitch = vals[1];
            if (pitch.equals("R")) {
                boolean start = Boolean.parseBoolean(vals[2]);
                Note note = new Note(dur, start);
                notes[i] = note;
            } else {
                int octave = Integer.parseInt(vals[2]);
                String sharpValue = vals[3];
                boolean start = Boolean.parseBoolean(vals[4]);
                if (startAddingToDuration == true) {
                    duration = duration + dur;
                }
                if (start == true) {
                    startAddingToDuration = !startAddingToDuration;
                    duration = duration + dur;
                }


                Note note = new Note(dur, Pitch.getValueOf(pitch), octave, Accidental.getValueOf(sharpValue), start);
                notes[i] = note;
            }
        }


    }

    public Note[] getNotes() {
        return notes;
    }

    public String toString() {
        String info = Arrays.toString(notes);
        System.out.println(info);
        return info;
    }

    public void reverse() {
        Note tmp;

        for (int i = 0; i < notes.length / 2; i++) {
            tmp = notes[i];
            notes[i] = notes[notes.length - 1 - i];
            notes[notes.length - 1 - i] = tmp;
        }
    }

    public boolean octaveDown() {
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].getOctave() <= Note.OCTAVE_MIN) {
                return false;
            }
        }
        for (int i = 0; i < notes.length; i++) {
            notes[i].setOctave(notes[i].getOctave() - 1);
        }
        return true;

    }

    public void changeTempo(double ratio) {
        for (int i = 0; i < notes.length; i++) {
            notes[i].setDuration(notes[i].getDuration() * ratio);
        }
    }

    public boolean octaveUp() {
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].getOctave() >= Note.OCTAVE_MAX) {
                return false;
            }
        }
        for (int i = 0; i < notes.length; i++) {
            notes[i].setOctave(notes[i].getOctave() + 1);
        }
        return true;
    }

    public void play() {
        boolean startRepeating = false;
        int repeatIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < notes.length; i++) {
            notes[i].play();

            if (notes[i].isRepeat() == true) {
                if (repeatIndex == -1) {//this is for when it starts repeating
                    repeatIndex = i;
                } else {
                    endIndex = i;
                    for (int s = repeatIndex; s <= endIndex; s++) {
                        notes[s].play();

                    }
                    repeatIndex = -1;
                    endIndex = -1;
                }
                startRepeating = !startRepeating;

            }


        }

    }

    public double getTotalDuration() {
        return duration;
    }

    public String getTitle() {
        return songTitle;
    }

    public String getArtist() {
        return authorName;
    }


}