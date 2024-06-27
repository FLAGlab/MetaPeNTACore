package metapenta.commands;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.io.MetabolicNetworkXMLWriter;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Reaction;


public class BalanceReactions {
	private String networkFile;
	private String outPrefix;
	/**
	 * args[0]: Metabolic network in XML format
	 * args[1]: Output
	 */
	public static void main(String[] args) throws Exception{
		BalanceReactions instance = new BalanceReactions();
		instance.networkFile = args[0];
		instance.outPrefix = args[1];
		instance.run();
	}

	public void run() throws Exception {
		MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
		MetabolicNetwork network = loader.loadNetwork(networkFile);
		List<Reaction> reactionsUnbalanced = network.getUnbalancedReactions();
		Map<String, String> reactionsUnbalancedReason = network.findReasonsUnbalancedReactions();
		String reportFile = outPrefix+"_report.tsv";
		try (PrintStream out = new PrintStream(reportFile)) {
			out.print("Reaction id \t Name \t Reason why it is unbalance \t Sum of reactants coefficients \t Sum of products coefficients \t Difference between reactants and products \t It was balanced \t What was modified \t Sum of reactants coefficients \t Sum of products coefficients \t Difference between reactants and products \n ");

			for (Entry<String, String> entry : reactionsUnbalancedReason.entrySet()) {
	            //System.out.println(entry.getKey() + ": " + entry.getValue());
				String id = entry.getKey();
				Reaction r = network.getReaction(id);
				String reasonUnbalanced = entry.getValue();
				out.print(entry.getKey() + "\t "+ r.getName() + "\t");
				//TODO: Fix format
				out.print(reasonUnbalanced + " \t ");
				String outcome = r.balanceReaction();
				
				if(r.isBalanced()) {
					out.print("YES \t ");
					out.print(outcome+ "\t ");
					Map<String, Integer> sumreacts = r.getSumReactants();
					for (Entry<String, Integer> entry1 : sumreacts.entrySet()) {
						out.print("{ "+ entry1.getKey() + ": " + entry1.getValue() + "}");
					}
					out.print(" \t ");
					Map<String, Integer> sumproducts = r.getSumProducts();
					for (Entry<String, Integer> entry2 : sumproducts.entrySet()) {
						out.print("{ "+entry2.getKey() + ": " + entry2.getValue()+ "}");
						System.out.println("PRODUCTS");
						System.out.println("{ "+entry2.getKey() + ": " + entry2.getValue()+ "}");
					}
					out.print(" \t ");
					
					Map<String, Integer> difference = r.getDifference();
					for (Entry<String, Integer> entry3 : difference.entrySet()) {
						out.print("{ "+entry3.getKey() + ": " + entry3.getValue()+ "}");
					}
					
					out.print(" \t \n");
					
					
				}
				else {
					out.print("NO \t ");
					out.print(outcome + " \t ");
					out.print(" - \t - \t - \t - \t \n");
					break;
				}
			}
		}
		
		MetabolicNetworkXMLWriter output = new MetabolicNetworkXMLWriter();
		output.saveNetwork(network, outPrefix+"_network.xml");
	}
}
