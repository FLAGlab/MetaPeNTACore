package metapenta.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import metapenta.io.MetabolicNetworkXMLLoader;

public class MetabolicNetwork {
	private Logger log = Logger.getAnonymousLogger();
	private Map<String, GeneProduct> enzymes = new TreeMap<>();
	private Map<String,String> parameters = new HashMap<>();
	private Map<String, Compartment> compartments = new TreeMap<>();
	private Map<String, Metabolite> metabolites = new TreeMap<>();
	private Map<String, List<Metabolite>> metabolitesByCompartment = new HashMap<>();
	private Map<String, Reaction> reactions = new TreeMap<>();
	private Map<String, ReactionGroup> reactionGroups = new TreeMap<>();
	
	public void addParameter (String id, String value) {
		parameters.put(id, value);
	}
	public String getValueParameter (String parameterId) {
		return parameters.get(parameterId);
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void addGeneProduct (GeneProduct product) {
		String id = product.getId();
		GeneProduct existing = enzymes.get(id);
		if(existing!=null) {
			if(existing!=product) log.warning("Trying to add different gene products with the same id "+id+ " existing: "+existing+" new: "+product);
		} else enzymes.put(product.getId(), product);
	}
	public GeneProduct getGeneProduct (String id) {
		return enzymes.get(id);
	}
	public boolean existsGeneProduct (String id) {
		return enzymes.containsKey(id);
	}
	public List<GeneProduct> getGeneProductsAsList() {
		return new ArrayList<>(enzymes.values());
	}
	public void removeAllGeneProducts() {
		for(Reaction r:reactions.values()) {
			r.removeAllEnzymes();
		}
		enzymes.clear();
	}
	public void addCompartment (Compartment compartment) {
		compartments.put(compartment.getId(), compartment);
	}
	public List<Compartment> getCompartmentsAsList() {
		return new ArrayList<>(compartments.values());
	}
	public void addMetabolite (Metabolite metabolite) {
		metabolites.put(metabolite.getId(), metabolite);
		String compartmentId = metabolite.getCompartmentId();
		if(compartmentId==null) {
			log.warning("No compartment for metabolite with id "+metabolite.getId());
		}
		List<Metabolite> metabolitesCompartment = metabolitesByCompartment.computeIfAbsent(compartmentId, v->new ArrayList<Metabolite>());
		metabolitesCompartment.add(metabolite);
	}
	public Metabolite getMetabolite(String id) {
		return metabolites.get(id);
	}
	public List<Metabolite> getMetabolitesAsList () {
		return new ArrayList<>(metabolites.values());
	}
	public boolean existsMetabolite (String id) {
		return metabolites.containsKey(id);
	}
	public Map<String, List<Metabolite>> getMetabolitesByCompartments() {
		return metabolitesByCompartment;
	}
	private void removeMetabolite(String id) {
		Metabolite m = metabolites.get(id);
		if(m == null) {
			log.warning("WARN. Metabolite not found with id "+id);
			return;
		}
		String compartmentId = m.getCompartmentId();
		List<Metabolite> metabolitesByComp = metabolitesByCompartment.get(compartmentId);
		if(metabolitesByComp!=null) metabolitesByComp.remove(m);
		else log.warning("Metabolite with id "+id+" weird compartment: "+compartmentId); 
		metabolites.remove(id);
	}
	public void removeMetabolites (Set<String> metaboliteIds) {
		for(String id:metaboliteIds) removeMetabolite(id);
	}
	public void addReaction (Reaction reaction) {
		if(reaction.getEnzymes()!=null) {
			for (GeneProduct enzyme: reaction.getEnzymes()) addGeneProduct(enzyme);
		}
		reactions.put(reaction.getId(), reaction);
	}
	public void addReactions(List<Reaction> reactions){
		for(Reaction reaction: reactions) addReaction(reaction);
    }
    public Reaction getReaction (String id) {
		return reactions.get(id);
	}
	public List<Reaction> getReactionsAsList () {
		return new ArrayList<>(reactions.values());
	}
	public Set<String> getReactionIds() {
		return reactions.keySet();
	}
    public boolean existsReaction (String id) {
		return reactions.containsKey(id);
	}
	public void removeReactions(Set<String> reactionIds) {
		for(String id:reactionIds) removeReaction(id);
	}
	public void removeReaction(String id) {
		reactions.remove(id);
		for(ReactionGroup group:reactionGroups.values()) {
			group.removeReaction(id);
		}
	}
	public Reaction findReactionByKeggId(String id) {
		for(Reaction r:reactions.values()) {
			if(r.getKeggId().equals(id)) return r;
		}
		return null;
	}
	public void addReactionGroup(ReactionGroup g) {
		reactionGroups.put(g.getId(), g);
	}
	
	public Map<String, ReactionGroup> getReactionGroups() {
		return reactionGroups;
	}
	
	
	/**
	 * Returns a list with reactions having metabolites without formula
	 * @return List<Reaction> Reactions involved in metabolites without formula
	 */
	public List<Reaction> getReactionsMetabolitesWithoutFormula() {
		Set<String> metaboliteIds = new HashSet<>();
		for(Metabolite m:metabolites.values()) {
			//if("_2__45__Hydroxy__45__carboxylates__91__c__93__".equals(m.ID())) System.out.println("Formula: "+m.getChemicalFormula());
			if(m.getChemicalFormula()==null) {
				metaboliteIds.add(m.getId());
				log.info("Next metabolite without formula: "+m.getId());
			}
		}
		log.info("Total metabolites without formula: "+metaboliteIds.size());
		return getReactionsByMetaboliteIds(metaboliteIds);
    }
	private List<Reaction> getReactionsByMetaboliteIds(Set<String> metaboliteIds) {
		List<Reaction> answer = new ArrayList<>();
		for(Reaction r:reactions.values()) {
			List<ReactionComponent> allMetabolites = new ArrayList<>();
			allMetabolites.addAll(r.getReactants());
			allMetabolites.addAll(r.getProducts());
			for(ReactionComponent comp:allMetabolites) {
				if(metaboliteIds.contains(comp.getMetaboliteId())) {
					answer.add(r);
					break;
				}
			}
		}
		log.info("Total reactions with metabolites without formula: "+answer.size());
		return answer;
	}
	public List<Reaction> inferExchangeReactions() {
		List<Reaction> exchangeReactions = new ArrayList<>();
		for(Reaction reaction : reactions.values()) {
			if (reaction.getProducts().isEmpty() || reaction.getReactants().isEmpty()) {
				exchangeReactions.add(reaction);
			}
		}
		return exchangeReactions;
	}
	public List<Reaction> getReversibleReactions () {
		List<Reaction> answer = new ArrayList<>();
		for(Reaction reaction : reactions.values()) {
			if(reaction.isReversible()) answer.add(reaction);
		}
		return answer;
	}
	public List<Reaction> getIrreversibleReactions () {
		List<Reaction> answer = new ArrayList<>();
		for(Reaction reaction : reactions.values()) {
			if(reaction.isReversible()) answer.add(reaction);
		}
		return answer;
	}
	public List<Reaction> getUnbalancedReactions () {
		List<Reaction> reactionsUnBalanced = new ArrayList<>();
		for (Reaction reaction:reactions.values()) {
			if(!reaction.isBalanced()) {
				reactionsUnBalanced.add(reaction);
			}
		}
		return reactionsUnBalanced;
	}
	public Map<String, String> findReasonsUnbalancedReactions() {
		List<Reaction> unbalanced = getUnbalancedReactions();
		Map<String, String> answer = new HashMap<String, String>();
		for(Reaction r:unbalanced) answer.put(r.getId(), r.findReasonNotBalanced());
		return answer;
	}

    public List<Metabolite> getRootNoProductionGaps() {
        Map<String,Metabolite> rootNoProductionGaps = new HashMap<String, Metabolite>();
        for(Metabolite m:metabolites.values()) rootNoProductionGaps.put(m.getId(),m);

        for (Reaction reaction : reactions.values()) {
            for (ReactionComponent c : reaction.getProducts()) {
                rootNoProductionGaps.remove(c.getMetaboliteId());
            }

            if (reaction.isReversible()) {
                for (ReactionComponent c : reaction.getReactants()) {
                    rootNoProductionGaps.remove(c.getMetaboliteId());
                }
            }
        }

        return new ArrayList<>(rootNoProductionGaps.values());
    }

    public List<Metabolite> getRootNoConsumptionGaps() {
    	Map<String,Metabolite> rootNoProductionGaps = new HashMap<String, Metabolite>();
        for(Metabolite m:metabolites.values()) rootNoProductionGaps.put(m.getId(),m);

        for (Reaction reaction : reactions.values()) {
            for (ReactionComponent c : reaction.getReactants()) {
                rootNoProductionGaps.remove(c.getMetaboliteId());
            }

            if (reaction.isReversible()) {
                for (ReactionComponent c : reaction.getProducts()) {
                    rootNoProductionGaps.remove(c.getMetaboliteId());
                }
            }
        }

        return new ArrayList<>(rootNoProductionGaps.values());
    }
    public List<GeneProduct> getGeneProductsReaction(String reactionID) {
		Reaction r = reactions.get(reactionID);
		if(r==null) return null;
		return r.getEnzymes();
	}
    public static MetabolicNetwork load(String filename) throws IOException {
    	MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
    	return loader.loadNetwork(filename);
    }
	
}
