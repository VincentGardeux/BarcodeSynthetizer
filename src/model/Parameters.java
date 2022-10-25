package model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Parameters 
{
	public static final String currentVersion = "1.1";
	
	// Input parameters
	public static int length = -1;
	public static int minimum_hamming_distance = 2;
	public static int maximum_homopolymer_length = 2;
	public static float lower_entropy = 1.5f;
	public static float lower_entropy_2 = 2.5f;
	public static float lower_gc_ratio = 35f;
	public static int starting_G = 2;
	public static int ending_T = 2;
	public static String output_file = null;
	public static String input_file = null;
	public static boolean report_freq = false;
	
	// Preloaded list of barcodes
	public static HashSet<String> valid_barcodes = null;
	
	public static void loadParams(String[] args)
	{
		for(int i = 0; i < args.length; i++) 
		{
			if(args[i].startsWith("-"))
			{
				switch(args[i])
				{
					case "-h":
						printHelp();
						System.exit(0);
						break;
					case "-o":
						i++;
						output_file = args[i];
						output_file = output_file.replaceAll("\\\\", "/");
						File f = new File(output_file);
						if(f.exists() && f.isFile()) System.err.println("Output file already exist. It will be overwritten.");
						if(f.exists() && !f.isFile()) new ErrorMessage(output_file + " is not a file.");
						break;
					case "-i":
						i++;
						input_file = args[i];
						input_file = input_file.replaceAll("\\\\", "/");
						f = new File(input_file);
						if(!f.exists()) new ErrorMessage("Input file does not exist at " + input_file);
						if(!f.isFile()) new ErrorMessage(input_file + " is not a file.");
						break;
					case "-l":
						i++;
						try
						{
							length = Integer.parseInt(args[i]);
							if(length < 1) new ErrorMessage("The '-l' option should be followed by an Integer > 0. You entered " + args[i]);
							if(length > 30) new ErrorMessage("The '-l' option should be followed by an Integer <= 30 (remember, this scripts test all possibilities in alphabetical order...). You entered " + args[i]); // Also would not fit in a long if >31 (4^32 > 1E19)
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '-l' option should be followed by an Integer. You entered " + args[i]);
						}
						break;
					case "--min-hamming":
						i++;
						try
						{
							minimum_hamming_distance = Integer.parseInt(args[i]);
							if(minimum_hamming_distance < 0) new ErrorMessage("The '--min-hamming' option should be followed by an Integer >= 0. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--min-hamming' option should be followed by an Integer. You entered " + args[i]);
						}
						break;
					case "--max-homopolymer":
						i++;
						try
						{
							maximum_homopolymer_length = Integer.parseInt(args[i]);
							if(maximum_homopolymer_length < 1) new ErrorMessage("The '--max-homopolymer' option should be followed by an Integer >= 1. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--max-homopolymer' option should be followed by an Integer. You entered " + args[i]);
						}
						break;
					case "--min-gc-ratio":
						i++;
						try
						{
							lower_gc_ratio = Float.parseFloat(args[i]);
							if(lower_gc_ratio > 50) new ErrorMessage("The '--min-gc-ratio' option should provide the lower bound of the range, here it is > 50%. You entered " + args[i]);
							if(lower_gc_ratio < 0) new ErrorMessage("The '--min-gc-ratio' option should provide the lower bound of the range, here it is < 0%. You entered " + args[i]);
							if(lower_gc_ratio < 1) new ErrorMessage("The '--min-gc-ratio' option should be in percent, here it is < 1%. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--min-gc-ratio' option should be followed by a Float. You entered " + args[i]);
						}
						break;
					case "--min-entropy":
						i++;
						try
						{
							lower_entropy = Float.parseFloat(args[i]);
							if(lower_entropy <= 0) new ErrorMessage("The '--min-entropy' option should be followed by an Integer > 0. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--min-entropy' option should be followed by a Float. You entered " + args[i]);
						}
						break;
					case "--min-entropy-2":
						i++;
						try
						{
							lower_entropy_2 = Float.parseFloat(args[i]);
							if(lower_entropy_2 <= 0) new ErrorMessage("The '--min-entropy-2' option should be followed by an Integer > 0. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--min-entropy-2' option should be followed by a Float. You entered " + args[i]);
						}
						break;
					case "--starting-g":
						i++;
						try
						{
							starting_G = Integer.parseInt(args[i]);
							if(starting_G < 0) new ErrorMessage("The '--starting-g' option should be followed by an Integer >= 0. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--starting-g' option should be followed by a Integer. You entered " + args[i]);
						}
						break;
					case "--ending-t":
						i++;
						try
						{
							ending_T = Integer.parseInt(args[i]);
							if(ending_T < 0) new ErrorMessage("The '--ending-t' option should be followed by an Integer >= 0. You entered " + args[i]);
						}
						catch(NumberFormatException nfe)
						{
							new ErrorMessage("The '--ending-t' option should be followed by a Integer. You entered " + args[i]);
						}
						break;
					case "--report-freq":
						report_freq = true;
						break;
					default:
						new ErrorMessage("Unused argument: " + args[i]);
				}
			}
		}
		if(output_file == null) new ErrorMessage("No output file was specified. You can specify an output file by using the '-o' option.\nUse '-h' option to display an help message.");
		if(length == -1) new ErrorMessage("No barcode length was specified. You can specify a barcode length by using the '-l' option.\nUse '-h' option to display an help message.");
		displayHeaderMessage();
		System.out.println("\nConfig: Barcode length = " + length);
		System.out.println("Config: Avoid 'G' in the " + starting_G + " first nucleotides");
		System.out.println("Config: Avoid 'T' in the " + ending_T + " last nucleotides");
		System.out.println("Config: Minimum Hamming distance = " + minimum_hamming_distance);
		System.out.println("Config: Maximum Homopolymer length = " + maximum_homopolymer_length);
		System.out.println("Config: GC ratio should be in [" + lower_gc_ratio + "%, " + (100 - lower_gc_ratio) + "%]");
		System.out.println("Config: Minimum Shannon Entropy (1-mer) = " + lower_entropy);
		System.out.println("Config: Minimum Shannon Entropy (2-mer) = " + lower_entropy_2);
		if(input_file != null) 
		{
			System.out.print("Config: Input barcode file provided... ");
			valid_barcodes = new HashSet<String>();
			try 
			{
				BufferedReader br = new BufferedReader(new FileReader(input_file));
				String line = br.readLine();
				while(line != null)
				{
					valid_barcodes.add(line.trim());
					line = br.readLine();
				}
				br.close();
			}
			catch(IOException ioe)
			{
				new ErrorMessage(ioe.getMessage());
			}
			System.out.println("[" + valid_barcodes.size() + " barcodes]");
		}
		else System.out.println("Config: No input barcode file provided. Starting generation from scratch.");
		System.out.println();
	}
	
	public static void displayHeaderMessage()
	{
		System.out.println("BarcodeSynthetizer " + Parameters.currentVersion);
	}
	
	public static void printHelp()
	{
		displayHeaderMessage();
		System.out.println("\nList of options:");
		System.out.println("\t-l %i\t\t\t[Required] Barcode length");
		System.out.println("\t-o %s\t\t\t[Required] Output file");
		System.out.println("\t-i %s\t\t\t[Optional] Input file (set of barcodes to include / starts with)");
		System.out.println("\t--starting-g %f\t\tAvoid 'G' in the XX first nucleotides [Default = 2]");
		System.out.println("\t--ending-t %f\t\tAvoid 'T' in the XX last nucleotides [Default = 2]");
		System.out.println("\t--min-hamming %i\tMinimum Hamming distance between 2 barcodes [Default = 2]");
		System.out.println("\t--max-homopolymer %i\tMaximum length of homopolymer sequence [Default = 2]");
		System.out.println("\t--min-gc-ratio %f\tMinimum GC ratio [Default = 35, i.e. GC in (35%, 65%)]");
		System.out.println("\t--min-entropy %f\tMinimum Shannon Entropy (1-mer) [Default = 1.5]");
		System.out.println("\t--min-entropy-2 %f\tMinimum Shannon Entropy (2-mer) [Default = 2.5]");
		System.out.println("\t--report-freq\tUse this option to report nucleotide frequencies in generated barcodes");
		System.out.println("\t-h\t\t\tPrint help message");	
	}
}
