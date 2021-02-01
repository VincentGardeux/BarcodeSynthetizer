import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import barcode.Barcode;
import barcode.Nucleotide;
import model.ErrorMessage;
import model.Parameters;
import tools.Utils;

public class BarcodeSynthetizer 
{
	public static void main(String[] args) 
	{
		long start = System.currentTimeMillis();
		Parameters.loadParams(args);
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
	
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.output_file));
			for(String s:valid_barcodes) bw.write(s + "\n");
			bw.close();
		}
		catch(IOException ioe)
		{
			new ErrorMessage(ioe.getMessage());
		}
	}

}
