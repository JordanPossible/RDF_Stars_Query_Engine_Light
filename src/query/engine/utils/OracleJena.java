package query.engine.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;

import query.engine.datastructure.DoubleDictionary;
import query.engine.datastructure.StarQueryBulk;

public class OracleJena {
	Model model = ModelFactory.createDefaultModel();
	
	public void loadData(String filePath) {
		InputStream in = FileManager.get().open(filePath);
		this.model.read(in, null);
	}
	
	public void solveQuery(StarQuery starQuery, DoubleDictionary dictionary) {
		String rawQuery = starQuery.getRawQuery();
		
		Query query = QueryFactory.create(rawQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		
		try {

			ResultSet rs = qexec.execSelect();

//			ResultSetFormatter.out(System.out, rs, query);
			
//			System.out.println(rs.hasNext());
			
			while (rs.hasNext() ) {
	            QuerySolution qs = rs.next();
	            Resource agentUri = qs.getResource("v0");
//	            System.out.println(agentUri.getURI());
	            starQuery.addOracleSolution(dictionary.getByString(agentUri.getURI()));
			}

		} finally {

			qexec.close();
		}

	}
	
	public void solveBulkQuery(StarQueryBulk starQueryBulk, DoubleDictionary dictionary){
		for(int queryIndex = 0; queryIndex < starQueryBulk.getQueryBulkSize(); queryIndex++) {
			solveQuery(starQueryBulk.getStarQuery(queryIndex), dictionary);
		}
	}
}
