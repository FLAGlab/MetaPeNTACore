package metapenta.services.dto;

import metapenta.model.MetabolicNetwork;
import java.util.List;

public class ShortestPathsDTO {
	

    private MetabolicNetwork network;
    private String originId;
    private String destinationId;
    private List<String> path;
    
	public ShortestPathsDTO(MetabolicNetwork network, String originId, String destinationId, List<String> path) {
		super();
		this.network = network;
		this.originId = originId;
		this.destinationId = destinationId;
		this.path = path;
	}
	public MetabolicNetwork getNetwork() {
		return network;
	}
	public String getOriginId() {
		return originId;
	}
	public String getDestinationId() {
		return destinationId;
	}
	public List<String> getPath() {
		return path;
	}
    
    
}
