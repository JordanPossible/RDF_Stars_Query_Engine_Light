package query.engine.datastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import query.engine.utils.RDFTriple;

/**
 * This class implement a POS Index 
 */
public class POS {
	
	private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> posIndex;
	
	public POS(BigTable bigTable) {
		posIndex = new TreeMap<>();
		
		for(int index = 0; index < bigTable.getSize(); index++) {
			int integerSubject = bigTable.getTriple(index).getSubjectDictionaryID();
			int integerPredicate = bigTable.getTriple(index).getPredicateDictionaryID();
			int integerObject = bigTable.getTriple(index).getObjectDictionaryID();
			
			posIndex.putIfAbsent(integerPredicate, new TreeMap<>());
			TreeMap<Integer, TreeSet<Integer>> mapedIntegerPredicate = posIndex.get(integerPredicate);
			mapedIntegerPredicate.putIfAbsent(integerObject, new TreeSet<>());
			mapedIntegerPredicate.get(integerObject).add(integerSubject);
		}
	}
	
	/*
     * Getters
     */
	public TreeMap<Integer, TreeSet<Integer>> get(Integer i){
		return posIndex.get(i);
		
	}
	
	public Set<Integer> getKeySet(){
		return posIndex.keySet();
	}
	
	public int getSize() {
		return posIndex.size();
	}
	
	/*
     * Test
     */
	
	/*
	 * This methode has a long computational time,
	 * For each entry in our POS index, we will look for the entry in the Big Table.
	 */
	public boolean checkIndexHealthIndexToBigTable(BigTable bigTable, DoubleDictionary dictionary) {
		int cpt = 0;
		for (Integer predicateKey : posIndex.keySet())
			for (Integer objectKey : posIndex.get(predicateKey).keySet())
				for (Integer subject : posIndex.get(predicateKey).get(objectKey)) {
					RDFTriple triple = new RDFTriple(dictionary.getByID(subject), dictionary.getByID(predicateKey), dictionary.getByID(objectKey));
					triple.setSubjectDictionaryID(dictionary.getByString(triple.getSubjectString()));
					triple.setPredicateDictionaryID(dictionary.getByString(triple.getPredicateString()));
					triple.setObjectDictionaryID(dictionary.getByString(triple.getObjectString()));
					if(!bigTable.checkTriplePresence(triple)) {
						return false;
					}
//					System.out.println(cpt);
//					cpt++;
				}
		return true;
	}
	
	/*
	 * For each entry in our BigTable, we will look for the entry in the BigTable
	 */
	public boolean checkIndexHealthBigTableToIndex(BigTable bigTable, DoubleDictionary dictionary) {
		for(int bigTableIndex = 0; bigTableIndex < bigTable.getSize(); bigTableIndex++) {
//			int subjectInteger = bigTable.getTriple(bigTableIndex).getSubjectDictionaryID();
			int predicateInteger = bigTable.getTriple(bigTableIndex).getPredicateDictionaryID();
			int objectInteger = bigTable.getTriple(bigTableIndex).getObjectDictionaryID();
			if(posIndex.get(predicateInteger).get(objectInteger) == null) {
				System.out.println("ERROR!! bigTableIndex : " + bigTableIndex);
			}
		}
		return true;
	}

	/*
	 * Logs :
	 * Write the POS INDEX as a semi-human-readable file
	 */
	public void writeLogs(String filePath) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		
		for (Integer predicateKey : posIndex.keySet())
			for (Integer objectKey : posIndex.get(predicateKey).keySet())
				for (Integer subject : posIndex.get(predicateKey).get(objectKey))
					fw.write("PREDICATE : " + predicateKey + " OBJECT : " + objectKey + " SUBJECT : " + subject + "\n");
		fw.close();
	}
}



