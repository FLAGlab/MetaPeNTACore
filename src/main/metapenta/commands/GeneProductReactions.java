package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.dto.GeneProductReactionsDTO;
import metapenta.io.jsonWriters.GeneProductReactionsWriter;

/**
 * args[0] SMBL with metabolic network
 * args[1] Gene product ID
 * args[2] Output file name
 */
public class GeneProductReactions {

	public static void main(String[] args) throws Exception  {
		MetabolicNetworkService network = new MetabolicNetworkService(args[0]);

		GeneProductReactionsDTO geneProductReactions = network.getGeneProductReactions(args[1]);
		GeneProductReactionsWriter geneProductReactionsWriter = new GeneProductReactionsWriter(geneProductReactions, args[2]);
		geneProductReactionsWriter.write();

	}
}


