package metapenta.commands;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.tools.io.writers.MetabolicNetworkXMLOutput;
import metapenta.model.metabolic.network.Reaction;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;

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
		List<Reaction> reactionsUnbalanced = network.getReactionsUnbalanced();
		Map<Reaction, Map<String, String>> reactionsUnbalancedReason = network.reactionsUnbalancedReason(reactionsUnbalanced);
		String reportFile = outPrefix+"_report.tsv";
		try (PrintStream out = new PrintStream(reportFile)) {
			out.print("Reaction id \t Name \t Reason why it is unbalance \t Sum of reactants coefficients \t Sum of products coefficients \t Difference between reactants and products \t It was balanced \t What was modified \t Sum of reactants coefficients \t Sum of products coefficients \t Difference between reactants and products \n ");

			for (Entry<Reaction, Map<String, String>> entry : reactionsUnbalancedReason.entrySet()) {
	            //System.out.println(entry.getKey() + ": " + entry.getValue());
				Reaction r = entry.getKey();
				out.print(r.ID() + "\t "+ r.getName() + "\t");
				Map<String, String> reason = entry.getValue();
				for (Entry<String, String> reasonEntry : reason.entrySet()) {
			        String reasonUnbalanced = reasonEntry.getKey();
			        String[] sum = reasonEntry.getValue().split("\\|");
			        System.out.println(reasonEntry.getValue());
			        System.out.println(sum[0]);
			        out.print(reasonUnbalanced + " \t " +  sum[0] + " \t " + sum[1] + " \t " +  sum[2] + " \t ");
			    }
				
				for (Entry<Boolean, String> balanceEntry : r.balanceReaction().entrySet()) {
					if(balanceEntry.getKey()) {
						out.print("YES \t ");
						out.print(balanceEntry.getValue()+ "\t ");
						List<Map<String, Integer>> reacts = r.getListElements(r.getReactants());
						Map<String, Integer> sumreacts = r.getSumReactants();
						for (Entry<String, Integer> entry1 : sumreacts.entrySet()) {
							out.print("{ "+ entry1.getKey() + ": " + entry1.getValue() + "}");
						}
						out.print(" \t ");
						
						List<Map<String, Integer>> products = r.getListElements(r.getProducts());
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
						out.print(balanceEntry.getValue() + " \t ");
						out.print(" - \t - \t - \t - \t \n");
						break;
					}
				}
			}
		}
		
		MetabolicNetworkXMLOutput output = new MetabolicNetworkXMLOutput();
		output.saveNetwork(network, outPrefix+"_network.xml");
	}
}
