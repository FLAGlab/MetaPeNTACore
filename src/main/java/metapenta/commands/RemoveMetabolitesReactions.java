package metapenta.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.services.IMetabolicNetworkService;
import metapenta.services.MetabolicNetworkService;
import metapenta.services.SelectSubnetworkService;
import metapenta.tools.io.writers.MetabolicNetworkXMLOutput;

public class RemoveMetabolitesReactions {

	public static void main(String[] args) throws Exception {
		IMetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetabolicNetwork network = networkService.getNetwork();
		System.out.println("Loaded reactions: "+network.getReactionsAsList().size());
		Set<String> metaboliteIds = SelectSubnetworkService.loadIds(args[1]);
		Set<String> reactionIds = SelectSubnetworkService.loadIds(args[2]);
		network.removeReactions(reactionIds);
		network.removeMetabolites(metaboliteIds);
		System.out.println("Remaining reactions: "+network.getReactionsAsList().size());
		MetabolicNetworkXMLOutput writer = new MetabolicNetworkXMLOutput();
		writer.saveNetwork(network, args[3]);
	}

}
