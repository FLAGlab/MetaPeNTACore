package metapenta.model.networks;
import java.util.*;

import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.metabolic.network.Compartment;
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
	private Map<String,String> parameters = new HashMap<>();
	private Map<String, Compartment> compartments = new TreeMap<>();
	private Map<String, GeneProduct> geneProducts = new TreeMap<>();
	private Map<String, Metabolite> metabolites = new TreeMap<>();
	private Map<String, Reaction> reactions = new TreeMap<>();

	public boolean existsMetabolite(String metaboliteID){
		return metabolites.containsKey(metaboliteID);
	}

	public boolean existsGeneProduct(String geneProductID){
		return geneProducts.containsKey(geneProductID);
	}


	public void addGeneProduct(GeneProduct product) {
		geneProducts.put(product.ID(), product);
	}

	public void addCompartment(Compartment compartment) {
		compartments.put(compartment.getId(), compartment);
	}

	public List<Compartment> getCompartmentsAsList() {
		return new ArrayList<>(compartments.values());
	}
	public void addParameter(String id, String value) {
		parameters.put(id, value);
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public void addMetabolite(Metabolite metabolite) {
		metabolites.put(metabolite.ID(), metabolite);
	}


	public void addReactions(List<Reaction> reactions) {
		for(Reaction reaction: reactions) {
			addReaction(reaction);
		}
	}

	public void addReaction(Reaction reaction) {
		reactions.put(reaction.ID(), reaction);
		addReactionsMetaboliteIfAbsent(reaction);
	}

	private void addReactionsMetaboliteIfAbsent(Reaction reaction){
		addReactionComponentsIfAbsent(reaction.getReactants());
		addReactionComponentsIfAbsent(reaction.getProducts());
	}

	private void addReactionComponentsIfAbsent(List<ReactionComponent> reactants){
		for (ReactionComponent reactant: reactants){
			Metabolite metabolite = reactant.getMetabolite();

			metabolites.putIfAbsent(metabolite.ID(), metabolite);
		}
	}

	public String getValueParameter(String parameterId) {
		return parameters.get(parameterId);
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

	public List<GeneProduct> getGeneProductsAsList() {
		return new ArrayList<>(geneProducts.values());
	}
	
	public void removeMetabolites(Set<String> metaboliteIds) {
		for(String id:metaboliteIds) removeMetabolite(id);
		
	}

	private void removeMetabolite(String id) {
		Metabolite m = metabolites.get(id);
		if(m == null) {
			System.err.println("WARN. Metabolite not found with id "+id);
			return;
		}
		String compartmentId = m.getCompartmentId();
		List<Metabolite> metabolitesByComp = metabolitesByCompartment.get(compartmentId);
		if(metabolitesByComp!=null) metabolitesByComp.remove(m);
		else System.err.println("WARN. Metabolite with id "+id+" weird compartment: "+compartmentId); 
		metabolites.remove(id);
	}

	public void removeReactions(Set<String> reactionIds) {
		for(String id:reactionIds) removeReaction(id);
		
	}
	
	private void removeReaction(String id) {
		reactions.remove(id);
		
	}

	public List<Reaction> getReactionsUnbalanced() {
		List<Reaction> reactionsUnBalanced = new ArrayList<>();

		Set<String> keys=reactions.keySet();
		for (String key : keys) {
			Reaction reaction = reactions.get(key);
			boolean balanced = reaction.isBalanced();

			if(!balanced) {
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
		List<String> reactionIds = new ArrayList<String>();
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
			List<Metabolite> compartmentMetabolites = metabolitesByCompartment.computeIfAbsent(metabolite.ID(), k -> new ArrayList<>());
			compartmentMetabolites.add(metabolite);
		}
	}

	public Map<String, List<Metabolite>> getReactionsByCompartments() {
		return metabolitesByCompartment;
	}
	public List<Reaction> getReactionsMetabolitesWithoutFormula() {
		Set<String> metaboliteIds = new HashSet<>();
		for(Metabolite m:metabolites.values()) {
			if("_2__45__Hydroxy__45__carboxylates__91__c__93__".equals(m.ID())) System.out.println("Formula: "+m.getChemicalFormula());
			if(m.getChemicalFormula()==null) {
				metaboliteIds.add(m.ID());
				System.out.println("Next metabolite without formula: "+m.ID());
			}
		}
		System.out.println("Total metabolites without formula: "+metaboliteIds.size());
		return getReactionsByMetaboliteIds(metaboliteIds);
	}

	private List<Reaction> getReactionsByMetaboliteIds(Set<String> metaboliteIds) {
		List<Reaction> answer = new ArrayList<>();
		for(Reaction r:reactions.values()) {
			List<ReactionComponent> allMetabolites = new ArrayList<>();
			allMetabolites.addAll(r.getReactants());
			allMetabolites.addAll(r.getProducts());
			for(ReactionComponent comp:allMetabolites) {
				if(metaboliteIds.contains(comp.getMetabolite().ID())) {
					answer.add(r);
					break;
				}
			}
		}
		System.out.println("Total reactions with metabolites without formula: "+answer.size());
		return answer;
	}

	
}