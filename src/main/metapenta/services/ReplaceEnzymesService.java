package metapenta.services;

import java.util.List;

import metapenta.io.MetabolicNetworkXMLWriter;
import metapenta.model.GeneProduct;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Reaction;

public class ReplaceEnzymesService {
	private MetabolicNetwork network;
	public ReplaceEnzymesService(MetabolicNetwork network) {
		this.network = network;
	}

	private void replaceEnzymes(MetabolicNetwork network2) {
		network.removeAllGeneProducts();
		List<Reaction> reactions = network.getReactionsAsList();
		for(Reaction r:reactions) {
			Reaction r2 = network2.getReaction(r.getId());
			if(r2==null) r2 = network2.getReaction(r.getKeggId());
			if(r2==null) {
				System.err.println("WARN. Reaction with ids: "+r.getId()+" "+r.getKeggId()+" not found in new network");
				continue;
			}
			List<GeneProduct> enzymes = r2.getEnzymes();
			for(GeneProduct enzyme:enzymes) {
				network.addGeneProduct(enzyme);
			}
			r.setEnzymes(enzymes);
		}
	}

	public MetabolicNetwork getNetwork() {
		return network;
	}
	
	public static void main(String[] args) throws Exception {
        MetabolicNetwork network = MetabolicNetwork.load(args[0]);
        ReplaceEnzymesService instance = new ReplaceEnzymesService(network);
        MetabolicNetwork network2 = MetabolicNetwork.load(args[1]);
        instance.replaceEnzymes(network2);
        MetabolicNetworkXMLWriter writer = new MetabolicNetworkXMLWriter();
        writer.saveNetwork(instance.getNetwork(), args[2]);
        
	}
}
