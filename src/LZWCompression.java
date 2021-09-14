import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.nio.file.*;

public class LZWCompression {

	HashMap <String,String> map;

	String theText = "";

	String output = "";

	String inputPath = "";

	//Constructor -- initializes the first 256 values

	public LZWCompression (String path){
		inputPath = path;
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
			theText = Files.readString(Paths.get(inputPath + ".txt"), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		System.out.println(theText);

		

	}

	//Compression algorithm

	public void LZWcompress() {

		output = "";
		String current = "";

		for(int i = 0; i < theText.length(); i++) {

			current+=theText.charAt(i);

			if (i == theText.length() - 1) {

				output+=map.get(current);

			} 
			else if (!map.containsKey(current + theText.charAt(i + 1))) {

				output+=map.get(current);

				map.put(current + theText.charAt(i + 1), Integer.toBinaryString(map.size()));

				current = "";

			}
		}
		System.out.println(output);
	}

	//Encode the compressed file

	public void writeFile() {
		BinaryOut out = new BinaryOut(inputPath + ".dat");
		for (int i = 0; i < output.length(); i++) {
			if (output.charAt(i) == '0') {
				out.write(false);
			} else {
				out.write(true);
			}
		}
		out.flush();
	}
}