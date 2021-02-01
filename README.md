![](https://img.shields.io/badge/build-passing-green.svg)
![](https://img.shields.io/badge/version-1.0-blue.svg)
![](https://img.shields.io/badge/java-1.8-red.svg)

# BarcodeSynthetizer 1.0

A tool for generating dna barcodes (A,C,G,T) of any length, following a given set of rules.

## Download software
BarcodeSynthetizer is provided as a [single executable jar file](../master/release/BarcodeSynthetizer-1.0.jar?raw=true).
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
java -jar BarcodeSynthetizer-1.0.jar -h
```
The output should look like:

```
BarcodeSynthetizer 1.0

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
        -h                      Print help message
```
