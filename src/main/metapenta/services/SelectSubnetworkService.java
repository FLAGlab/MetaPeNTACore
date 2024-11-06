package metapenta.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import metapenta.model.Compartment;
import metapenta.model.GeneProduct;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.model.ReactionGroup;
import metapenta.model.MetabolicNetwork;
import metapenta.io.MetabolicNetworkXMLWriter;

public class SelectSubnetworkService {

	private MetabolicNetwork network;
	public SelectSubnetworkService(MetabolicNetwork network) {
		this.network = network;
	}
	public MetabolicNetwork buildSubnetwork(Set<String> reactionIds) {
		MetabolicNetwork answer = new MetabolicNetwork();
		for(Compartment c:network.getCompartmentsAsList()) answer.addCompartment(c);
		for(Map.Entry<String, String> entry:network.getParameters().entrySet()) answer.addParameter(entry.getKey(), entry.getValue());
		List<Reaction> reactions= new ArrayList<>();
		for(String id:reactionIds) {
			Reaction r = network.getReaction(id);
			if(r==null) {
				r = network.findReactionByKeggId(id);
			}
			if(r==null) {
				System.err.println("Reaction id "+id+" not found in network");
				continue;
			}
			reactions.add(r);
			List<GeneProduct> enzymes = r.getEnzymes();
			for(GeneProduct g: enzymes) answer.addGeneProduct(g);
			List<ReactionComponent> c1 = new ArrayList<>();
			c1.addAll(r.getReactants());
			c1.addAll(r.getProducts());
			for(ReactionComponent rc:c1) {
				Metabolite m = rc.getMetabolite();
				if(answer.getMetabolite(m.getId())==null) answer.addMetabolite(m);
			}	
		}
		for(Reaction r: reactions) answer.addReaction(r);
		Set<String> outReactionIds = answer.getReactionIds();
		for(ReactionGroup group:network.getReactionGroups().values()) {
			Set<String> c = new HashSet<>(group.getReactionIds());
			c.retainAll(outReactionIds);
			if(c.size()>0) {
				ReactionGroup outGroup = new ReactionGroup(group.getId());
				outGroup.setName(group.getName());
				outGroup.setKind(group.getKind());
				outGroup.setSboTerm(group.getSboTerm());
				for(String rid:c) {
					Reaction r = answer.getReaction(rid);
					outGroup.addReaction(r);
				}
				answer.addReactionGroup(outGroup);
			}
		}
		return answer;
		
	}
	public static void main(String[] args) throws Exception {
        MetabolicNetwork network = MetabolicNetwork.load(args[0]);
        SelectSubnetworkService instance = new SelectSubnetworkService(network);
        Set<String> reactionIds = loadIds(args[1]);
        MetabolicNetwork subnetwork = instance.buildSubnetwork(reactionIds);
        MetabolicNetworkXMLWriter writer = new MetabolicNetworkXMLWriter();
        writer.saveNetwork(subnetwork, args[2]);
        
	}
	public static Set<String> loadIds(String filename) throws IOException {
		Set<String> ids = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line = reader.readLine();
			while (line!=null) {
				if(line.length()>0 && line.charAt(0)!='#') ids.add(line);
				line = reader.readLine();
			}
		}
		return ids;
	}


}
