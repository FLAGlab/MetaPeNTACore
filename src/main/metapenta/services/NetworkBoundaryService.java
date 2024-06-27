package metapenta.services;

import metapenta.io.jsonWriters.NetworkBoundaryWriter;
import metapenta.model.MetabolicNetwork;
import metapenta.services.dto.NetworkBoundaryDTO;

public class NetworkBoundaryService {
	private MetabolicNetwork network;
	public void setMetabolicNetwork(MetabolicNetwork network){
    	this.network = network;
    }
    public NetworkBoundaryDTO getNetworkBoundary() {
        return new NetworkBoundaryDTO(network.inferExchangeReactions());
    }
    
    /**
     * args[0]: First metabolic network in XML format
     * args[1]: Output file path
     */
    public static void main(String[] args) throws Exception {
        NetworkBoundaryService instance = new NetworkBoundaryService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
        NetworkBoundaryDTO networkBoundary = instance.getNetworkBoundary();
        NetworkBoundaryWriter boundaryWriter = new NetworkBoundaryWriter(networkBoundary, args[1]);
        boundaryWriter.write();
    }
}
