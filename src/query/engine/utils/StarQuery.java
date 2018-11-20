package query.engine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import query.engine.datastructure.DoubleDictionary;

/**
 * This class define what a StarQuery is :
 * 		The variable will always be the Subject (ie : ?v0)
 * 		and few QueryCondition will be requiered to match for this subject variable
 */
public class StarQuery {
	
	private String rawQuery;
	private List<QueryCondition> conditions;
	private List<Integer> conditionsEvaluationorder = new ArrayList<Integer>();
	private Set<Integer> finalSolutions = new TreeSet<Integer>();
	private Set<Integer> oracleSolutions = new TreeSet<Integer>();
	private float soundness; // fraction of answers for our system that are also answers for O 
	private float completeness; // fraction of answers for O that are also answers for our system

	public StarQuery(List<QueryCondition> conditions, String rawQuery) {
		this.rawQuery = rawQuery;
		this.conditions = new ArrayList<QueryCondition>();
		for(QueryCondition conds : conditions) {
			this.conditions.add(conds);
		}
	}
	
    /*
     * Getters
     */
	public float getSoundness() {
		return this.soundness;
	}
	
	public float getCompleteness() {
		return this.completeness;
	}
	public List<QueryCondition> getQueryCondition(){
		return this.conditions;
	}
	
	public int getConditionsSize() {
		return this.conditions.size();
	}
	
	public String getRawQuery() {
		return this.rawQuery;
	}
	
	public Set<Integer> getFinalSolutions(){
		return this.finalSolutions;
	}
	
    public String getReadableStarQuery(DoubleDictionary dictionary) {
    	String queryString = "RAWQUERY : " + "\n" + rawQuery;
    	int conditionIndex = 0;
    	for(QueryCondition cond : conditions) {
    		queryString += "CONDITION_INDEX : " + conditionIndex + "\n"
    					+ cond.getReadableStarCondition(dictionary) + "\n";
    		conditionIndex++;
    	}
    	if(conditions.size() > 2  ) queryString += "CONDITION_EVALUATION_ORDER : " + conditionsEvaluationorder + "\n";
    	if(finalSolutions.size() != 0) {
    		queryString += "SOLUTION_NUMBER : " + finalSolutions.size() + "\n";
    		for(Integer solution : finalSolutions) {
    			queryString += "SOLUTION : " + dictionary.getByID(solution) + " (_ID_ : " + solution + ") " + "\n";
    		}
    	} else {
    		queryString += "No Solutions has been found.";
    	}
    	
    	if(oracleSolutions.size() != 0) {
    		queryString += "ORACLE_SOLUTION_NUMBER : " + oracleSolutions.size() + "\n";
    		for(Integer solution : oracleSolutions) {
    			queryString += "ORACLE_SOLUTION : " + dictionary.getByID(solution) + " (_ID_ : " + solution + ") " + "\n";
    		}
    	} else {
    		queryString += "No Solutions has been found.";
    	}
    	
    	return queryString;
    }
    
    /*
     * Setter
     */
    public void addOracleSolution(int sol) {
    	oracleSolutions.add(sol);
    }
    
    public void commitQuerySolution(TreeSet<Integer> allSolutions){
    	finalSolutions.addAll(allSolutions);
    	
    }
    
    public void commitConditionEvaluationorder(int evaluationOrder){
    	conditionsEvaluationorder.add(evaluationOrder);    	
    }
    
    public void computeSoundness() {
    	int match = 0;
    	if(finalSolutions.size() == 0 && oracleSolutions.size() == 0) {
    		this.soundness = (float) 100;
    	} else {
    		for(Integer solution : finalSolutions) {
    			if(oracleSolutions.contains(solution)) {
    				match++;
    			}
    		}
    		if(match != finalSolutions.size()) {
    			this.soundness = (float) match / finalSolutions.size() * 100;
    			System.out.println("WARNING!!!!!!!!!!!!!!!!!!!!");
    		} else {
    			this.soundness = (float) 100;
    		}
    	}
    }
    
    public void computeCompleteness() {
    	int match = 0;
    	if(finalSolutions.size() == 0 && oracleSolutions.size() == 0) {
    		this.completeness = (float) 100;
    	} else {
    		for(Integer solution : oracleSolutions) {
    			if(finalSolutions.contains(solution)) {
    				match++;
    			}
    		}
    		if(match != oracleSolutions.size()) {
    			this.completeness = (float) match / oracleSolutions.size() * 100;
    			System.out.println("WARNING!!!!!!!!!!!!!!!!!!!!");
    		} else {
    			this.completeness = (float) 100;
    		}
    	}
    	
    }
    

    	
}