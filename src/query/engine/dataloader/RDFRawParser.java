package query.engine.dataloader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import query.engine.utils.RDFTriple;

/**
 * This class will loadup the RDFTriple List on the call of the parseData methode 
 * this methode requiere a String as argument which represents the rdf data file
 * and will return the temporary big table as an RDFTriple ArrayList
 */
public final class RDFRawParser {
	private static List<RDFTriple> bigTableTmp = new ArrayList<RDFTriple>();
	private static class RDFListener extends RDFHandlerBase {
		@Override
		public void handleStatement(Statement st) {
			bigTableTmp.add(new RDFTriple(st.getSubject().toString(), st.getPredicate().toString(), st.getObject().toString()));
		}
	};

	public List<RDFTriple> parseData(String pathToFile) throws FileNotFoundException{
		Reader reader = new FileReader(pathToFile);
		org.openrdf.rio.RDFParser rdfParser = Rio
				.createParser(RDFFormat.RDFXML);
		rdfParser.setRDFHandler(new RDFListener());
		try {
			rdfParser.parse(reader, "");
		} catch (Exception e) {
		}
		try {
			reader.close();
		} catch (IOException e) {
		}
		return bigTableTmp;
	}
}