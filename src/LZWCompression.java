import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.Socket;

public class LZWCompression {

long startTime = System.nanoTime(); //timer variable, records start time
	public final class BinaryOut {

	    private BufferedOutputStream out;  // the output stream
	    private int buffer;                // 8-bit buffer of bits to write out
	    private int n;                     // number of bits remaining in buffer


	   /**
	     * Initializes a binary output stream from standard output.
	     */
	    public BinaryOut() {
	        out = new BufferedOutputStream(System.out);
	    }

	   /**
	     * Initializes a binary output stream from an {@code OutputStream}.
	     * @param os the {@code OutputStream}
	     */
	    public BinaryOut(OutputStream os) {
	        out = new BufferedOutputStream(os);
	    }

	   /**
	     * Initializes a binary output stream from a file.
	     * @param filename the name of the file
	     */
	    public BinaryOut(String filename) {
	        try {
	            OutputStream os = new FileOutputStream(filename);
	            out = new BufferedOutputStream(os);
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	   /**
	     * Initializes a binary output stream from a socket.
	     * @param socket the socket
	     */
	    public BinaryOut(Socket socket) {
	        try {
	            OutputStream os = socket.getOutputStream();
	            out = new BufferedOutputStream(os);
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	   /**
	     * Writes the specified bit to the binary output stream.
	     * @param x the bit
	     */
	    private void writeBit(boolean x) {
	        // add bit to buffer
	        buffer <<= 1;
	        if (x) buffer |= 1;

	        // if buffer is full (8 bits), write out as a single byte
	        n++;
	        if (n == 8) clearBuffer();
	    }

	   /**
	     * Writes the 8-bit byte to the binary output stream.
	     * @param x the byte
	     */
	    private void writeByte(int x) {
	        assert x >= 0 && x < 256;

	        // optimized if byte-aligned
	        if (n == 0) {
	            try {
	                out.write(x);
	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	            return;
	        }

	        // otherwise write one bit at a time
	        for (int i = 0; i < 8; i++) {
	            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
	            writeBit(bit);
	        }
	    }

	    // write out any remaining bits in buffer to the binary output stream, padding with 0s
	    private void clearBuffer() {
	        if (n == 0) return;
	        if (n > 0) buffer <<= (8 - n);
	        try {
	            out.write(buffer);
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        n = 0;
	        buffer = 0;
	    }

	   /**
	     * Flushes the binary output stream, padding 0s if number of bits written so far
	     * is not a multiple of 8.
	     */
	    public void flush() {
	        clearBuffer();
	        try {
	            out.flush();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	   /**
	     * Flushes and closes the binary output stream.
	     * Once it is closed, bits can no longer be written.
	     */
	    public void close() {
	        flush();
	        try {
	            out.close();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	   /**
	     * Writes the specified bit to the binary output stream.
	     * @param x the {@code boolean} to write
	     */
	    public void write(boolean x) {
	        writeBit(x);
	    }

	   /**
	     * Writes the 8-bit byte to the binary output stream.
	     * @param x the {@code byte} to write.
	     */
	    public void write(byte x) {
	        writeByte(x & 0xff);
	    }

	   /**
	     * Writes the 32-bit int to the binary output stream.
	     * @param x the {@code int} to write
	     */
	    public void write(int x) {
	        writeByte((x >>> 24) & 0xff);
	        writeByte((x >>> 16) & 0xff);
	        writeByte((x >>>  8) & 0xff);
	        writeByte((x >>>  0) & 0xff);
	    }

	   /**
	     * Writes the <em>r</em>-bit int to the binary output stream.
	     *
	     * @param  x the {@code int} to write
	     * @param  r the number of relevant bits in the char
	     * @throws IllegalArgumentException unless {@code r} is between 1 and 32
	     * @throws IllegalArgumentException unless {@code x} is between 0 and 2<sup>r</sup> - 1
	     */
	    public void write(int x, int r) {
	        if (r == 32) {
	            write(x);
	            return;
	        }
	        if (r < 1 || r > 32) throw new IllegalArgumentException("Illegal value for r = " + r);
	        if (x >= (1 << r))   throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
	        for (int i = 0; i < r; i++) {
	            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
	            writeBit(bit);
	        }
	    }


	   /**
	     * Writes the 64-bit double to the binary output stream.
	     * @param x the {@code double} to write
	     */
	    public void write(double x) {
	        write(Double.doubleToRawLongBits(x));
	    }

	   /**
	     * Writes the 64-bit long to the binary output stream.
	     * @param x the {@code long} to write
	     */
	    public void write(long x) {
	        writeByte((int) ((x >>> 56) & 0xff));
	        writeByte((int) ((x >>> 48) & 0xff));
	        writeByte((int) ((x >>> 40) & 0xff));
	        writeByte((int) ((x >>> 32) & 0xff));
	        writeByte((int) ((x >>> 24) & 0xff));
	        writeByte((int) ((x >>> 16) & 0xff));
	        writeByte((int) ((x >>>  8) & 0xff));
	        writeByte((int) ((x >>>  0) & 0xff));
	    }

	   /**
	     * Writes the 32-bit float to the binary output stream.
	     * @param x the {@code float} to write
	     */
	    public void write(float x) {
	        write(Float.floatToRawIntBits(x));
	    }

	   /**
	     * Write the 16-bit int to the binary output stream.
	     * @param x the {@code short} to write.
	     */
	    public void write(short x) {
	        writeByte((x >>>  8) & 0xff);
	        writeByte((x >>>  0) & 0xff);
	    }

	   /**
	     * Writes the 8-bit char to the binary output stream.
	     *
	     * @param  x the {@code char} to write
	     * @throws IllegalArgumentException unless {@code x} is betwen 0 and 255
	     */
	    public void write(char x) {
	        if (x < 0 || x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
	        writeByte(x);
	    }

	   /**
	     * Writes the <em>r</em>-bit char to the binary output stream.
	     *
	     * @param  x the {@code char} to write
	     * @param  r the number of relevant bits in the char
	     * @throws IllegalArgumentException unless {@code r} is between 1 and 16
	     * @throws IllegalArgumentException unless {@code x} is between 0 and 2<sup>r</sup> - 1
	     */
	    public void write(char x, int r) {
	        if (r == 8) {
	            write(x);
	            return;
	        }
	        if (r < 1 || r > 16) throw new IllegalArgumentException("Illegal value for r = " + r);
	        if (x >= (1 << r))   throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
	        for (int i = 0; i < r; i++) {
	            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
	            writeBit(bit);
	        }
	    }

	   /**
	     * Writes the string of 8-bit characters to the binary output stream.
	     *
	     * @param  s the {@code String} to write
	     * @throws IllegalArgumentException if any character in the string is not
	     *         between 0 and 255
	     */
	    public void write(String s) {
	        for (int i = 0; i < s.length(); i++)
	            write(s.charAt(i));
	    }


	   /**
	     * Writes the string of <em>r</em>-bit characters to the binary output stream.
	     * @param  s the {@code String} to write
	     * @param  r the number of relevants bits in each character
	     * @throws IllegalArgumentException unless r is between 1 and 16
	     * @throws IllegalArgumentException if any character in the string is not
	     *         between 0 and 2<sup>r</sup> - 1
	     */
	    public void write(String s, int r) {
	        for (int i = 0; i < s.length(); i++)
	            write(s.charAt(i), r);
	    }
	}


	HashMap <String,String> map; //is the dictionary

	String theText = "";  //full file text as string

	String path = ""; //file name

	String encodeOutput = ""; //encoded message as string

	String decodeOutput = ""; //decoded message as string

	//Constructor -- initializes the first 256 values | dictionary limited to a total of 400 entries

	public LZWCompression (String file)
	{
		File tempFile = new File(file+".txt"); //checks to see if the file to be read actually exists
		if(tempFile.exists() == false)
		{
				System.out.println("You're Bad: File Not Found");
				System.exit(1);
		}

		path = file;
		map = new HashMap<String, String>();

		for(int i = 0; i < 256; i++)

		{
			String binaryString = Integer.toBinaryString(i);

			int l = 9-binaryString.length();

			for(int j = 0; j < l; j++){

				binaryString = "0"+binaryString;
			}

			String actualLetters = Character.toString((char)i);

			map.put(actualLetters, binaryString);

		}

	}

	//Converts the file into a String

	public void readFile() {

		try {
			theText = Files.readString(Paths.get(path + ".txt"), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	//Compression algorithm

	public void LZWcompress() {

		encodeOutput = "";
		String current = "";

		for(int i = 0; i < theText.length(); i++) //limits additional dictionary entries to 300
		{

			current+=theText.charAt(i);

			if (i == theText.length() - 1) {

				encodeOutput+=map.get(current);

			}
			else if (!map.containsKey(current + theText.charAt(i + 1))) {

				encodeOutput+=map.get(current);

				if (map.size() < 400)//limits the dictionary size to 400 entries
				{
					map.put(current + theText.charAt(i + 1), Integer.toBinaryString(map.size()));
				}

				current = "";

			}
		}


	}

	//Encode the compressed file

	public void writeFile() {
		BinaryOut out = new BinaryOut(path + ".dat");
		for (int i = 0; i < encodeOutput.length(); i++) {
			if (encodeOutput.charAt(i) == '0') {
				out.write(false);
			} else {
				out.write(true);
			}
		}
		out.flush();
	}

	//Decode output binary string

	public void LZWdecompress()
	 {
		HashMap <String, String> decodeMap = new HashMap <String, String>();

		for(int i = 0; i < 256; i++)

		{
			String binaryString = Integer.toBinaryString(i);

			int l = 9-binaryString.length();

			for(int j = 0; j < l; j++){

				binaryString = "0"+binaryString;
			}

			String actualLetters = Character.toString((char)i);

			decodeMap.put(binaryString, actualLetters);

		}

		for(int i = 0; i < encodeOutput.length(); i+=9) {

			String current = decodeMap.get(encodeOutput.substring(i, i+9));

			decodeOutput += current;

			if (i < encodeOutput.length() - 9) {
				String next = decodeMap.get(encodeOutput.substring(i+9, i+18));
				if (next == null) { //special add
					decodeMap.put(Integer.toBinaryString(decodeMap.size()), current + current.substring(0, 1));
				} else {
					decodeMap.put(Integer.toBinaryString(decodeMap.size()), current + next.substring(0, 1));
				}
			}

		}

		try //writes decoded data to output file (decoded.txt)
		{

		BufferedWriter decoded = new BufferedWriter(new FileWriter("decoded.txt"));
		decoded.write(decodeOutput);
		decoded.close();
	}catch(IOException e){}

	}

	public void compressDecompress()//compresses file then decompresses file and ouputs to decoded.txt
	{
		readFile();
		LZWcompress();
		writeFile();
		LZWdecompress();
		System.out.println("Finished in " + (System.nanoTime()-startTime)/1000000 + " miliseconds");
	}

	public static void main (String[]args)
	{
		LZWCompression file1 = new LZWCompression("lzw-file3");
		file1.compressDecompress();
	}
}
