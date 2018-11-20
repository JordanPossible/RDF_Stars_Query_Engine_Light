package query.engine.dataloader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import query.engine.datastructure.BigTable;
import query.engine.utils.RDFTriple;

/**
 * This utility class offer a single methode loadData(BigTable bigTable, String filePath) which will
 * load up a Big Table from an RDF data file
 */
public class RDFBulkLoader {
	
	public void loadRDF(BigTable bigTable, String filePath) {
		List<RDFTriple> bigTableTmp = new ArrayList<RDFTriple>();
		RDFRawParser parser = new RDFRawParser();
		try {
			bigTableTmp = parser.parseData(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Please Note: 
		 * When using the addAll() method to copy, the contents of both the array lists 
		 * (originalArrayList and copyArrayList) refer to the same objects or contents. 
		 * So if you modify any one of them the other will also reflect the same change.
		 * If you don't wan't this then you need to copy each element from the originalArrayList 
		 * to the copyArrayList, like using a for or while loop. */
		for(RDFTriple tripleTmp : bigTableTmp) {
			bigTable.insertTriple(tripleTmp);
		}

	}
}
