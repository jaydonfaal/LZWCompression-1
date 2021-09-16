import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.nio.file.*;

public class LZWCompression {

	HashMap <String,String> map;

	String theText = "";	

	String path = "";

	String encodeOutput = "";

	String decodeOutput = "";

	//Constructor -- initializes the first 256 values

	public LZWCompression (String path){
		this.path = path;
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

		for(int i = 0; i < theText.length(); i++) {

			current+=theText.charAt(i);

			if (i == theText.length() - 1) {

				encodeOutput+=map.get(current);

			} 
			else if (!map.containsKey(current + theText.charAt(i + 1))) {

				encodeOutput+=map.get(current);

				if (map.size() < 512) {
					map.put(current + theText.charAt(i + 1), Integer.toBinaryString(map.size()));
				}

				current = "";

			}
		}
		
		System.out.println(encodeOutput);
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

	public void LZWdecompress() {
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

		System.out.println(decodeOutput);
	}
}