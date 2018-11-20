package query.engine.datastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import query.engine.utils.RDFTriple;

/**
 * This class implements a dictionary which can be accessed from 2 datastructure :
 * stringKeyDictionary which offer an access by String
 * integerKeyDictionary which offer an access by Integer
 */
public class DoubleDictionary {

	private TreeMap<String, Integer> stringKeyDictionary;
	private ArrayList<String> integerKeyDictionary;
	
	/*
	 * We iterate over the Big Table 
	 * For each RDFTriple, we check if the dictionaries contains this triple
	 * If not, we add an entry for the considered subject, predicate or object in the 2 dictionaries
	 */
	public DoubleDictionary(BigTable bigTable) {
		stringKeyDictionary = new TreeMap<String, Integer>();
		integerKeyDictionary = new ArrayList<String>();
		
		int dictionarySize = 0;
		for(int index = 0; index < bigTable.getSize(); index++) {
			
			if(!stringKeyDictionary.containsKey(bigTable.getTriple(index).getSubjectString())) {
				stringKeyDictionary.put(bigTable.getTriple(index).getSubjectString(), dictionarySize);
				integerKeyDictionary.add(bigTable.getTriple(index).getSubjectString());
				dictionarySize++;
			}
			if(!stringKeyDictionary.containsKey(bigTable.getTriple(index).getObjectString())) {
				stringKeyDictionary.put(bigTable.getTriple(index).getObjectString(), dictionarySize);
				integerKeyDictionary.add(bigTable.getTriple(index).getObjectString());
				dictionarySize++;
			}
			if(!stringKeyDictionary.containsKey(bigTable.getTriple(index).getPredicateString())) {
				stringKeyDictionary.put(bigTable.getTriple(index).getPredicateString(), dictionarySize);
				integerKeyDictionary.add(bigTable.getTriple(index).getPredicateString());
				dictionarySize++;
			}
		}
	}
	
	/*
     * Getters
     */
	public String getByID(int id) {
		return this.integerKeyDictionary.get(id);
	}
	
	public int getByString(String s) {
		if(stringKeyDictionary.containsKey(s)) {
			return this.stringKeyDictionary.get(s);
		}
		return -1;
	}
	
	/*
     * Test
     */
	
	/*
	 * This methodes will check the consistence of the dictionary
	 * by getting the value from fromIntegerKeyDictionary to fromStringKeyDictionary
	 * and checking wether these values correspond to the considered index
	 */
	public boolean checkDictionaryHealth() {
		if(integerKeyDictionary.size() != stringKeyDictionary.size()) return false;
		for(int index = 0; index < integerKeyDictionary.size(); index++) {
			String fromIntegerKeyDictionary = integerKeyDictionary.get(index);
			int fromStringKeyDictionary = stringKeyDictionary.get(fromIntegerKeyDictionary);
			if(fromStringKeyDictionary != index) {
				System.out.println(" Dictionary ERROR ");
				return false;
			}
		}
		return true;
	}

	/*
	 * Logs :
	 * Write the Dictionary as a semi-human-readable file
	 */
	public void writeLogs(String filePath) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		
		for(int index = 0; index < integerKeyDictionary.size(); index++) {
			String fromIntegerKeyDictionary = integerKeyDictionary.get(index);
			int fromStringKeyDictionary = stringKeyDictionary.get(fromIntegerKeyDictionary);
			fw.write("integerKeyDictionary at INDEX : " + index + " String fromIntegerKeyDictionary : " 
					+ fromIntegerKeyDictionary + " int fromStringKeyDictionary : " + fromStringKeyDictionary + " \n");
		}
		
		fw.write("\n The integerKeyDictionary contains : " + integerKeyDictionary.size() + " entry.");
		fw.write("\n The stringKeyDictionary contains : " + stringKeyDictionary.size() + " entry.");
		fw.close();
	}
}



