package query.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import org.apache.commons.io.FileUtils;

import query.engine.dataloader.QueryBulkLoader;
import query.engine.dataloader.RDFBulkLoader;
import query.engine.datastructure.BigTable;
import query.engine.datastructure.DoubleDictionary;
import query.engine.datastructure.OPS;
import query.engine.datastructure.POS;
import query.engine.datastructure.StarQueryBulk;
import query.engine.handler.SelectivityHandler;
import query.engine.handler.StarQueryHandler;
import query.engine.utils.OracleJena;
import query.engine.utils.RDFTriple;

public class Hexastore {

	private BigTable bigTable;
	private RDFBulkLoader rdfBulkLoader;
	private DoubleDictionary dictionary;
	private POS pos;
	private OPS ops;
	private StarQueryBulk starQueryBulk;
	private QueryBulkLoader queryBulkLoader;
	private StarQueryHandler starQueryhandler;
	private SelectivityHandler selectivityHandler;
	
	public Hexastore(String queriestDir, String datatDir, String outputDir, 
			boolean verbose, boolean exportResults, boolean exportStats, boolean workloadTime) throws IOException {

		File outputDirectory = new File(outputDir);
		if(!outputDirectory.exists()) outputDirectory.mkdir();
		new File(outputDir + "/logs/").mkdirs();
		FileWriter fwVerbose = new FileWriter(outputDir + "/verbose.txt");
		
		/* Initialise BigTable and RDFBulkLoader instance */
		if(verbose) System.out.println("Initialise BigTable and RDFBulkLoader instance...");
		Instant start = Instant.now();
		
		bigTable = new BigTable();
		rdfBulkLoader = new RDFBulkLoader();
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Initialise BigTable and RDFBulkLoader instance[DONE] " + "[TIME : " + timeElapsed + " Millis]");
		if(verbose) fwVerbose.write("Initialise BigTable and RDFBulkLoader instance[DONE] " + "[TIME : " + timeElapsed + " Millis]" + "\n");
		/* ---------------------------------------------*/
		
		
		
		/* Loading RDF Bulk Data from file to the BigTable */
		if(verbose) System.out.println("\nLoading RDF Bulk Data from " + datatDir + "file to the BigTable...");
		start = Instant.now();
		
		rdfBulkLoader.loadRDF(bigTable, datatDir);
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Loading RDF Bulk Data from " + datatDir + "file to the BigTable[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Loading RDF Bulk Data from " + datatDir + "file to the BigTable[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		/* ---------------------------------------------*/
		
		
		
		/* Computing the Dictionary, Testing the Dictionary Health, and writting Dictionary and Big Table Logs*/
		if(verbose) System.out.println("\nComputing the Dictionary, Testing the Dictionary Health, and writting Dictionary and Big table Logs...");
		start = Instant.now();

		dictionary = new DoubleDictionary(bigTable);
		if(dictionary.checkDictionaryHealth()) System.out.println("	[checkDictionaryHealth] has been executed correctly.");
		dictionary.writeLogs(outputDir + "/logs/dictionary_log.txt"); System.out.println("	Dictionary Logs has been saved to " + outputDir + " /logs/dictionary_log.txt");
		bigTable.setAllDictionaryID(dictionary); System.out.println("	Updated Big Table with Dictionary Key");
		bigTable.writeLogs(outputDir +  "/logs/bigTable_log.txt"); System.out.println("	Big Table Logs has been saved to " + outputDir + " /logs/bigTable_log.txt");
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Computing the Dictionary, Testing the Dictionary Health, and writting Dictionary and Big table Logs[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Computing the Dictionary, Testing the Dictionary Health, and writting Dictionary and Big table Logs[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		/* ----------------------------------------- */
		
		
		/* Computing the POS and OPS Indexes */
		if(verbose) System.out.println("\nComputing the POS and OPS Indexes...");
		start = Instant.now();
		
		pos = new POS(bigTable); System.out.println("	POS Has been created.");
		ops = new OPS(bigTable); System.out.println("	OPS Has been created.");
		finish = Instant.now();
		pos.writeLogs(outputDir +  "/logs/POS_log.txt"); System.out.println("	POS Logs has been saved to " + outputDir + " /logs/POS_log.txt");
		ops.writeLogs(outputDir +  "/logs/OPS_log.txt"); System.out.println("	OPSLogs has been saved to " + outputDir + " /logs/OPS_log.txt");
		
		
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Computing the POS and OPS Indexes[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Computing the POS and OPS Indexes[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		
		/* ----------------------------------------- */

//		if(pos.checkIndexHealthIndexToBigTable(bigTable, dictionary)) System.out.println("POS has been loaded correctly.");
//		if(ops.checkIndexHealthIndexToBigTable(bigTable, dictionary)) System.out.println("OPS has been loaded correctly.");
//		if(ops.checkIndexHealthBigTableToIndex(bigTable, dictionary)) System.out.println("OPS has been loaded correctly.");

		/* Initialise StarQueryBulk and QueryBulkLoader instance, Load all the Queries into the Bulk */
		if(verbose) System.out.println("\nInitialise StarQueryBulk and QueryBulkLoader instance, Load all the Queries into the Bulk...");
		start = Instant.now();
		
		starQueryBulk = new StarQueryBulk();
		queryBulkLoader = new QueryBulkLoader(); System.out.println("	Loading the queries files from " + queriestDir + " into the StarQueryBulk");
		queryBulkLoader.loadQueries(starQueryBulk, dictionary, queriestDir);
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Initialise StarQueryBulk and QueryBulkLoader instance, Load all the Queries into the Bulk[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Initialise StarQueryBulk and QueryBulkLoader instance, Load all the Queries into the Bulk[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		/* ----------------------------------------- */
		
		/* Initialise SelectivityHandler and compute Selectivity for each Query */
		if(verbose) System.out.println("\nInitialise SelectivityHandler and compute Selectivity for each Query...");
		start = Instant.now();
		
		selectivityHandler = new SelectivityHandler(); System.out.println("	Computing SingleSelectivity and PatternSelectivity for each Query");
		
		Instant computeSelectivityStart = Instant.now();
		selectivityHandler.computeSelectivity(starQueryBulk, ops, pos, bigTable);
		Instant computeSelectivityFinish = Instant.now();
		long computeSelectivityTimeElapsed = Duration.between(computeSelectivityStart, computeSelectivityFinish).toMillis();
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Initialise SelectivityHandler and compute Selectivity for each Query[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Initialise SelectivityHandler and compute Selectivity for each Query[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		/* ----------------------------------------- */
		
		/* Solving all queries and writting StarQueryBulk Logs */
		if(verbose) System.out.println("\nSolving all queries and writting StarQueryBulk Logs...");
		start = Instant.now();
		
		starQueryhandler = new StarQueryHandler(); System.out.println("	Solving all queries");
		
		Instant solveBulkQueryStart = Instant.now();
		starQueryhandler.solveBulkQuery(starQueryBulk, pos, ops);
		Instant solveBulkQueryFinish = Instant.now();
		long solveBulkQueryTimeElapsed = Duration.between(solveBulkQueryStart, solveBulkQueryFinish).toMillis();
		
		/* ORACLE */
		Instant oracleLoadStart = Instant.now();
		
		System.out.println("	Loading query in JENA model");
		OracleJena oj = new OracleJena();
//		oj.loadData(datatDir);
		
		Instant oracleLoadFinish = Instant.now();
		long oracleLoadElapsed = Duration.between(oracleLoadStart, oracleLoadFinish).toMillis();
		
		if(verbose) System.out.println("	Loading query in JENA model[DONE] " 
				+ "[TIME : " + oracleLoadElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(oracleLoadElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("	Loading query in JENA model[DONE] " 
				+ "[TIME : " + oracleLoadElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(oracleLoadElapsed) + " Seconds]" + "\n");
		
		Instant oracleSolveStart = Instant.now();
		
		if(verbose) System.out.println("	Solving queries with JENA");
//		oj.solveBulkQuery(starQueryBulk, dictionary);
		
		Instant oracleSolveFinish = Instant.now();
		long oracleSolveElapsed = Duration.between(oracleSolveStart, oracleSolveFinish).toMillis();
		
		if(verbose) System.out.println("	Solving queries with JENA[DONE] " 
				+ "[TIME : " + oracleSolveElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(oracleSolveElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("	Solving queries with JENA[DONE] " 
				+ "[TIME : " + oracleSolveElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(oracleSolveElapsed) + " Seconds]" + "\n");
		/* ------ */
		
		/* Completeness */
//		starQueryBulk.computeCompleteness();
//		starQueryBulk.computeSoundness();
//		if(verbose) System.out.println("	Checking for queries anserws quality");
//		starQueryBulk.checkQualityAnserws();
		/* ------ */
		
//		starQueryBulk.writeLogs(outputDir +  "/logs/QueryBulk_log.txt", dictionary); System.out.println("	Queries Logs has been saved to " + outputDir +  "/logs/QueryBulk_log.txt");
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		if(verbose) System.out.println("Solving all queries and writting StarQueryBulk Logs[DONE] " 
										+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]");
		if(verbose) fwVerbose.write("Solving all queries and writting StarQueryBulk Logs[DONE] " 
				+ "[TIME : " + timeElapsed + " Millis | " + TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + " Seconds]" + "\n");
		/* ----------------------------------------- */
		
		System.out.println("\nDONE.\n");
		fwVerbose.close();
		
		if(exportStats) copyFile(outputDir +  "/logs/QueryBulk_log.txt", outputDir + "/export_stats.txt");
		if(exportResults) starQueryBulk.exportResults(outputDir + "/export_results.csv", dictionary);
		if(workloadTime) { 
			System.out.println("Computing Selectivity for this workload " + "[TIME : " + computeSelectivityTimeElapsed + "Millis]");
			System.out.println("Workload Time evaluation " + "[TIME : " + solveBulkQueryTimeElapsed + "Millis]");
			System.out.println("Workload Time evaluation with JENA Oracle" + "[TIME : " + oracleSolveElapsed + "Millis]");
		}
	}
	
	public static void copyFile(String sourcePath, String destinationPath) throws IOException {
	    Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
	}
}




