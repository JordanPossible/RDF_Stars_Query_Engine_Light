package query.engine.utils;

import query.engine.datastructure.DoubleDictionary;

/**
 * This class represent a WHERE condition of a SPARQL query
 * A condition is made of condition on the predicate and a condition on the object
 * Later, we compute some selectivity metrics so we can solve queries faster
 */
public class QueryCondition {
	private int onPredicateCondition;
	private int onObjectCondition;
	private float predicateSelectivity;
	private float objectSelectivity;
	private float patternSelectivity; 

	
	public QueryCondition(int onPredCond, int onObjCond) {
		this.onPredicateCondition = onPredCond;
		this.onObjectCondition = onObjCond;
	}
	
	/*
	 * Getters
	 */
	public float getPredicateSelectivity() {
		return this.predicateSelectivity;
	}
	
	public float getObjectSelectivity() {
		return this.objectSelectivity;
	}
	
	public float getPatternSelectivity() {
		return this.patternSelectivity;
	}
	
	public int getOnPredicateCondition() {
		return this.onPredicateCondition;
	}
	
	public int getOnObjectCondition() {
		return this.onObjectCondition;
	}

	public String getReadableStarCondition(DoubleDictionary dictionary) {
		return "PREDICATE : " + dictionary.getByID(onPredicateCondition) + "(_ID_ : " + onPredicateCondition + ") "
				+ "PREDICATE_SELECTIVITY : " + predicateSelectivity + "\n"
				+ "OBJECT : " + dictionary.getByID(onObjectCondition) + "(_ID_ : " + onObjectCondition + ") "
				+ "OBJECT_SELECTIVITY : " + objectSelectivity + "\n"
				+ "PATTERN_SELECTIVITY : " + patternSelectivity;
	}
	
	public String getReadableStarCondition() {
		return "PREDICATE : " + "(_ID_ : " + onPredicateCondition + ") "
				+ "OBJECT : " + "(_ID_ : " + onObjectCondition + ") " + "\n";
	}
	
	/*
	 * Setters
	 */
	public void setPredicateSelectivity(float sel) {
		this.predicateSelectivity = sel;
	}
	
	public void setObjectSelectivity(float sel) {
		this.objectSelectivity = sel;
	}

	public void setPatternSelectivity(float sel) {
		this.patternSelectivity = sel;
		
	}
}
