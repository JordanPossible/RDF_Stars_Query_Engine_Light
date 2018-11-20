package query.engine.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import query.engine.datastructure.OPS;
import query.engine.datastructure.POS;
import query.engine.datastructure.StarQueryBulk;
import query.engine.utils.QueryCondition;
import query.engine.utils.StarQuery;

/**
 * This utility class offer the methodes which solves queries
 * solveBulkQuery will solveStarQuery of each query in the bulk
 */
public class StarQueryHandler {
	
	/*
	 * 
	 */
	private void solveStarQuery(StarQuery starQuery, POS pos, OPS ops) {
		
		ArrayList<TreeSet<Integer>> allSolutions = new ArrayList<TreeSet<Integer>>();
		ArrayList<Float> selectivityPatternList = new ArrayList<Float>();
		
		/* For each QueryConditions we will find the solutions using the Selectivity metrics to pick the appropriate index */
		for(QueryCondition queryCondition : starQuery.getQueryCondition()) {
			int predicateCondition = queryCondition.getOnPredicateCondition();
			int objectCondition = queryCondition.getOnObjectCondition();
			TreeSet<Integer> solutions = new TreeSet<Integer>();
			if(queryCondition.getPredicateSelectivity() < queryCondition.getObjectSelectivity()) {
				if(pos.get(predicateCondition) != null && pos.get(predicateCondition).get(objectCondition) != null) {
					solutions.addAll(pos.get(predicateCondition).get(objectCondition));
				}
			} else {
				if(ops.get(objectCondition) != null && ops.get(objectCondition).get(predicateCondition) != null) {
					solutions.addAll(ops.get(objectCondition).get(predicateCondition));
				}
			}
			
			allSolutions.add(solutions);
			selectivityPatternList.add(queryCondition.getPatternSelectivity());
		}
		/* If the Query has only one condition, then we commit the final result
		 * Else if the Query has twoo condition, we need to perform a single merge
		 * Else, we need to perform multiple merge
		 */
		if(starQuery.getConditionsSize() == 1) {
			starQuery.commitQuerySolution(allSolutions.get(0));
		} else if(starQuery.getConditionsSize() == 2){
			TreeSet<Integer> mergedSolution = new TreeSet<Integer>();
			mergedSolution.clear();
			mergeTwooSolutions(allSolutions, mergedSolution);
			if(mergedSolution.size() > 0) starQuery.commitQuerySolution(mergedSolution);
		} else {
			TreeSet<Integer> mergedSolution = new TreeSet<Integer>();
			mergedSolution.clear();
			mergeMultipleSolutions(allSolutions, mergedSolution, selectivityPatternList, starQuery);
			if(mergedSolution.size() > 0) starQuery.commitQuerySolution(mergedSolution);
		}
		
	}
	
	/*
	 * we could had used retainAll
	 */
	private void mergeTwooSolutions(ArrayList<TreeSet<Integer>> allSolutions, TreeSet<Integer> mergedSolution)  {
		for(Integer solutionsFirstSet : allSolutions.get(0)) {
			for(Integer solutionsSecondtSet : allSolutions.get(1)) {
				if(solutionsFirstSet.equals(solutionsSecondtSet)) {
					mergedSolution.add(solutionsFirstSet);
				}
			}
		}
	}
	
	
	private void mergeMultipleSolutions(ArrayList<TreeSet<Integer>> allSolutions, TreeSet<Integer> mergedSolution,ArrayList<Float> selectivityPatternList, StarQuery starQuery) {
		/* We put the minimum selective pattern solutions into our mergedSolution */
		int firstMinIndex = getIndexOfMin(selectivityPatternList);
		selectivityPatternList.set(firstMinIndex, Float.MAX_VALUE); // We merge each solution only once
		starQuery.commitConditionEvaluationorder(firstMinIndex);
		mergedSolution.addAll(allSolutions.get(firstMinIndex));
		
		/* For each next minimum selective solutions, we will merge it to the mergedSolution */
		for(int mergeToPerform = allSolutions.size() -1; mergeToPerform > 0; mergeToPerform--) {
			int minIndex = getIndexOfMin(selectivityPatternList);
			selectivityPatternList.set(minIndex, Float.MAX_VALUE);
			starQuery.commitConditionEvaluationorder(minIndex);
			mergedSolution.retainAll(allSolutions.get(minIndex));
		}
	}
	
	private int getIndexOfMin(List<Float> data) {
	    float min = Float.MAX_VALUE;
	    int index = -1;
	    for (int i = 0; i < data.size(); i++) {
	        Float f = data.get(i);
	        if (Float.compare(f.floatValue(), min) < 0) {
	            min = f.floatValue();
	            index = i;
	        }
	    }
	    return index;
	}
	

	public void solveBulkQuery(StarQueryBulk starQueryBulk, POS pos, OPS ops) {
		for(int queryIndex = 0; queryIndex < starQueryBulk.getQueryBulkSize(); queryIndex++) {
			solveStarQuery(starQueryBulk.getStarQuery(queryIndex), pos, ops);
		}
	}
	
}
