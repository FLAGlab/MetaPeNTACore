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
import metapenta.tools.io.writers.MetabolicNetworkXMLOutput;

public class RemoveMetabolitesReactions {

	public static void main(String[] args) throws Exception {
		IMetabolicNetworkService networkService = new MetabolicNetworkService(args[0]);

		MetabolicNetwork network = networkService.getNetwork();
		System.out.println("Loaded reactions: "+network.getReactionsAsList().size());
		Set<String> metaboliteIds = loadIds(args[1]);
		Set<String> reactionIds = loadIds(args[2]);
		network.removeReactions(reactionIds);
		network.removeMetabolites(metaboliteIds);
		System.out.println("Remaining reactions: "+network.getReactionsAsList().size());
		MetabolicNetworkXMLOutput writer = new MetabolicNetworkXMLOutput();
		writer.saveNetwork(network, args[3]);
	}

	private static Set<String> loadIds(String filename) throws IOException {
		Set<String> ids = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line = reader.readLine();
			while (line!=null) {
				ids.add(line);
				line = reader.readLine();
			}
		}
		return ids;
	}

}
