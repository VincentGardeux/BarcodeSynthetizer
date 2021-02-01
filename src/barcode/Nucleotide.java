package barcode;

import java.util.HashMap;

public class Nucleotide 
{
	private static HashMap<Character, Character> map;
	public static char first = 'A';
	
	public static void init()
	{
		map = new HashMap<Character, Character>();
		map.put('A', 'C');
		map.put('C', 'G');
		map.put('G', 'T');
		map.put('T', 'A');
	}
	
	public static char next(char nuc)
	{
		return map.get(nuc);
	}
	
}
