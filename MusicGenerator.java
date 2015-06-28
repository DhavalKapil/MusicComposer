/**
 * MusicGenerator : A music generator that makes pleasing music based on western music theory
 * @author Dhaval Kapil
 * MIT license http://www.opensource.org/licenses/mit-license.php
 */


import javax.sound.midi.*;
import java.io.*;

public class MusicGenerator
{   
    private final static int[] major = {48, 50, 52, 53, 55, 57, 59, 60, 62, 64, 65, 67, 69, 71, 72, 74, 76, 77, 79, 81, 83, 84};
    private final static int[] dorian = {48, 50, 51, 53, 55, 57, 58, 60, 62, 63, 65, 67, 69, 70, 72, 74, 75, 77, 79, 81, 82, 84};
    private final static int[] phrygian = {48, 49, 51, 53, 55, 56, 58, 60, 61, 63, 65, 67, 68, 70, 72, 73, 75, 77, 79, 80, 82, 84};
    private final static int[] lydian = {48, 50, 52, 54, 55, 57, 59, 60, 62, 64, 66, 67, 69, 71, 72, 74, 76, 78, 79, 81, 83, 84};
    private final static int[] mixolydian = {48, 50, 52, 53, 55, 57, 58, 60, 62, 64, 65, 67, 69, 70, 72, 74, 76, 77, 79, 81, 82, 84};    
    private final static int[] minor = {48, 50, 51, 53, 55, 56, 58, 60, 62, 63, 65, 66, 68, 70, 72, 74, 75, 77, 79, 80, 82, 84};
    private final static int[] locrian ={48, 49, 51, 53, 54, 56, 58, 60, 61, 63, 65, 66, 68, 70, 72, 73, 75, 77, 78, 80, 82, 84};
    private final static int[] pentatonic = {48, 50, 52, 55, 57, 60, 62, 64, 67, 69, 72, 74, 76, 79, 81, 84};
    private static int[] notesAllowed;
    private static int[] sequence;
    
    private static String key;
    private static String scale;
    private static int noOfNotes;
    private static int offset;
    private static String file;
    
    public static void main(String[] args)
    {   if(args.length!=4)
        {   System.out.println("ERROR: usage:\njava MusicGenerator <key of scale> <scale type> <no. of notes> <file>");
            System.exit(0);
        }
        try
        {   key = args[0].toLowerCase();
            scale = args[1].toLowerCase();
            if(scale.equals("pentatonic"))
                notesAllowed = new int[16];
            else
                notesAllowed = new int[22];
            noOfNotes =  Integer.parseInt(args[2]); 
            file = args[3];
            if(!file.endsWith(".mid"))
                file += ".mid";
            checkKey();
            checkScale();
            allowNotes();
            generateSequence();
            printSequence();
            writeToFile();
            playMusic();            
        }
        catch(Exception e)
        {   System.out.println(e.toString());
        }
    }
    
    private static void checkKey()
    throws Exception
    {   if((key.length()!=1) && (key.length()!=2))
            throw new Exception("Invalid key of scale");
        switch(key.charAt(0))
        {   case 'a' : offset = 9;
                break;
            case 'b' : offset = 10;
                break;
            case 'c' : offset = 0;
                break;
            case 'd' : offset = 2;
                break;
            case 'e' : offset = 4;
                break;
            case 'f' : offset = 5;
                break;
            case 'g' : offset = 7;
                break;
            default : throw new Exception("Invalid key of scale");
        }
        if(key.length()==2)
            if(key.charAt(1)!='#')
                throw new Exception("Invalid key of scale");
            else if((key.charAt(0)=='b') || (key.charAt(0)=='e'))
                throw new Exception("Invalid key of scale");
            else
                offset += 1;
    }
    
    private static void checkScale()
    throws Exception
    {   if(!scale.equals("major"))
            if(!scale.equals("dorian"))
                if(!scale.equals("phrygian"))
                    if(!scale.equals("lydian"))
                        if(!scale.equals("mixolydian"))
                            if(!scale.equals("minor"))
                                if(!scale.equals("locrian"))
                                    if(!scale.equals("pentatonic"))
                                        throw new Exception("Invalid scale");
    }
    
    private static void allowNotes()
    {   for(int i = 0;i<notesAllowed.length;i++)
        {   if(scale.equals("major"))
                notesAllowed[i] = major[i] + offset;
            else if(scale.equals("dorian"))
                notesAllowed[i] = dorian[i] + offset;
            else if(scale.equals("phrygian"))
                notesAllowed[i] = phrygian[i] + offset;
            else if(scale.equals("lydian"))
                notesAllowed[i] = lydian[i] + offset;
            else if(scale.equals("mixolydian"))
                notesAllowed[i] = mixolydian[i] + offset;
            else if(scale.equals("minor"))
                notesAllowed[i] = minor[i] + offset;
            else if(scale.equals("locrian"))
                notesAllowed[i] = locrian[i] + offset;
            else if(scale.equals("pentatonic"))
                notesAllowed[i] = pentatonic[i] + offset;
        }
    }
    
    private static void generateSequence()
    {   sequence = new int[noOfNotes];
        for(int i = 0;i<sequence.length;i++)
        {   sequence[i] =  notesAllowed[(int)(Math.random()*notesAllowed.length)];
            if(i!=0)
                if(sequence[i]==sequence[i-1])
                    i--;     
        }
    }
    
    private static void printSequence()
    {   for(int note:sequence)
            System.out.print(note + " ");
        System.out.println();
    }
    
    private static void playMusic()
    throws Exception
    {   Synthesizer synth = MidiSystem.getSynthesizer();       
        synth.open();                
        MidiChannel chan[] = synth.getChannels();
        chan[4].programChange(0,0);            
        for(int note:sequence)
        {   if (chan[4] != null) 
                chan[4].noteOn(note, 93);
            Thread.sleep(((int)(Math.random()*2) + 1)*200);
        }
        synth.close();
    }
    
    private static void writeToFile()
    throws Exception
    {   Sequencer sc = MidiSystem.getSequencer();
        Sequence s = new Sequence(Sequence.PPQ, 10);
        sc.setSequence(s);
        Track track = s.createTrack();
        for(int i = 0, n = 0;i<sequence.length;i++, n+=4)
        {   ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, 0, sequence[i], 100);
            track.add(new MidiEvent(on, n));
        }
        MidiSystem.write(s, 0, new File(file));
        sc.close();
    }
}
