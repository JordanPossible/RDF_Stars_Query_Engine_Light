package query.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import query.engine.dataloader.QueryBulkLoader;
import query.engine.dataloader.RDFBulkLoader;
import query.engine.datastructure.BigTable;
import query.engine.datastructure.DoubleDictionary;
import query.engine.datastructure.OPS;
import query.engine.datastructure.POS;
import query.engine.datastructure.StarQueryBulk;
import query.engine.handler.SelectivityHandler;
import query.engine.handler.StarQueryHandler;

public class Experiment3 {
	private BigTable bigTable;
	private RDFBulkLoader rdfBulkLoader;
	private DoubleDictionary dictionary;
	private POS pos;
	private OPS ops;
	private StarQueryBulk starQueryBulk;
	private QueryBulkLoader queryBulkLoader;
	private StarQueryHandler starQueryhandler;
	private SelectivityHandler selectivityHandler;
	
	public Experiment3(String queriestDir, String datatDir, String outputDir, 
			boolean verbose, boolean exportResults, boolean exportStats, boolean workloadTime) throws IOException {
		
		String exp3_path = "./exp3.txt";
		File file = new File(exp3_path);
		FileWriter fr = new FileWriter(file, true);


		bigTable = new BigTable();
		rdfBulkLoader = new RDFBulkLoader();
		
		/* Big Table */
		rdfBulkLoader.loadRDF(bigTable, datatDir);

		/* Dico */		
		dictionary = new DoubleDictionary(bigTable);
		bigTable.setAllDictionaryID(dictionary); 

		/* Index */
		pos = new POS(bigTable); 
		ops = new OPS(bigTable); 

		/* load queries */
		starQueryBulk = new StarQueryBulk();
		queryBulkLoader = new QueryBulkLoader(); 
		queryBulkLoader.loadQueries(starQueryBulk, dictionary, queriestDir);
		
		/* compute selectivity */
		selectivityHandler = new SelectivityHandler();
		selectivityHandler.computeSelectivity(starQueryBulk, ops, pos, bigTable);
		
		/* solve queries without shuffle*/
		for(int i = 0; i < 10; i++) {
			Instant startBT = Instant.now();
		
			starQueryhandler = new StarQueryHandler(); 
			starQueryhandler.solveBulkQuery(starQueryBulk, pos, ops);
		
			Instant finishBT = Instant.now();
			long timeElapsedBT = Duration.between(startBT, finishBT).toMillis();
		
			fr.write("\nworkload time : " + timeElapsedBT+ "\n");
		}
		
		fr.write("\n after suffle" + "\n");
		
		/* solve queries with shuffle*/
		for(int i = 0; i < 10; i++) {
			starQueryBulk.shuffleQueryBulk();
			
			Instant startBT = Instant.now();
		
			starQueryhandler = new StarQueryHandler(); 
			starQueryhandler.solveBulkQuery(starQueryBulk, pos, ops);
		
			Instant finishBT = Instant.now();
			long timeElapsedBT = Duration.between(startBT, finishBT).toMillis();
		
			fr.write("\nworkload time : " + timeElapsedBT+ "\n");
		}
		fr.close();
	}

}
