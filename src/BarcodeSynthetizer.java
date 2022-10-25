import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import barcode.Barcode;
import barcode.Nucleotide;
import model.ErrorMessage;
import model.Parameters;
import tools.Utils;

public class BarcodeSynthetizer 
{
	public static void main(String[] args) 
	{
		Parameters.loadParams(args);
		if(Parameters.sequential) generateSequentially();
		else generateRandomly();
	}
	
	/**
	 * This method generate the barcodes by first filtering impossible barcodes (according to parameters), and storing the leftovers in the RAM
	 * Then, it randomize the stored barcodes, and proceed, with the final rules
	 * 
	 * @note Probably should use better storage (not HashSet) and better representation (4 integers?)
	 * @author Vincent Gardeux
	 */
	private static void generateRandomly()
	{
		Random rnd = new Random(Parameters.seed);
		long start = System.currentTimeMillis();
		// 1. First pass: generating all barcodes and storing the potential ok ones.
		Nucleotide.init();
		Barcode b = new Barcode(Parameters.length);
		long nbBarcodes = 0;
		long totalBarcodes = (long)Math.pow(4, Parameters.length);
		int threshold = 1;
		List<String> potential_barcodes = new ArrayList<String>();
		while(b.hasNext())
		{
			if(!b.startingG(Parameters.starting_G)) // Should not have G in the 2 first bases
			{
				if(!b.endingT(Parameters.ending_T)) // Should not have T in the 2 last bases
				{
					HashMap<Character, Integer> map = b.createCharacterMap();
					if(Utils.is_between(b.gc_content(map), Parameters.lower_gc_ratio, 100 - Parameters.lower_gc_ratio)) // GC content comprised between the limits
					{
						if(b.maximum_homopolymer(map) <= Parameters.maximum_homopolymer_length) // Test max Homopolymer length
						{
							if(Utils.shannon_entropy(Parameters.length, map) >= Parameters.lower_entropy) // Minimum Shannon entropy (1-mer)
							{
								HashMap<String, Integer> map2 = b.createTwoMERMap();
								if(Utils.shannon_entropy2(Parameters.length, map2) >= Parameters.lower_entropy_2) // Minimum Shannon entropy (2-mer)
								{
									potential_barcodes.add(b.toString());
								}
							}
						}
					}
				}
			}
			b.next();
			nbBarcodes++;
			if(100 * nbBarcodes / totalBarcodes > threshold)
			{
				System.out.println("First pass (no Hamming calculation): " + nbBarcodes + " processed [" + threshold + "%] in "+Utils.toReadableTime(System.currentTimeMillis() - start));
				threshold++;
			}
		}
		System.out.println("First pass over: " + nbBarcodes + " barcodes were tested [" + potential_barcodes.size() + " potential one(s)");
		// 2. Second pass: Randomize and filter remaining barcodes.
		System.out.println("\nNow running second pass.");
		long start2 = System.currentTimeMillis();
		Collections.shuffle(potential_barcodes, rnd);
		long newBarcodes = 0;
		totalBarcodes = potential_barcodes.size();
		nbBarcodes = 0;
		threshold = 1;
		HashSet<String> valid_barcodes = (Parameters.valid_barcodes != null)?Parameters.valid_barcodes:new HashSet<String>();
		for(String b1:potential_barcodes)
		{
			// Only Hamming distance is left to check
			int minHamming = Integer.MAX_VALUE;
			for(String b2:valid_barcodes)
			{
				int d = Utils.hammingDistance(b1, b2);
				if(d < minHamming) minHamming = d;
			}
			if(minHamming >= Parameters.minimum_hamming_distance) // Minimum Hamming distance to set of valid barcodes
			{
				newBarcodes++;
				valid_barcodes.add(b1);
			}
			nbBarcodes++;
			if(100 * nbBarcodes / totalBarcodes > threshold)
			{
				System.out.println("Second pass (only Hamming calculation): " + nbBarcodes + " processed [" + threshold + "%] in "+Utils.toReadableTime(System.currentTimeMillis() - start2));
				threshold++;
			}
		}
		System.out.println("Second pass over: " + nbBarcodes + " barcodes were tested [" + valid_barcodes.size() + " valid one(s), "+ newBarcodes +" new one(s)]");
		System.out.println("Total synthesis done in " + Utils.toReadableTime(System.currentTimeMillis() - start));
	
		float[][] frequencies = new float[4][Parameters.length];
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.output_file));
			for(String s:valid_barcodes) 
			{
				bw.write(s + "\n");
				if(Parameters.report_freq)
				{
					for(int i = 0; i < s.length(); i++)
					{
						switch(s.charAt(i))
						{
							case 'A':
								frequencies[0][i] += 1f/valid_barcodes.size();
								break;
							case 'C':
								frequencies[1][i] += 1f/valid_barcodes.size();
								break;
							case 'G':
								frequencies[2][i] += 1f/valid_barcodes.size();
								break;
							case 'T':
								frequencies[3][i] += 1f/valid_barcodes.size();
								break;
						}
					}
				}
			}
			bw.close();
		}
		catch(IOException ioe)
		{
			new ErrorMessage(ioe.getMessage());
		}
		// Print report freq
		if(Parameters.report_freq)
		{
			System.out.print("A");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[0][i]);
			System.out.println();
			System.out.print("C");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[1][i]);
			System.out.println();
			System.out.print("G");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[2][i]);
			System.out.println();
			System.out.print("T");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[3][i]);
			System.out.println();
		}
	}
	
	/**
	 * This method generate the barcodes without using the RAM.
	 * However, it generates them sequentially, from AAAAAAAA[...]AAAA to TTTTTTTTT[...]TTTTT
	 * This can bias the results towards more barcodes starting with A, then C, then G, then T
	 * @deprecated
	 * @author Vincent Gardeux
	 */
	private static void generateSequentially()
	{
		long start = System.currentTimeMillis();
		Nucleotide.init();
		Barcode b = new Barcode(Parameters.length);
		long nbBarcodes = 0;
		long totalBarcodes = (long)Math.pow(4, Parameters.length);
		long newBarcodes = 0;
		int threshold = 1;
		HashSet<String> valid_barcodes = (Parameters.valid_barcodes != null)?Parameters.valid_barcodes:new HashSet<String>();
		while(b.hasNext())
		{
			if(!b.startingG(Parameters.starting_G)) // Should not have G in the 2 first bases
			{
				if(!b.endingT(Parameters.ending_T)) // Should not have T in the 2 last bases
				{
					HashMap<Character, Integer> map = b.createCharacterMap();
					//if(map.size() >= 3) // Different nucleotides diversity (at least 3/4) // [EDIT] Commented because should be handled by entropy
					//{
						if(Utils.is_between(b.gc_content(map), Parameters.lower_gc_ratio, 100 - Parameters.lower_gc_ratio)) // GC content comprised between the limits
						{
							if(b.maximum_homopolymer(map) <= Parameters.maximum_homopolymer_length) // Test max Homopolymer length
							{
								if(b.hammingDistance(valid_barcodes) >= Parameters.minimum_hamming_distance) // Minimum Hamming distance to set of valid barcodes
								{
									if(Utils.shannon_entropy(Parameters.length, map) >= Parameters.lower_entropy) // Minimum Shannon entropy (1-mer)
									{
										HashMap<String, Integer> map2 = b.createTwoMERMap();
										if(Utils.shannon_entropy2(Parameters.length, map2) >= Parameters.lower_entropy_2) // Minimum Shannon entropy (2-mer)
										{
											newBarcodes++;
											valid_barcodes.add(b.toString());
										}
									}
								}
							}
						}
					//}
				}
			}
			b.next();
			nbBarcodes++;
			if(100 * nbBarcodes / totalBarcodes > threshold)
			{
				System.out.println(nbBarcodes + " processed [" + threshold + "%] in "+Utils.toReadableTime(System.currentTimeMillis() - start));
				threshold++;
			}
		}
		System.out.println(nbBarcodes + " barcodes were tested [" + valid_barcodes.size() + " valid one(s), "+ newBarcodes +" new one(s)]");
		System.out.println("Synthesis done in " + Utils.toReadableTime(System.currentTimeMillis() - start));
	
		float[][] frequencies = new float[4][Parameters.length];
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.output_file));
			for(String s:valid_barcodes) 
			{
				bw.write(s + "\n");
				if(Parameters.report_freq)
				{
					for(int i = 0; i < s.length(); i++)
					{
						switch(s.charAt(i))
						{
							case 'A':
								frequencies[0][i] += 1f/valid_barcodes.size();
								break;
							case 'C':
								frequencies[1][i] += 1f/valid_barcodes.size();
								break;
							case 'G':
								frequencies[2][i] += 1f/valid_barcodes.size();
								break;
							case 'T':
								frequencies[3][i] += 1f/valid_barcodes.size();
								break;
						}
					}
				}
			}
			bw.close();
		}
		catch(IOException ioe)
		{
			new ErrorMessage(ioe.getMessage());
		}
		// Print report freq
		if(Parameters.report_freq)
		{
			System.out.print("A");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[0][i]);
			System.out.println();
			System.out.print("C");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[1][i]);
			System.out.println();
			System.out.print("G");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[2][i]);
			System.out.println();
			System.out.print("T");
			for(int i = 0; i < Parameters.length; i++) System.out.print("\t" + frequencies[3][i]);
			System.out.println();
		}
	}
}
