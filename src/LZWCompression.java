import java.io.*;

import java.util.HashMap;

import java.util.*;

public class LZWCompression {

	HashMap <String,String> map;

	String theText = "";

	String output = "";

	//Constructor -- initializes the first 256 values

	public LZWCompression (){
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

	public void readText() {

		try {
			BufferedReader theReader = new BufferedReader (new FileReader("lzw-file1.txt"));



			int w = theReader.read();

			while (w!=-1){

				theText += (char)theReader.read();

				w=theReader.read();
			}

			theReader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		

		

	}

	//Compression algorithm

	public void LZWcompress() {

		//s is the current value in the algorithm

		String current = "";

		//c is the "next" in the algorithm

		String next = "";

		int num = 0;

		int counter = 0;

		for(int i = 0; i < theText.length(); i++) {

			current+=theText.charAt(i);

			if (i == theText.length() - 1) {

				output+=map.get(current);

			} 
			else if (!map.containsKey(current + theText.charAt(i + 1))) {

				output+=map.get(current);

				map.put(current + theText.charAt(i + 1), Integer.toBinaryString(map.size()));

				output = "";

			}
		}
		System.out.println(output);
	}


	public static void main(String[] args) {

		LZWCompression test = new LZWCompression();

		test.readText();

		test.LZWcompress();

	}

}