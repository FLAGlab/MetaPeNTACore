package metapenta.commands;

import metapenta.model.MetabolicNetwork;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;
import metapenta.model.dto.GeneProductReactionsDTO;
import metapenta.tools.io.writers.GeneProductReactionsWriter;
import metapenta.tools.io.writers.Writer;

/**
 * args[0] SMBL with metabolic network
 * args[1] Gene product ID
 * args[2] Output file name
 */
public class GeneProductReactions {

	public static void main(String[] args) throws Exception  {
		MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
		MetabolicNetwork network = loader.loadNetwork(args[0]);

		GeneProductReactionsDTO geneProductReactions = network.getGeneProductReactions(args[1]);
		Writer geneProductReactionsWriter = new GeneProductReactionsWriter(geneProductReactions, args[2]);
		geneProductReactionsWriter.write();

	}
}


