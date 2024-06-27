package metapenta.commands;

import java.io.PrintStream;
import java.util.List;

import metapenta.model.Reaction;
import metapenta.model.MetabolicNetwork;
import metapenta.services.MetabolicNetworkService;

public class FindMetabolitesWithoutFormula {

	public static void main(String[] args) throws Exception {
		MetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetabolicNetwork network = networkService.getNetwork(); 

		List<Reaction> reactions = network.getReactionsMetabolitesWithoutFormula();
		try (PrintStream out = new PrintStream(args[1])) {
			printReactions(reactions, out);
		}
		

	}

	public static void printReactions(List<Reaction> reactions,  PrintStream  out) {
		for(Reaction r: reactions) out.println(r.getId());
		
	}

}
