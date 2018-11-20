package query.engine;

import java.io.IOException;

import query.engine.dataloader.RDFBulkLoader;
import query.engine.datastructure.BigTable;
import query.engine.utils.RDFTriple;

public class Run {
	
	public static void main(String[] args) throws IOException {
		boolean verbose = false, exportResults = false, exportStats = false, workloadTime = false;
    	if(args.length != 10) {
    		System.out.println("This program need the followings arguments :  ");
    		System.out.println("-queries \"/chemin/vers/requetes\" " + "-data \"/chemin/vers/donnees\" " + "-output \"/chemin/vers/dossier/sortie\" " 
    							+ "-verbose -export_results -export_stats -workload_time");
    	}
    	String queriestDir = args[1];
    	String datatDir = args[3];
    	String outputDir = args[5];
    	if(args[6].equals("-verbose")) verbose = true;
    	if(args[7].equals("-export_results")) exportResults = true;
    	if(args[8].equals("-export_stats")) exportStats = true;
    	if(args[9].equals("-workload_time")) workloadTime = true;
    	
    	Hexastore hexastore = new Hexastore(queriestDir, datatDir, outputDir, verbose, exportResults, exportStats, workloadTime);
//    	Experiment3bis e1= new Experiment3bis(queriestDir, datatDir, outputDir, verbose, exportResults, exportStats, workloadTime);
	}
}

