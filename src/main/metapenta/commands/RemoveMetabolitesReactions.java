package metapenta.commands;

import java.util.Set;

import metapenta.model.MetabolicNetwork;
import metapenta.services.MetabolicNetworkService;
import metapenta.services.SelectSubnetworkService;
import metapenta.io.MetabolicNetworkXMLWriter;

public class RemoveMetabolitesReactions {

	public static void main(String[] args) throws Exception {
		MetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetabolicNetwork network = networkService.getNetwork();
		System.out.println("Loaded reactions: "+network.getReactionsAsList().size());
		Set<String> metaboliteIds = SelectSubnetworkService.loadIds(args[1]);
		Set<String> reactionIds = SelectSubnetworkService.loadIds(args[2]);
		network.removeReactions(reactionIds);
		network.removeMetabolites(metaboliteIds);
		System.out.println("Remaining reactions: "+network.getReactionsAsList().size());
		MetabolicNetworkXMLWriter writer = new MetabolicNetworkXMLWriter();
		writer.saveNetwork(network, args[3]);
	}

}
