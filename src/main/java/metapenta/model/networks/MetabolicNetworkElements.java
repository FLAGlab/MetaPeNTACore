package metapenta.model.networks;
import java.util.*;

import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;

/**
 * Represents a metabolic network of reactions on metabolites
 * @author Jorge Duitama
 */
public class MetabolicNetworkElements {
	private Map<String, List<Metabolite>> metabolitesByCompartment = new HashMap<>();
	private Map<String, GeneProduct> geneProducts = new TreeMap<>();
	private Map<String, Metabolite> metabolites = new TreeMap<>();
	private Set<String> compartments = new TreeSet<>();
	private Map<String, Reaction> reactions = new TreeMap<>();

	public void addGeneProduct(GeneProduct product) {
		geneProducts.put(product.getId(), product);
	}

	public void addMetabolite(Metabolite metabolite) {
		metabolites.put(metabolite.getId(), metabolite);
		compartments.add(metabolite.getCompartment());
	}


	public void addReactions(List<Reaction> reactions) {
		for(Reaction reaction: reactions) {
			addReaction(reaction);
		}
	}

	public void addReaction(Reaction reaction) {
		reactions.put(reaction.getId(), reaction);
		addReactionsMetaboliteIfAbsent(reaction);
	}

	private void addReactionsMetaboliteIfAbsent(Reaction reaction){
		addReactionComponentsIfAbsent(reaction.getReactants());
		addReactionComponentsIfAbsent(reaction.getProducts());
	}

	private void addReactionComponentsIfAbsent(List<ReactionComponent> reactants){
		for (ReactionComponent reactant: reactants){
			Metabolite metabolite = reactant.getMetabolite();

			metabolites.putIfAbsent(metabolite.getId(), metabolite);
		}
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

	public GeneProduct getGeneProduct(String id) throws GeneProductDoesNotExitsException {
		GeneProduct geneProduct = geneProducts.get(id);

		if (geneProduct == null){
			throw new GeneProductDoesNotExitsException(id);
		}

		return geneProduct;
	}

	public List<String> getReactionIds(){
		List<String> reactionIds = new ArrayList();
		Set<String> keys = reactions.keySet();
		reactionIds.addAll(keys);

		return reactionIds;
	}



	public List<Metabolite> getMetabolitesAsList() {
		return new ArrayList<>(metabolites.values());
	}

	public List<Reaction> getReactionsAsList () {
		return new ArrayList<>(reactions.values());
	}


	public List<Metabolite> getMetabolitesByCompartment(String compartment){
		return metabolitesByCompartment.get(compartment);
	}

	private void calculateReactionsByCompartments(){
		List<Metabolite> metabolites = getMetabolitesAsList();
		for(Metabolite metabolite: metabolites) {
			List<Metabolite> compartmentMetabolites = metabolitesByCompartment.computeIfAbsent(metabolite.getId(), k -> new ArrayList<>());
			compartmentMetabolites.add(metabolite);
		}
	}

	public Map<String, List<Metabolite>> getReactionsByCompartments() {
		return metabolitesByCompartment;
	}
}