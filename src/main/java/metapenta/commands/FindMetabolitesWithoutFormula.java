package metapenta.commands;

import java.io.PrintStream;
import java.util.List;

import metapenta.model.metabolic.network.Reaction;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.services.IMetabolicNetworkService;
import metapenta.services.MetabolicNetworkService;

public class FindMetabolitesWithoutFormula {

	public static void main(String[] args) throws Exception {
		IMetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetabolicNetwork network = networkService.getNetwork(); 

		List<Reaction> reactions = network.getReactionsMetabolitesWithoutFormula();
		try (PrintStream out = new PrintStream(args[1])) {
			printReactions(reactions, out);
		}
		

	}

	public static void printReactions(List<Reaction> reactions,  PrintStream  out) {
		for(Reaction r: reactions) out.println(r.ID());
		
	}

}
