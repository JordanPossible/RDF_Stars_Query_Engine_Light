package query.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import query.engine.dataloader.RDFBulkLoader;
import query.engine.datastructure.BigTable;
import query.engine.datastructure.DoubleDictionary;
import query.engine.datastructure.OPS;
import query.engine.datastructure.POS;

public class Experiment1 {
	private BigTable bigTable;
	private RDFBulkLoader rdfBulkLoader;
	private DoubleDictionary dictionary;
	private POS pos;
	private OPS ops;
	
	public Experiment1(String queriestDir, String datatDir, String outputDir, 
			boolean verbose, boolean exportResults, boolean exportStats, boolean workloadTime) throws IOException {
		
		String exp1_path = "./exp1.txt";
		File file = new File(exp1_path);
		FileWriter fr = new FileWriter(file, true);


		bigTable = new BigTable();
		rdfBulkLoader = new RDFBulkLoader();
		
		/* Big Table */
		Instant startBT = Instant.now();

		rdfBulkLoader.loadRDF(bigTable, datatDir);

		Instant finishBT = Instant.now();
		long timeElapsedBT = Duration.between(startBT, finishBT).toMillis();
		
		fr.write("\nbig table : " + timeElapsedBT+ "\n");
		
		/* Dico */
		Instant startD = Instant.now();
		
		dictionary = new DoubleDictionary(bigTable);
		bigTable.setAllDictionaryID(dictionary); 

		Instant finishD = Instant.now();
		long timeElapsedD = Duration.between(startD, finishD).toMillis();
		
		fr.write("dico : " + timeElapsedD+ "\n");
		
		/* Index */
		Instant startI = Instant.now();
		
		pos = new POS(bigTable); 
		ops = new OPS(bigTable); 

		Instant finishI = Instant.now();
		long timeElapsedI = Duration.between(startI, finishI).toMillis();
		
		fr.write("index : " + timeElapsedI + "\n\n");
		
		fr.close();
	}

}
