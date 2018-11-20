package query.engine.handler;

import query.engine.datastructure.BigTable;
import query.engine.datastructure.OPS;
import query.engine.datastructure.POS;
import query.engine.datastructure.StarQueryBulk;

/**
 * This utility class offer the methodes which compute the selectivity of our QueryConditions
 */
public class SelectivityHandler {
	
	public void computeSelectivity(StarQueryBulk queryBulk, OPS ops, POS pos, BigTable bigTable) {
		computeSingleSelectivity(queryBulk, ops, pos, bigTable);
		computePatternSelectivity(queryBulk, ops, pos, bigTable);
	}
	
	/*
	 * For each QueryCondition in each StarQuery, we will compute the predicateSelectivity and the objectSelectivity
	 * And set these values with the QueryConditionSetters
	 */
	private void computeSingleSelectivity(StarQueryBulk queryBulk, OPS ops, POS pos, BigTable bigTable) {
		for(int queryIndex = 0; queryIndex < queryBulk.getQueryBulkSize(); queryIndex++) {
			for(int conditionIndex = 0; conditionIndex < queryBulk.getStarQuery(queryIndex).getConditionsSize(); conditionIndex++) {
				int predicateCondition = queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).getOnPredicateCondition();
				int objectCondition = queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).getOnObjectCondition();
				int predicateOccurance;
				int objectOccurance;
				if(pos.get(predicateCondition) != null) {
					predicateOccurance = pos.get(predicateCondition).size();
				} else {
					predicateOccurance = 0;
				}
				
				if(ops.get(objectCondition) != null) {
					objectOccurance = ops.get(objectCondition).size();
				} else {
					objectOccurance = 0;
				}
				
				float predicateSelectivity = (float)predicateOccurance / (float)bigTable.getSize();
				float objectSelectivity = (float)objectOccurance / (float)bigTable.getSize();
				
				queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).setPredicateSelectivity(predicateSelectivity);
				queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).setObjectSelectivity(objectSelectivity);
			}
		}
	}
	
	/*
	 * For each QueryCondition in each StarQuery, we will compute the patternSelectivity
	 * And set the value with the QueryConditionSetters
	 */
	private void computePatternSelectivity(StarQueryBulk queryBulk, OPS ops, POS pos, BigTable bigTable) {
		for(int queryIndex = 0; queryIndex < queryBulk.getQueryBulkSize(); queryIndex++) {
			for(int conditionIndex = 0; conditionIndex < queryBulk.getStarQuery(queryIndex).getConditionsSize(); conditionIndex++) {
				int predicateCondition = queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).getOnPredicateCondition();
				int objectCondition = queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).getOnObjectCondition();
				
				int patternOccurencePOS;
//				int patternOccurenceOPS;
				
				if(pos.get(predicateCondition) != null && pos.get(predicateCondition).get(objectCondition) != null) {
					patternOccurencePOS = pos.get(predicateCondition).get(objectCondition).size();
				} else {
					patternOccurencePOS = 0;
				}
				
				/* The value found will be the same 
				if(ops.get(objectCondition) != null && ops.get(objectCondition).get(predicateCondition) != null) {
					patternOccurenceOPS = ops.get(objectCondition).get(predicateCondition).size();
				} else {
					patternOccurenceOPS = 0;
				} */
				
				float patternSelectivity = (float)patternOccurencePOS / (float)bigTable.getSize();
				queryBulk.getStarQuery(queryIndex).getQueryCondition().get(conditionIndex).setPatternSelectivity(patternSelectivity);
			}
		}
	}
}


