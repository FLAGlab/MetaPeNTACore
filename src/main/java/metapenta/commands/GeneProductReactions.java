package metapenta.commands;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.networks.MetabolicNetworkElements;
import metapenta.services.MetabolicNetworkService;
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
		MetabolicNetworkService network = new MetabolicNetworkService(args[0]);

		GeneProductReactionsDTO geneProductReactions = network.getGeneProductReactions(args[1]);
		Writer geneProductReactionsWriter = new GeneProductReactionsWriter(geneProductReactions, args[2]);
		geneProductReactionsWriter.write();

	}
}


