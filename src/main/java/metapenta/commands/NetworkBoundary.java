package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.model.dto.NetworkBoundaryDTO;
import metapenta.tools.io.writers.NetworkBoundaryWriter;

/**
 * args[0]: First metabolic network in XML format
 * args[1]: Output Path
 */
public class NetworkBoundary {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);
        NetworkBoundaryDTO networkBoundary = network.findNetworkBoundary();

        NetworkBoundaryWriter boundaryWriter = new NetworkBoundaryWriter(networkBoundary, args[1]);
        boundaryWriter.write();
    }
}
