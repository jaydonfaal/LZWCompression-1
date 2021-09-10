
import java.io.*;
import java.util.HashMap;
import java.util.*;
public class LZWCompression {
	private static final int BIT_0 = 1;

	/** Mask for bit 1 of a byte. */
	private static final int BIT_1 = 0x02;

	/** Mask for bit 2 of a byte. */
	private static final int BIT_2 = 0x04;
 
	/** Mask for bit 3 of a byte. */
	private static final int BIT_3 = 0x08;
     
	/** Mask for bit 4 of a byte. */
	private static final int BIT_4 = 0x10;

	/** Mask for bit 5 of a byte. */
	private static final int BIT_5 = 0x20;

	/** Mask for bit 6 of a byte. */
	private static final int BIT_6 = 0x40;

	/** Mask for bit 7 of a byte. */
	private static final int BIT_7 = 0x80;

	private static final int[] BITS = { BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5, BIT_6, BIT_7 };

	HashMap <String,String> map;
	String theText = "";

	public LZWCompression (){
		map = new HashMap<String, String>();
		for(int i = 0; i < 256; i++)
		{
			String binaryString = Integer.toBinaryString(i);                                            
			int l = 9-binaryString.length();
			for(int j = 0; j < l; j++)
			{
				binaryString = "0"+binaryString;
			}
			String actualLetters = Character.toString((char)i);
			map.put(actualLetters, binaryString);
		}
	}

	private void readText() throws IOException
	{
		//String theText = "";
		BufferedReader theReader = new BufferedReader (new FileReader("lzw-file1.txt"));

		int w = theReader.read();
		while (w!=-1)
		{
			theText += (char)theReader.read();
			w=theReader.read();
		}
		theReader.close();
	}

	public void LZWcompress() {
		String s = "";
		String c = "";
		int counter = 0;
		s+=theText.charAt(counter);
		while(theText.charAt(counter)!=-1) {
			c=theText.substring(counter+1,counter+2);
			if (map.containsValue(s+c)){
				s=s+c;
				counter++;
			}
			else
			{
				String binString=Integer.toBinaryString(256+counter);
				map.put(binString,(s+c));
				counter++;
				s=c;
			}
		}
			
	}

	public static void writeToFile(String str) throws IOException
	{
		char[] ascii = str.toCharArray();
		byte[] l_raw = new byte[ascii.length >> 3];
		/*
		 * We decr index jj by 8 as we go along to not recompute indices using
		 * multiplication every time inside the loop.
		 */
		for (int ii = 0, jj = ascii.length - 1; ii < l_raw.length; ii++, jj -= 8) {
			for (int bits = 0; bits < BITS.length; ++bits) {
				if (ascii[jj - bits] == '1') {
					l_raw[ii] |= BITS[bits];
				}
			}
		}
		FileOutputStream fos = new FileOutputStream("output.bin");
		fos.write(l_raw);
	}
}


