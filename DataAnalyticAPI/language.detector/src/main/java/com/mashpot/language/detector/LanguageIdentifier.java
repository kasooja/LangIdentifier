package com.mashpot.language.detector;


import java.io.*;
import java.util.*;

public class LanguageIdentifier {
	
	// To store the similarity measure with language.
	static class SimiliarityAndLanguage {
		public SimiliarityAndLanguage(double similiarity, String language) {
			this.similiarity = similiarity;
			this.language = language;
		}
		double similiarity = Double.NaN;
		String language = null;
	}
	
	
	/*
	 * main
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}
		
		String pathToReadObjects = args[args.length - 1];
		ArrayList<LanguageCorpus> languageCorpus = readModels (pathToReadObjects); 
		for (int i = 0; i < args.length -1; ++i) {
			LanguageCorpus languageCorpusTobeIdentified = new LanguageCorpus(args[i], null);
			String language = detectLanguage (languageCorpus, languageCorpusTobeIdentified);
			System.out.println("The language of file " + args[i] + " is " + language);
		}
	}

	// Calculate similarity measure of given file with all the known models and returns the language of model having highest similarity measure.
	private static String detectLanguage(
			ArrayList<LanguageCorpus> languageCorpus,
			LanguageCorpus languageCorpusTobeIdentified) {
		Iterator<LanguageCorpus> iteLanguageCorpus = languageCorpus.iterator();
		SimiliarityAndLanguage maxSimiliaritySeen = null;
		while (iteLanguageCorpus.hasNext()) {
			SimiliarityAndLanguage similiarityAndLanguage = findSimilarity (iteLanguageCorpus.next(), languageCorpusTobeIdentified);
			if (maxSimiliaritySeen == null)
				maxSimiliaritySeen = similiarityAndLanguage;
			else if (similiarityAndLanguage != null) {
				if (similiarityAndLanguage.similiarity > maxSimiliaritySeen.similiarity)
					maxSimiliaritySeen = similiarityAndLanguage;
			}
		}
		if (maxSimiliaritySeen != null)
			return maxSimiliaritySeen.language;
		return null;
	}

	// Calculate the cosine similarity.
	private static SimiliarityAndLanguage findSimilarity(LanguageCorpus languageCorpusIndentifiedAgainst,
			LanguageCorpus languageCorpusTobeIdentified) {		
		double dotProduct = 0;
		int languageCorpusIndentifiedAgainstSize = languageCorpusIndentifiedAgainst.getNoofTotalWords();
		if (languageCorpusIndentifiedAgainstSize == 0)
			return null;
		Iterator<String> itelanguageCorpusTobeIdentified = languageCorpusTobeIdentified.getTermFrequencyMap().keySet().iterator();
		
		while (itelanguageCorpusTobeIdentified.hasNext()) {
			String key = itelanguageCorpusTobeIdentified.next();
			int val1 = languageCorpusTobeIdentified.getTermFrequencyMap().get(key);
			if (languageCorpusIndentifiedAgainst.getTermFrequencyMap().containsKey(key)) {
				dotProduct += val1 * languageCorpusIndentifiedAgainst.getTermFrequencyMap().get(key);
			}
		}		
		return new SimiliarityAndLanguage( dotProduct / languageCorpusIndentifiedAgainstSize, languageCorpusIndentifiedAgainst.getLanguage());
	}

    // Reads all five models into arraylist
	private static ArrayList<LanguageCorpus> readModels(String pathToReadObjects) {
		File file = new File(pathToReadObjects);
		File[] files = file.listFiles();
		ArrayList<LanguageCorpus> arrayLanguageCorpus = new ArrayList<LanguageCorpus>();
		for (int i = 0; i < files.length; ++i) {
			LanguageCorpus languageCorpus = LanguageCorpus.readLanguageCorpus(files[i].getAbsolutePath());
			if (languageCorpus != null)
				arrayLanguageCorpus.add(languageCorpus);
		}
		return arrayLanguageCorpus;
	}

	private static void printUsage() {
		System.out.println("provide the space sepatated paths of files you wish to identify the languages.");
		System.out.println("Then provide the path of objects stored on disk");		
	}
}
