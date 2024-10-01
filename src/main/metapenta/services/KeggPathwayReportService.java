package metapenta.services;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import metapenta.model.GeneProduct;
import metapenta.model.Reaction;
import metapenta.model.MetabolicNetwork;
import metapenta.io.KeggPathwayXMLProcessor;
import metapenta.io.MetabolicNetworkXMLLoader;

public class KeggPathwayReportService {
	private Map<String,Reaction> reactionsByKeggId;
	
	public KeggPathwayReportService(MetabolicNetwork network) {
		reactionsByKeggId = new HashMap<>();
		List<Reaction> reactions = network.getReactionsAsList();
		for(Reaction r:reactions) {
			String keggId = r.getKeggId();
			if(keggId!=null) reactionsByKeggId.put(keggId, r);
		}
	}
	public void buildReport(String pathwayFile, PrintStream out) throws IOException {
		
		KeggPathwayXMLProcessor keggPathwayProcessor = new KeggPathwayXMLProcessor();
		keggPathwayProcessor.load(pathwayFile);
		Map<String,Set<String>> reactionEnzymes = keggPathwayProcessor.getReactionEnzymes();
		Set<String> orphanReactionIds = keggPathwayProcessor.getOrphanReactionIds();
		for(Map.Entry<String,Set<String>> entry:reactionEnzymes.entrySet()) {
			Reaction r = reactionsByKeggId.get(entry.getKey());
			if(r!=null) {
				String ecNumber = r.getEcCode();
				//TODO: Validate consistency of gene products
				out.println(entry.getKey()+"\t"+r.getId()+"\t"+ecNumber+"\t"+parseGeneProducts(r)+"\tPRESENT");
			} else {
				out.println(entry.getKey()+"\tND\tND\t"+parseSet(entry.getValue())+"\tNotPresent");
			}
		}
		for(String keggId:orphanReactionIds) {
			Reaction r = reactionsByKeggId.get(keggId);
			if(r!=null) {
				String ecNumber = r.getEcCode();
				out.println(keggId+"\t"+r.getId()+"\t"+ecNumber+"\t"+parseGeneProducts(r)+"\tPRESENT");
			} else {
				out.println(keggId+"\tND\tND\tND\tNotPresent");
			}
		}
	}
	
	
	
	private String parseGeneProducts(Reaction r) {
		Set<String> geneIds = new HashSet<>();
		for(GeneProduct g:r.getEnzymes()) {
			geneIds.add(g.getName());
		}
		return parseSet(geneIds);
	}
	private String parseSet(Set<String> geneIds) {
		if(geneIds.size()==0) return "ND";
		StringBuilder answer = new StringBuilder();
		List<String> genesList = new ArrayList<>(geneIds);
		Collections.sort(genesList);
		boolean c = false;
		for(String geneId:genesList) {
			if(c) answer.append(",");
			else c= true;
			answer.append(geneId);
		}
		return answer.toString();
	}
	
	public static void main(String[] args) throws Exception {
		MetabolicNetworkXMLLoader networkLoader = new MetabolicNetworkXMLLoader();
        MetabolicNetwork network = networkLoader.loadNetwork(args[0]);
        KeggPathwayReportService instance = new KeggPathwayReportService(network);
        try (PrintStream out = new PrintStream(args[2])) {
        	instance.buildReport(args[1], out);
        }	
	}
	
}
