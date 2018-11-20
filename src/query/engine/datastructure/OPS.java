package query.engine.datastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import query.engine.utils.RDFTriple;

/**
 * This class implement a OPS Index 
 */
public class OPS {
	
	private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> opsIndex;
	
	public OPS(BigTable bigTable) {
		opsIndex = new TreeMap<>();
		
		for(int index = 0; index < bigTable.getSize(); index++) {
			int integerSubject = bigTable.getTriple(index).getSubjectDictionaryID();
			int integerPredicate = bigTable.getTriple(index).getPredicateDictionaryID();
			int integerObject = bigTable.getTriple(index).getObjectDictionaryID();
			
			opsIndex.putIfAbsent(integerObject, new TreeMap<>());
			TreeMap<Integer, TreeSet<Integer>> mapedIntegerObject = opsIndex.get(integerObject);
			mapedIntegerObject.putIfAbsent(integerPredicate, new TreeSet<>());
			mapedIntegerObject.get(integerPredicate).add(integerSubject);
		}
	}
	
	/*
     * Getters
     */
	public TreeMap<Integer, TreeSet<Integer>> get(Integer i){
		return opsIndex.get(i);
		
	}
	
	public Set<Integer> getKeySet(){
		return opsIndex.keySet();
	}
	
	public int getSize() {
		return opsIndex.size();
	}
	
	/*
     * Test
     */
	
	/*
	 * This methode has a long computational time.
	 * For each entry in our OPS index, we will look for the entry in the Big Table.
	 */
	public boolean checkIndexHealthIndexToBigTable(BigTable bigTable, DoubleDictionary dictionary) {
		int cpt = 0;
		for (Integer objectKey : opsIndex.keySet())
			for (Integer predicateKey : opsIndex.get(objectKey).keySet())
				for (Integer subject : opsIndex.get(objectKey).get(predicateKey)) {
					RDFTriple triple = new RDFTriple(dictionary.getByID(subject), dictionary.getByID(predicateKey), dictionary.getByID(objectKey));
					triple.setSubjectDictionaryID(dictionary.getByString(triple.getSubjectString()));
					triple.setPredicateDictionaryID(dictionary.getByString(triple.getPredicateString()));
					triple.setObjectDictionaryID(dictionary.getByString(triple.getObjectString()));
					if(!bigTable.checkTriplePresence(triple)) {
						System.out.println("ERROR : " + cpt);
					}
//					System.out.println(cpt);
					cpt++;
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
			if(opsIndex.get(objectInteger).get(predicateInteger) == null) {
				System.out.println("ERROR!! bigTableIndex : " + bigTableIndex);
			}
		}
		return true;
	}

	/*
	 * Logs :
	 * Write the OPS INDEX as a semi-human-readable file
	 */
	public void writeLogs(String filePath) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		
		for (Integer objectKey : opsIndex.keySet())
			for (Integer predicateKey : opsIndex.get(objectKey).keySet())
				for (Integer subject : opsIndex.get(objectKey).get(predicateKey))
					fw.write("OBJECT : " + objectKey + " PREDICATE : " + predicateKey + " SUBJECT : " + subject + "\n");
		fw.close();
	}
}
