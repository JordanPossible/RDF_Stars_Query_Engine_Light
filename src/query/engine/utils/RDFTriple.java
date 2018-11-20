package query.engine.utils;

/**
 * This class implements a light version of an RDF triple
 * The constructor take in paramaters 3 Strings representing the subject, predicate, object
 * While we can set the integer representation later when the dictionary will be able to deliver an ID key
 */
public class RDFTriple {

    private String subjectString;
    private String predicateString;
    private String objectString;
    
    private int subjectDictionaryID;
    private int predicateDictionaryID;
    private int objectDictionaryID;

    public RDFTriple(String subjectString, String predicateString, String objectString) {
        this.subjectString = subjectString;
        this.predicateString = predicateString;
        this.objectString = objectString;
    }
    
    public RDFTriple() {}
    
    /*
     * These setters methodes will be used by the dictionary 
     * so we can set the ID value chosen by the dictionary
     */
    public void setSubjectDictionaryID(int id) {
    	this.subjectDictionaryID = id;
    }
    
    public void setPredicateDictionaryID(int id) {
    	this.predicateDictionaryID = id;
    }
    
    public void setObjectDictionaryID(int id) {
    	this.objectDictionaryID = id;
    }

    /*
     * Getters
     */
    public String getSubjectString() {
    	return this.subjectString;
    }
    
    public String getPredicateString() {
    	return this.predicateString;
    }
    
    public String getObjectString() {
    	return this.objectString;
    }
    
    public int getSubjectDictionaryID() {
    	return this.subjectDictionaryID;
    }
    
    public int getPredicateDictionaryID() {
    	return this.predicateDictionaryID;
    }
    
    public int getObjectDictionaryID() {
    	return this.objectDictionaryID;
    }
    
    public String getReadableTriple() {
    	String triple = "SUBJECT : " + this.subjectString + "(_id_ : " + this.subjectDictionaryID + ")"
    				+ " PREDICATE : " + this.predicateString + "(_id_ : " + this.predicateDictionaryID + ")"
    				+ " OBJECT : " + this.objectString + "(_id_ : " + this.objectDictionaryID + ")";
    	return triple;
    }
    
}
