package barcode;

import java.util.HashMap;
import java.util.HashSet;

import tools.Utils;

public class Barcode 
{
	public char[] sequence = null;
	private int last = -1;
	private boolean hasNext = true;
	
	public Barcode(int l) 
	{
		sequence = new char[l];
		for(int i = 0; i < sequence.length; i++) sequence[i] = Nucleotide.first;
		last = l - 1;
	}
	
	public boolean startingG(int n) // n first bases should not be G
	{
		if(n == 0) return false;
		for(int i = 1; i <= n; i++)
		{
			if(sequence[i - 1] == 'G') return true;
		}
		return false;
	}
	
	public boolean endingT(int n) // n last bases should not be T
	{
		if(n == 0) return false;
		for(int i = 1; i <= n; i++)
		{
			if(sequence[last - i + 1] == 'T') return true;
		}
		return false;
	}
		
	public HashMap<Character, Integer> createCharacterMap()
	{
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		for(int i = 0; i < sequence.length; i++) 
		{
			char currentChar = sequence[i];
			Integer val = map.get(currentChar);
			if(val == null) val = 0;
			map.put(currentChar, val + 1);
		}
		return map;
	}
	
	public HashMap<String, Integer> createTwoMERMap()
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < sequence.length - 1; i++) 
		{
			char[] ca = { sequence[i], sequence[i + 1] };
			String two_mer = String.valueOf(ca); // 2-mer
			Integer val = map.get(two_mer);
			if(val == null) val = 0;
			map.put(two_mer, val + 1);
		}
		return map;
	}
	
	public float gc_content(HashMap<Character, Integer> map)
	{
		Integer nbG = map.get('G');
		if(nbG == null) nbG = 0;
		Integer nbC = map.get('C');
		if(nbC == null) nbC = 0;
		return 100f*(nbG + nbC) / sequence.length;
	}
	
	public int maximum_homopolymer(HashMap<Character, Integer> map) // Calculate maximum length of homopolymers
	{
        int maximum_homopolymer = 0;
        int homo = 0;
        char letter = 'X';
		for(int i = 0; i < sequence.length; i++) 
		{
			char currentChar = sequence[i];
			if(letter == currentChar) homo++;
			else 
			{
				if(homo > maximum_homopolymer) maximum_homopolymer = homo;
				homo = 1;
				letter = currentChar;
			}
		}
		if(homo > maximum_homopolymer) maximum_homopolymer = homo;
		return maximum_homopolymer;
	}
	
	public int hammingDistance(HashSet<String> list)
	{	
		int minHamming = Integer.MAX_VALUE;
		for(String b:list)
		{
			int d = Utils.hammingDistance(this.sequence, b);
			if(d < minHamming) minHamming = d;
		}
		return minHamming;
	}
	
	public boolean hasNext()
	{
		return hasNext;
	}
	
	public void next()
	{
		sequence[last] = Nucleotide.next(sequence[last]);
		for(int i = last; i >= 0; i--)
		{
			if(sequence[i] == Nucleotide.first) 
			{
				if(i == 0) hasNext = false;
				else sequence[i - 1] = Nucleotide.next(sequence[i - 1]);
			}
			else break;
		}
	}
	
	public String toString() 
	{
		return String.valueOf(sequence);
	}	
}
