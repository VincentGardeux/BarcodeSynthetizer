![](https://img.shields.io/badge/build-passing-green.svg)
![](https://img.shields.io/badge/version-1.1-blue.svg)
![](https://img.shields.io/badge/java-1.8-red.svg)

# DNA BarcodeSynthetizer 1.1

A tool for generating dna barcodes (A,C,G,T) of any length, following a given set of rules.

## Download software
BarcodeSynthetizer is provided as a [single executable jar file](../master/release/BarcodeSynthetizer-1.1.jar?raw=true).
The .jar file contains all required materials and can be run on any terminal.

## Dependencies
### Java version
For the tools to run properly, you must have Java >= 1.8 installed. 

To check your java version, open your terminal application and r
un the following command:

```bash
java -version
```

If the output looks something like java version "1.8.x", you are good to go. 
If not, you may need to update your version; see the [Oracle Java website](http://www.oracle.com/technetwork/java/javase/downloads/) to download the latest JRE (for users) or JDK (for developers).

## Usage
To check that BarcodeSynthetizer is working properly, run the following command:

```bash
java -jar BarcodeSynthetizer-1.1.jar -h
```
The output should look like:

```
BarcodeSynthetizer 1.1

List of options:
        -l %i                   [Required] Barcode length
        -o %s                   [Required] Output file
        -i %s                   [Optional] Input file (set of barcodes to include / starts with)
        --starting-g %f         Avoid 'G' in the XX first nucleotides [Default = 2]
        --ending-t %f           Avoid 'T' in the XX last nucleotides [Default = 2]
        --min-hamming %i        Minimum Hamming distance between 2 barcodes [Default = 2]
        --max-homopolymer %i    Maximum length of homopolymer sequence [Default = 2]
        --min-gc-ratio %f       Minimum GC ratio [Default = 35, i.e. GC in (35%, 65%)]
        --min-entropy %f        Minimum Shannon Entropy (1-mer) [Default = 1.5]
        --min-entropy-2 %f      Minimum Shannon Entropy (2-mer) [Default = 2.5]
        --report-freq           Use this option to report nucleotide frequencies in generated barcodes
        --sequential            Use this option to use the obsolete one-pass 'sequential' algorithm
        --seed %i               Change the random seed for the two-pass 'randomized' algorithm
        -h                      Print help message
```

## Example

```bash
java -jar /software/BarcodeSynthetizer-1.0.jar -l 12 --min-hamming 6 -o barcode.list.txt
```

Generates the following output:

```
BarcodeSynthetizer 1.0

Config: Barcode length = 12
Config: Avoid 'G' in the 2 first nucleotides
Config: Avoid 'T' in the 2 last nucleotides
Config: Minimum Hamming distance = 6
Config: Maximum Homopolymer length = 2
Config: GC ratio should be in [35.0%, 65.0%]
Config: Minimum Shannon Entropy (1-mer) = 1.5
Config: Minimum Shannon Entropy (2-mer) = 2.5
Config: No input barcode file provided. Starting generation from scratch.

16777216 barcodes were tested [337 valid one(s), 337 new one(s)]
Synthesis done in 17 s 833 ms
```

And generate a text [file with 337 barcodes](../master/example/barcode.list.txt?raw=true) (one per line)

## Remarks
- This script is not an optimization algorithm to find for e.g. the larger possible barcode set.
- Shannon's entropy functions were implemented in Java to generate exactly the same results as the entropy (first order entropy = 1-mer entropy) and entropy2 (second order entropy = 2-mer entropy) functions from the [acss package in R](https://github.com/singmann/acss/)
