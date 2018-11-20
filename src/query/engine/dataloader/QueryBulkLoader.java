package query.engine.dataloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import query.engine.datastructure.DoubleDictionary;
import query.engine.datastructure.StarQueryBulk;
import query.engine.utils.QueryCondition;
import query.engine.utils.StarQuery;

/**
 * This utility class offer a single methode loadQueries(StarQueryBulk queryBulk, DoubleDictionary dictionary, String dir_path) 
 * which will load up all the queries in the dir_path in the queryBulk
 */
public class QueryBulkLoader {

	/* parseQueryFile is called on every file in the dir */
	public void loadQueries(StarQueryBulk queryBulk, DoubleDictionary dictionary, String dir_path) {
		File folder = new File(dir_path);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	try {
					parseQueryFile(queryBulk, dictionary, file.getAbsolutePath());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	}
	
	/* 
	 * The parseQueryFile methode crawl a query file and call parseSingleQuery on every query
	 */
	private void parseQueryFile(StarQueryBulk queryBulk, DoubleDictionary dictionary, String file) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(file));
		StringBuilder queryString = new StringBuilder();
		StringBuilder rawQuery = new StringBuilder();
		rawQuery.append("SELECT ?v0 WHERE { \n");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			rawQuery.append(line + "\n");
			if ((line.startsWith("select") || (line.startsWith("SELECT")))){
				queryString = new StringBuilder();
				rawQuery = new StringBuilder();
				rawQuery.append("SELECT ?v0 WHERE { \n");
			}
			queryString.append(line);
			
			if (line.contains("}")) {
				parseSingleQuery(queryBulk, dictionary, queryString.toString(), rawQuery.toString());
			}
		}
	}
	
	/*
	 *  This methode construct a StarQuery object and put it in the QueryBulk
	 */
	private void parseSingleQuery(StarQueryBulk queryBulk, DoubleDictionary dictionary, String query, String rawQuery) {
		String[] splitedQuery = query.split((Pattern.quote("?v0")));
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		for(int i = 2; i < splitedQuery.length; i++) {
			String[] splitedLine = splitedQuery[i].split((Pattern.quote(">")));
			String stringPredicate = splitedLine[0].replaceFirst("<", "");
			stringPredicate = stringPredicate.replaceFirst(" ", "");
			String stringObject = splitedLine[1].replaceFirst("<", "");
			stringObject = stringObject.replaceFirst(" ", "");
			int PredicateID = dictionary.getByString(stringPredicate);
			int objectID = dictionary.getByString(stringObject);
			queryConditions.add(new QueryCondition(PredicateID, objectID));
		}
		queryBulk.insertStarQuery(new StarQuery(queryConditions, rawQuery));	
	}
}

