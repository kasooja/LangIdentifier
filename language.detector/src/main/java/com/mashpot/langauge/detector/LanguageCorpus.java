package com.mashpot.langauge.detector;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageCorpus {
	// Corpus size
	private Integer noofTotalWords = null;
	private HashMap<String, Integer> termFrequencyMap = null;
	
	//Language of text in corpus. 
	private String language = null;
	
	public LanguageCorpus (Integer noofTotalWords, HashMap<String, Integer> termFrequencyMap, String language) {
		this.noofTotalWords = noofTotalWords;
		this.language = language;
		this.termFrequencyMap = termFrequencyMap;		
	}
	
	public Integer getNoofTotalWords() {
		return noofTotalWords;
	}
	public HashMap<String, Integer> getTermFrequencyMap() {
		return termFrequencyMap;
	}
	public String getLanguage() {
		return language;
	}
	public LanguageCorpus (String corpusFilename, String language)	{	
		// Total words in corpus. Initially set to zero
		this.noofTotalWords = new Integer(0);		
		this.termFrequencyMap= new HashMap<String, Integer>();
		this.language = language;
		createTermFrequencyMap(corpusFilename);
	}
	@SuppressWarnings("unchecked")
	public static LanguageCorpus readLanguageCorpus(String fileToReadObjectFrom) {
		if (!fileToReadObjectFrom.endsWith(".model"))
			return null;
		  ObjectInputStream inputStream = null;
		  try {			  
			  inputStream = new ObjectInputStream(new FileInputStream(fileToReadObjectFrom));
	            
	           Object obj = null;
	           Integer integer = null;
	           String string = null;
	           HashMap<String, Integer> hashmap = null;
	          
	           if ((obj = inputStream.readObject()) != null && obj instanceof String) {
	                string = (String)obj; 
	           }
	           
	           if ((obj = inputStream.readObject()) != null && obj instanceof Integer) {
	                integer = (Integer)obj; 
	           }
	           
	           if ((obj = inputStream.readObject()) != null && obj instanceof HashMap<?, ?>) {
	                hashmap = (HashMap<String, Integer>)obj; 
	           }
	           return new LanguageCorpus(integer, hashmap, string);
	         
	        } catch (EOFException ex) { 
	            System.out.println("End of file reached.");
	        } catch (ClassNotFoundException ex) {
	            ex.printStackTrace();
	        } catch (FileNotFoundException ex) {
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } finally {	            
	            try {
	                if (inputStream != null) {
	                    inputStream.close();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
		return null;
		
	}
	
	/** 	 
	 * Reads the file and creates word frequency map.
	 */
	
	private void createTermFrequencyMap (String corpusFilename) {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(corpusFilename));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		
		Pattern pattern = Pattern.compile("\\w+");	
		int totalTokens = 0;
		try {
			for(String line = ""; line != null; line = in.readLine()){
				Matcher matcher = pattern.matcher(line.toLowerCase());
				while(matcher.find()) { 
					String token = matcher.group();
					++totalTokens;
					if (this.termFrequencyMap.containsKey(token))
						this.termFrequencyMap.put(token, new Integer(this.termFrequencyMap.get(token) + 1));
					else 
						this.termFrequencyMap.put(token, new Integer(1));					
				}
			}
			this.noofTotalWords = new Integer(totalTokens);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
    public void writeWordFrequencyMapToDisk(String pathTowriteObject) throws IOException{
    	ObjectOutputStream objectOutputStream  = null;
    	
    	try {
    		objectOutputStream = new ObjectOutputStream(new FileOutputStream(pathTowriteObject));
    	
    		objectOutputStream.writeObject(this.language);
    		objectOutputStream.writeObject(this.noofTotalWords);
    		objectOutputStream.writeObject(this.termFrequencyMap);
    	} 
    	catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	} 
    	catch (IOException ex) {
    		ex.printStackTrace();
    	}
    	finally {            
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
		}
    }
	
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException	{
		if (args.length != 6) {
			printUsage();
			System.exit(0);
		}
		
		String pathToWriteObjects = args[5];
		
		LanguageCorpus[] languageCorpuses = new LanguageCorpus[5];
		String[] languages = {"ENGLISH", "GERMAN", "FRENCH", "ITALIAN", "DUTCH" };	
		
		for (int i = 0; i < 5; ++i) {
			languageCorpuses[i] = new LanguageCorpus(args[i], languages[i]);
			languageCorpuses[i].writeWordFrequencyMapToDisk(pathToWriteObjects + System.getProperty("file.separator") + languages[i] + ".model" );
		}	
	
		System.out.println("Models are created and saved successfull on disk");   
	}

	private static void printUsage() {
		System.out.println("Please provide the path of training data for following 5 languagues:");
		System.out.println("English");
		System.out.println("German");
		System.out.println("French");
		System.out.println("Italian");
		System.out.println("Dutch");
		System.out.println("Also, please provide the path of directory where training objects can be written");
	}
}