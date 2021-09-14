
public class LZWMain {
	public static void main (String[]args)
	{
		
			LZWCompression test = new LZWCompression();
			test.readText();
			test.LZWcompress();
			test.writeToFile();
		
	}
}
