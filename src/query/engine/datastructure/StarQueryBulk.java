package query.engine.datastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import query.engine.utils.RDFTriple;
import query.engine.utils.StarQuery;

/**
 * This class represent a collection of StarQuery (an ArrayList)
 */
public class StarQueryBulk {
	
	private ArrayList<StarQuery> queryBulk;
	
	public StarQueryBulk() {
		this.queryBulk = new ArrayList<StarQuery>();
	}
	
	/*
	 * Setters
	 */
	public void insertStarQuery(StarQuery query) {
		this.queryBulk.add(query);
	}
	
	/*
	 * Getters
	 */
	public StarQuery getStarQuery(int index) {
		return this.queryBulk.get(index);
	}
	
	public int getQueryBulkSize() {
		return this.queryBulk.size();
	}

	/*
	 * Logs :
	 * Write the StarQueryBulk as a semi-human-readable file
	 */
	public void writeLogs(String filePath, DoubleDictionary dictionary) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		
		int index = 0;
		for (StarQuery starQuery : queryBulk) {
			fw.write("Query at INDEX : " + index + "\n"
					+ starQuery.getReadableStarQuery(dictionary) + "\n\n"); //ajouter la string et espacer
			index++;
		}
			
		fw.close();
	}
	
	public void exportResults(String filePath, DoubleDictionary dictionary) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		
		for (StarQuery starQuery : queryBulk) {
			fw.write(starQuery.getRawQuery() + " , ");
			for(int solution : starQuery.getFinalSolutions()) {
				fw.write(dictionary.getByID(solution) + " , ");
			}
			fw.write("\n");
		}
		
		fw.close();
	}

	public void computeCompleteness() {
		for (StarQuery starQuery : queryBulk) {
			starQuery.computeCompleteness();
		}
	}
	
	public void computeSoundness() {
		for (StarQuery starQuery : queryBulk) {
			starQuery.computeSoundness();
		}
	}
	
	public void checkQualityAnserws() {
		for (StarQuery starQuery : queryBulk) {
			if(starQuery.getCompleteness() != (float) 100 || starQuery.getSoundness() != (float) 100) {
				System.out.println("Problem here" + starQuery.getRawQuery());
			}
		}
	}
	
	public void shuffleQueryBulk() {
		Collections.shuffle(this.queryBulk);
	}

}

