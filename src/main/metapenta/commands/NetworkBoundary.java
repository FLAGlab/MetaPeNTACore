package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.dto.NetworkBoundaryDTO;
import metapenta.io.jsonWriters.NetworkBoundaryWriter;

/**
 * args[0]: First metabolic network in XML format
 * args[1]: Output file path
 */
public class NetworkBoundary {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);
        NetworkBoundaryDTO networkBoundary = network.findNetworkBoundary();

        NetworkBoundaryWriter boundaryWriter = new NetworkBoundaryWriter(networkBoundary, args[1]);
        boundaryWriter.write();
    }
}
