package metapenta.model;
import java.util.*;
import metapenta.model.dto.GeneProductReactionsDTO;
import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;


/**
 * Represents a metabolic network of reactions on metabolites
 * @author Jorge Duitama
 */
public class MetabolicNetwork {
	private Map<String, GeneProduct> geneProducts = new TreeMap();
	private Map<String, Metabolite> metabolites = new TreeMap();
	private Set<String> compartments = new TreeSet<String>();
	private Map<String, Reaction> reactions = new TreeMap();

	public void addGeneProduct(GeneProduct product) {
		geneProducts.put(product.getId(), product);
	}

	public void addMetabolite(Metabolite metabolite) {
		metabolites.put(metabolite.getId(), metabolite);
		compartments.add(metabolite.getCompartment());
	}

	public void addReaction(Reaction r) {
		reactions.put(r.getId(),r);
	}

	public Metabolite getMetabolite (String id) {
		return metabolites.get(id);
	}


	public Reaction getReaction(String id) {
		return reactions.get(id);
	}

	public  Map<String,Metabolite> getMetabolites(){
		return metabolites;
	}

	public  Map<String,Reaction> getReactions(){
		return reactions;
	}

	public MetabolicNetwork(Map<String,Metabolite> metabolites, Map<String,Reaction> reactions) {
		this.metabolites = metabolites;
		this.reactions = reactions;
	}

	public MetabolicNetwork() {

	}

	public List<Reaction> getReactionsUnbalanced() {
		List<Reaction> reactionsUnBalanced = new ArrayList<>();

		Set<String> keys=reactions.keySet();
		for (String key : keys) {
			Reaction reaction = reactions.get(key);
			boolean isBalance = reaction.getIsBalanced();

			if(!isBalance) {
				reactionsUnBalanced.add(reaction);
			}
		}
		return reactionsUnBalanced;
	}

	public Map<Reaction, Map<String, String>> reactionsUnbalancedReason(List<Reaction> reactionsUnbalanced){

		Map<Reaction, Map<String, String>> reactionsUnbalancedReason = new HashMap<>();

		for (Reaction reaction: reactionsUnbalanced) {

			Map<String, String> reason =reaction.casesNoBalanced();

			reactionsUnbalancedReason.put(reaction, reason);
		}

		return reactionsUnbalancedReason;


	}
	private List<Reaction> getReactionsCatalyzedBy(String geneProductName){
		List<Reaction> catalyzedReactions = new ArrayList();
		Set<String> keys = reactions.keySet();

		for (String key : keys) {
			Reaction reaction= reactions.get(key);
			List<GeneProduct> enzymes= reaction.getEnzymes();

			for (GeneProduct enzyme : enzymes) {				
				if(enzyme.getName().equals(geneProductName)) {
					catalyzedReactions.add(reaction);
					break;
				}
			}			
		}

		return catalyzedReactions;
	}


	public GeneProductReactionsDTO getGeneProductReactions(String geneProductId) throws GeneProductDoesNotExitsException {
		GeneProduct geneProduct = geneProducts.get(geneProductId);
		List<Reaction> reactions = getReactionsCatalyzedBy(geneProductId);

		return new GeneProductReactionsDTO(reactions, geneProduct);
	}

	public GeneProduct getGeneProduct(String geneProductId) throws GeneProductDoesNotExitsException {
		GeneProduct geneProduct = geneProducts.get(geneProductId);

		if (geneProduct == null){
			throw new GeneProductDoesNotExitsException();
		}

		return geneProduct;
	}

	public List<String> getReactionIds(){
		List<String> reactionIds = new ArrayList();
		Set<String> keys = reactions.keySet();
		reactionIds.addAll(keys);

		return reactionIds;
	}

	public MetabolicNetwork interception(MetabolicNetwork metabolicNetwork){
		Map<String, Metabolite> metabolites = interceptionMetabolites(metabolicNetwork);
		Map<String, Reaction> reactions = interceptionReactions(metabolicNetwork);

		return new MetabolicNetwork(metabolites, reactions);
	}

	private Map<String, Metabolite> interceptionMetabolites(MetabolicNetwork metabolicNetwork){
		Map<String, Metabolite> metabolites = new HashMap<>();
		Set<String> metabolitesKeys = metabolicNetwork.getMetabolites().keySet();
		for (String metaboliteID : metabolitesKeys) {
			Metabolite metabolite = this.metabolites.get(metaboliteID);
			if( metabolite != null ) {
				metabolites.put(metaboliteID, metabolite);
			}
		}		
		return metabolites;
	}

	private Map<String, Reaction> interceptionReactions(MetabolicNetwork metabolicNetwork){
		Map<String, Reaction> reactions = new HashMap<>();
		Set<String> reactionIds = metabolicNetwork.getReactions().keySet();
		for (String reactionId : reactionIds) {
			Reaction reaction = reactions.get(reactionId);
			if( reaction!=null ) {
				reactions.put(reactionId, reaction);
			}
		}		
		return reactions;
	}


	public List<Metabolite> getMetabolitesAsList() {
		return new ArrayList<Metabolite>(metabolites.values());
	}


	public List<Reaction> getReactionsAsList () {
		return new ArrayList<Reaction>(reactions.values());
	}

}