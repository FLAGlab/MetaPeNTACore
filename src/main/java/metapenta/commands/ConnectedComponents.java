package metapenta.commands;

import metapenta.services.IMetabolicNetworkService;
import metapenta.model.dto.ConnectedComponentsDTO;
import metapenta.services.MetabolicNetworkService;
import metapenta.tools.io.writers.ConnectedComponentsWriter;

/**
 * args[0]: Metabolic network in XML format
 * args[1]: Output file
 */
public class ConnectedComponents {
	public static void main(String[] args) throws Exception {
		IMetabolicNetworkService network = new MetabolicNetworkService(args[0]);

		ConnectedComponentsDTO connectedComponents = network.connectedComponents();

		ConnectedComponentsWriter connectedComponentsWriter = new ConnectedComponentsWriter(connectedComponents, args[1]);
		connectedComponentsWriter.write();
	}
}
