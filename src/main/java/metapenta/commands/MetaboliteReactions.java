package metapenta.commands;

import metapenta.model.MetaPenta;
import metapenta.model.dto.MetaboliteReactionsDTO;
import metapenta.tools.io.writers.MetaboliteReactionsWriter;

public class MetaboliteReactions {
	/**
	 * The main method of class
	 * args[0] the path of the XML file
	 * args[1] A metabolite to find the reactions
	 * args[2] Name of file out
	 * @throws Exception if exists any error of I/O
	 */
	public static void main(String[] args) throws Exception {
		MetaPenta network = new MetaPenta(args[0]);

		MetaboliteReactionsDTO metabolitesReaction = network.getMetaboliteReactions(args[1]);

		MetaboliteReactionsWriter metaboliteReactionsWriter = new MetaboliteReactionsWriter(metabolitesReaction, args[2]);
		metaboliteReactionsWriter.write();

	}
}
