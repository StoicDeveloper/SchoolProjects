/*
 * usage notes:
 * -compile this file using "javac Decoder.java"
 * -to decode a file, use "java Decoder file_to_be_decoded.txt" (file extension is ignored, do not say ./file)
 * -file produced will be called "file_to_be_decoded.unzip" (can be renamed to .txt)
 * -code is not documented.
 */
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class Decoder {
    private HashMap<Integer,String> dict ;
    private Scanner in;
    private String file;
    private int next;

    public Decoder(String fileName) {
        file = fileName;
        try {
            in = new Scanner( new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dict = new HashMap<Integer,String>();
        for (int i= 32; i <= 126; i++) {
            dict.put(i-32,""+((char)i));
        }
        next = 95;
    }

    public void decode() {

        String[] info = file.split("\\.");
        String out = info[0];
        for (int i = 1; i < info.length-1; i++) {
            out += "." + info[i];
        }

        String out_file = out + ".unzip";
        try {


            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(out_file)));

            String last = null;

            boolean done = false;
            while (in.hasNextInt() && !done) {
                int code = in.nextInt();
                done = (code == -1);
                if (dict.containsKey(code)) {
                    String output = dict.get(code);
                    bw.write(output);
                    if (last != null) {
                        last = last + output.charAt(0);
                        dict.put(next, last);
                        next++;
                    }
                    last = output;
                } else if (!done) {
                    // special case;
                    assert (last != null);
                    String special = last + last.charAt(0);
                    bw.write( special);
                    dict.put(next, special);
                    next++;

                }
            }
            in.close();
            bw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            Decoder d = new Decoder(args[0]);
            d.decode();
        } else {
            System.err.println("Error: no command line argument found.");
        }
    }
}