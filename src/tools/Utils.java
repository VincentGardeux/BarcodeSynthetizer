package tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import model.ErrorMessage;

public class Utils 
{
	private static final double LOG_2 = Math.log(2);
	
	public static String[] sortKeys(Map<String, Integer> map)
	{
		String[] keys = map.keySet().toArray(new String[map.keySet().size()]);
		Arrays.sort(keys);
		return keys;
	}
	
	public static String toString(HashSet<String> str)
	{
		if(str == null) return "";
		StringBuffer sb = new StringBuffer();
		String prefix = "";
		for(String s:str)
		{
			sb.append(prefix).append(s);
			prefix = ",";
		}
		return sb.toString();
	}
	
	public static boolean is_between(float x, float a, float b) // Check if x is between a and b
	{
		 return ((x - a)  *  (b - x)) > 0;
	}

	/**
	 * @param sequence
	 * @param map
	 * @return Count the frequency of occurrence of each character
	 */
	public static double shannon_entropy(int length, HashMap<Character, Integer> map)
	{
	    double result = 0.0;
	    for(Character item:map.keySet())
	    {
	        float frequency = (float)map.get(item) / length;
	        result -= frequency * (Math.log(frequency) / LOG_2);
	    }
	    return result;
	}
	
	public static int hammingDistance(String a, String b) 
	{
		// Check if we can compute the distance
		if (a == null || b == null) new ErrorMessage("Strings must not be null");
		int l = a.length();
		if (l != b.length()) new ErrorMessage("Strings must have the same length");
		
		// Compute the distance
		int distance = 0;
		for(int i = 0; i < l; i++) if (a.charAt(i) != b.charAt(i)) distance++;
		
		return distance;
	}
	
	public static int hammingDistance(char[] a, String b) 
	{
		// Check if we can compute the distance
		if (a == null || b == null) new ErrorMessage("Strings must not be null");
		int l = a.length;
		if (l != b.length()) new ErrorMessage("Strings must have the same length");
		
		// Compute the distance
		int distance = 0;
		for(int i = 0; i < l; i++) if (a[i] != b.charAt(i)) distance++;
		
		return distance;
	}
	
	/**
	 * @param sequence
	 * @param map
	 * @return Count the frequency of occurrence of each 2-mer
	 */
	public static double shannon_entropy2(int length, HashMap<String, Integer> map)
	{	
	    double result = 0.0;
	    if(length < 2) new ErrorMessage("Cannot compute entropy2 with length < 2");   
	    // Calculate frequency of 2-mers and generate entropy
	    for(String item:map.keySet())
	    {
	        float frequency = (float)map.get(item) / (length - 1); // length - 1 because there is one less 2-mer than the length of the string
	        result -= frequency * (Math.log(frequency) / LOG_2);
	    }
	    return result;
	}
	
	public static String toReadableTime(long ms)
	{
		if(ms < 1000) return ""+ms+" ms";
		long s = ms / 1000;
		ms = ms % 1000;
		if(s < 60) return s+" s "+ms+" ms";
		long mn = s / 60;
		s = s % 60;
		if(mn < 60) return mn +" mn "+s+" s "+ms+" ms";
		long h = mn / 60;
		mn = mn % 60;
		if(h < 24) return h +" h "+ mn +" mn "+s+" s "+ms+" ms";
		long d = h / 24;
		h = h % 24;
		return d+ " d " + h +" h "+ mn +" mn "+s+" s "+ms+" ms";
	}
}
