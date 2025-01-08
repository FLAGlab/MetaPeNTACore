package metapenta.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metapenta.io.MetabolicNetworkXMLWriter;
import metapenta.model.GeneProduct;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Reaction;

public class CombineExpressionInformationService {

	private MetabolicNetwork network;
	public CombineExpressionInformationService(MetabolicNetwork network) {
		this.network = network;
	}
	public MetabolicNetwork getNetwork() {
		return network;
	}
	public void run(Map<String, List<Double>> expressionData) {
		List<GeneProduct> enzymes = network.getGeneProductsAsList();
		List<Reaction> toRemove = new ArrayList<>();
		for(GeneProduct g:enzymes) {
			List<Double> expression = expressionData.get(g.getId());
			if(expression==null) expression = expressionData.get(g.getName());
			if(expression==null) expression = expressionData.get(g.getLabel());
			if(expression==null || !passFilter(expression)) {
				System.out.println("Enzyme "+g.getId()+" not "+((expression==null)?"found":"expressed")+" . Name: "+g.getName()+" Label: "+g.getLabel());
				List<Reaction> reactions = network.getReactions(g);
				network.removeGeneProduct(g);
				for(Reaction r:reactions) {
					if(r.getEnzymes().size()==0) {
						System.out.println("Reaction "+r.getId()+" does not have enzymes left");
						toRemove.add(r);
					} else {
						System.out.println("Reaction "+r.getId()+". Enzymes left: "+r.getEnzymes().size());
					}
				}
			}
		}
		for(Reaction r: toRemove) network.removeReaction(r.getId());
	}
	private boolean passFilter(List<Double> expression) {
		int n=expression.size();
		int sum=0;
		int c = 0;
		int max = 0;
		for(double d:expression) {
			sum+=d;
			if (d>0.05) c++;
			if(max<d) d=max;
		}
		return sum>=0.01*n || c>=3 || max>=1;
	}
	public static void main(String[] args) throws Exception {
		MetabolicNetwork network = MetabolicNetwork.load(args[0]);
		CombineExpressionInformationService instance = new CombineExpressionInformationService(network);
        Map<String,List<Double>> expressionData = loadExpressionData(args[1]);
        instance.run(expressionData);
        MetabolicNetworkXMLWriter writer = new MetabolicNetworkXMLWriter();
        writer.saveNetwork(network, args[2]);

	}
	private static Map<String, List<Double>> loadExpressionData(String filename) throws IOException {
		Map<String, List<Double>> answer = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line = reader.readLine();
			String [] items = line.split(",");
			int n = items.length-1;
			line = reader.readLine();
			while (line!=null) {
				items = line.split(",");
				String tId = items[0];
				List<Double> tpms = new ArrayList<>(n);
				for(int i=1;i<items.length;i++) {
					tpms.add(Double.parseDouble(items[i]));
				}
				answer.put(tId, tpms);
				line = reader.readLine();
			}
		}
		return answer;
	}
}
