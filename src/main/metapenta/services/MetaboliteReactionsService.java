package metapenta.services;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.services.dto.MetaboliteReactionsDTO;
import metapenta.io.jsonWriters.MetaboliteReactionsWriter;

import java.util.ArrayList;
import java.util.List;

public class MetaboliteReactionsService {

    private static final String IS_PRODUCT = "is_product";

    private static final String IS_SUBSTRATE = "is_substrate";

    private MetabolicNetwork metabolicNetwork;

    private Metabolite metabolite;

    
    public MetabolicNetwork getMetabolicNetwork() {
		return metabolicNetwork;
	}

	public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
		this.metabolicNetwork = metabolicNetwork;
	}

	public Metabolite getMetabolite() {
		return metabolite;
	}

	public void setMetabolite(Metabolite metabolite) {
		this.metabolite = metabolite;
	}
	public void setMetabolite(String value) {
		this.metabolite = metabolicNetwork.getMetabolite(value);
	}

	public MetaboliteReactionsDTO getMetaboliteReactions() {
        List<Reaction> isSubstrate = getReactionsByCriteria(IS_SUBSTRATE);
        List<Reaction> isProduct = getReactionsByCriteria(IS_PRODUCT);
        return new MetaboliteReactionsDTO(isSubstrate, isProduct);
    }

    private List<Reaction> getReactionsByCriteria(String criteria){
        List<Reaction> reactions = metabolicNetwork.getReactionsAsList();
        
        for(Reaction r:reactions) {
        	List<ReactionComponent> metabolites = new ArrayList<ReactionComponent>();
        	if(IS_SUBSTRATE==criteria) {
        		metabolites = r.getReactants();
        	}
        	if(IS_PRODUCT==criteria) {
        		metabolites = r.getProducts();
        	}
        	for(ReactionComponent c:metabolites) {
        		if(c.getMetaboliteId().equals(metabolite.getId())) {
        			reactions.add(r);
        			break;
        		}
        	}
        }
        return reactions;
    }
    /**
	 * The main method of class
	 * args[0] the path of the XML file
	 * args[1] A metabolite to find the reactions
	 * args[2] Name of file out
	 * @throws Exception if exists any error of I/O
	 */
	public static void main(String[] args) throws Exception {
		MetaboliteReactionsService instance = new MetaboliteReactionsService();
		instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
		instance.setMetabolite(args[1]);
		MetaboliteReactionsDTO metabolitesReaction = instance.getMetaboliteReactions();

		MetaboliteReactionsWriter metaboliteReactionsWriter = new MetaboliteReactionsWriter(metabolitesReaction, args[2]);
		metaboliteReactionsWriter.write();

	}
}
