package metapenta.services;

import metapenta.io.jsonWriters.FindGapsWriter;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.services.dto.FindGapsDTO;

import java.util.List;

public class FindGapsService {
    private MetabolicNetwork metabolicNetwork;
    
	public MetabolicNetwork getMetabolicNetwork() {
		return metabolicNetwork;
	}
	public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
		this.metabolicNetwork = metabolicNetwork;
	}
	public FindGapsDTO getRootGaps() {
    	List<Metabolite> rootNoProduction = metabolicNetwork.getRootNoProductionGaps();
        List<Metabolite> rootNoConsumption = metabolicNetwork.getRootNoConsumptionGaps();
        return new FindGapsDTO(rootNoProduction, rootNoConsumption);
    }
    public static void main(String[] args) throws Exception {
    	FindGapsService instance = new FindGapsService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
        FindGapsDTO gaps = instance.getRootGaps();
        FindGapsWriter findGapsWriter = new FindGapsWriter(gaps, args[1]);
        findGapsWriter.write();
    }
}
