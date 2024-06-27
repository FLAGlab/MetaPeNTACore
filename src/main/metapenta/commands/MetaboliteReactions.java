package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.dto.MetaboliteReactionsDTO;
import metapenta.io.jsonWriters.MetaboliteReactionsWriter;

public class MetaboliteReactions {
	/**
	 * The main method of class
	 * args[0] the path of the XML file
	 * args[1] A metabolite to find the reactions
	 * args[2] Name of file out
	 * @throws Exception if exists any error of I/O
	 */
	public static void main(String[] args) throws Exception {
		MetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetaboliteReactionsDTO metabolitesReaction = networkService.getMetaboliteReactions(args[1]);

		MetaboliteReactionsWriter metaboliteReactionsWriter = new MetaboliteReactionsWriter(metabolitesReaction, args[2]);
		metaboliteReactionsWriter.write();

	}
}
