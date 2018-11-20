package query.engine.datastructure;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import query.engine.utils.RDFTriple;

/**
 * This class implements a simple representation of what a Big Table is,
 * The RDFTriples will be stored in an ArrayList 
 * which we will interact with through getters and setters
 */
public class BigTable {
	
	private List<RDFTriple> bigTable;
	
	public BigTable() {
		bigTable = new ArrayList<RDFTriple>();
	}
	
    /*
     * Getters
     */ 
    public RDFTriple getTriple(int index) {
    	return this.bigTable.get(index);
    }
    
	public int getSize() {
		return this.bigTable.size();
	}
	
    /*
     * Setters
     */
	public void insertTriple(RDFTriple triple) {
		this.bigTable.add(triple);
	}
	
	public void setAllDictionaryID(DoubleDictionary dictionary) {
		for(RDFTriple triple : bigTable) {
			triple.setSubjectDictionaryID(dictionary.getByString(triple.getSubjectString()));
			triple.setObjectDictionaryID(dictionary.getByString(triple.getObjectString()));
			triple.setPredicateDictionaryID(dictionary.getByString(triple.getPredicateString()));
		}
	}
	
	 /*
     * Test
     */
	public boolean checkTriplePresence(RDFTriple tripleToFind) {
		int intSubjectToFind = tripleToFind.getSubjectDictionaryID();
		int intPredicateToFind = tripleToFind.getPredicateDictionaryID();
		int intObjectToFind = tripleToFind.getObjectDictionaryID();
		for(RDFTriple triple : bigTable) 
			if(triple.getSubjectDictionaryID() == intSubjectToFind)
				if(triple.getPredicateDictionaryID() == intPredicateToFind)
					if(triple.getObjectDictionaryID() == intObjectToFind)
						return true;
		return false;
	}
	
	/*
	 * Logs :
	 * Write the BigTable as a semi-human-readable file
	 */
	public void writeLogs(String filePath) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		int index = 0;
		for(RDFTriple triple : bigTable) {
			fw.write("INDEX in ArrayList  : " + index + " = " + triple.getReadableTriple() + "\n");
			index++;
		}
		fw.write("\n The BIG TABLE contains : " + bigTable.size() + " RDF triple.");
		fw.close();
	}


}
