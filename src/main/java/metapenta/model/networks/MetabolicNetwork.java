package metapenta.model.networks;

import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.errors.MetaboliteDoesNotExistsException;
import metapenta.model.metabolic.network.*;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;

import java.util.*;

public class MetabolicNetwork {
    private MetabolicNetworkElements metabolicNetworkElements;

    private PetriNetElements petriNetElements;

    public MetabolicNetwork(){
        this.metabolicNetworkElements = new MetabolicNetworkElements();
        this.petriNetElements = new PetriNetElements();
    }

    public void addGeneProduct(GeneProduct product){
        metabolicNetworkElements.addGeneProduct(product);
    }
    
    public void addCompartment(Compartment compartment) {
		metabolicNetworkElements.addCompartment(compartment);
	}

    public List<Compartment> getCompartmentsAsList() {
		return metabolicNetworkElements.getCompartmentsAsList();
	}

    public void addMetabolite(Metabolite metabolite) {
        metabolicNetworkElements.addMetabolite(metabolite);
    }
    
    public void addParameter(String id, String value) {
		metabolicNetworkElements.addParameter(id, value);
	}

    public String getValueParameter(String parameterId) {
    	return metabolicNetworkElements.getValueParameter(parameterId);
    }

    public Map<String, String> getParameters() {
		return metabolicNetworkElements.getParameters();
	}
   
    public GeneProduct getGeneProduct(String id) throws GeneProductDoesNotExitsException {
        return metabolicNetworkElements.getGeneProduct(id);
    }

    public List<GeneProduct> getGeneProductsAsList() {
		return metabolicNetworkElements.getGeneProductsAsList();
	}
	
    public List<Reaction> getReactionsMetabolitesWithoutFormula() {
    	return metabolicNetworkElements.getReactionsMetabolitesWithoutFormula();
    }
    
    public void removeMetabolites(Set<String> metaboliteIds) {
		metabolicNetworkElements.removeMetabolites(metaboliteIds);
		
	}

	public void removeReactions(Set<String> reactionIds) {
		metabolicNetworkElements.removeReactions(reactionIds);
		
	}

    public Map<String, Transition<Reaction>> getTransitions() {
        return petriNetElements.getTransitions();
    }

    public Transition<Reaction> getTransition(String id){
        return petriNetElements.getTransition(id);
    }

    public Map<String, Place<Metabolite>> getPlaces() {
        return petriNetElements.getPlaces();
    }

    public void AddTransition(String id, Transition<Reaction> transition){
        petriNetElements.addTransition(id, transition);
    }

    public void addPlace(String key, Place<Metabolite> place){
        petriNetElements.addPlace(key, place);
    }

    public Place<Metabolite> getPlace(String id) throws MetaboliteDoesNotExistsException{
        Place<Metabolite> place = petriNetElements.getPlace(id);

        if (place == null) {
            throw new MetaboliteDoesNotExistsException();
        }

        return place;
    }

    public Reaction getReaction(String id){
        return metabolicNetworkElements.getReaction(id);
    }

    public List<String> getPlacesIds(){
        return petriNetElements.getPlacesIDs();
    }

    public List<String> getTransitionsIds(){
        return petriNetElements.getTransitionsIDs();
    }

    public List<Place<Metabolite>> getSources() {
        List<Place<Metabolite>> sourcePlaces = new ArrayList<>();
        List<String> placesIDs = petriNetElements.getPlacesIDs();
        for(String placeID: placesIDs) {
            Place<Metabolite> place = petriNetElements.getPlace(placeID);
            if (place.isSource()){
                sourcePlaces.add(place);
            }
        }

        return sourcePlaces;
    }

    public List<Place<Metabolite>> getSinks() {
        List<Place<Metabolite>> sinkPlaces = new ArrayList<>();
        List<String> placesIDs = petriNetElements.getPlacesIDs();

        for(String placeID: placesIDs) {
            Place<Metabolite> place = petriNetElements.getPlace(placeID);

            if (place.isSink()){
                sinkPlaces.add(place);
            }
        }

        return sinkPlaces;
    }

    public List<String> getReversibleReactionsIds(){
        List<String> reactionIds = new ArrayList<>();
        List<String> IDs = petriNetElements.getTransitionsIDs();
        for(String ID: IDs) {
            Reaction reaction = petriNetElements.getTransition(ID).getObject();
            if(reaction.isReversible()){
                reactionIds.add(reaction.getId());
            }
        }

        return reactionIds;
    }

    public List<String> getIrreversibleReactionsIds(){
        List<String> reactionIds = new ArrayList<>();
        List<String> IDs = petriNetElements.getTransitionsIDs();

        for(String ID: IDs) {
            Reaction reaction = petriNetElements.getTransition(ID).getObject();
            if(!reaction.isReversible()){
                reactionIds.add(reaction.getId());
            }
        }

        return reactionIds;
    }

    public Map<String, List<Metabolite>> getReactionsByCompartments() {
        return metabolicNetworkElements.getReactionsByCompartments();
    }

    public Place<Metabolite> getPlaceByNid(int nid) throws MetaboliteDoesNotExistsException {
        for(String placeID: getPlaces().keySet()){
            Place<Metabolite> place = getPlace(placeID);

            if (place.getObject().getNid() == nid) {
                return place;
            }
        }
        return null;
    }

    public Metabolite getMetabolite(String id) {
        return metabolicNetworkElements.getMetabolite(id);
    }

    public void addReactions(List<Reaction> reactions){
        for(Reaction reaction: reactions) {
            addReaction(reaction);
        }
    }

    public void addReaction(Reaction reaction){
        metabolicNetworkElements.addReaction(reaction);
        petriNetElements.loadReactionToPetriNetwork(reaction);
    }

    public List<Reaction> getReactionsUnbalanced(){
        return metabolicNetworkElements.getReactionsUnbalanced();
    }

    public List<Reaction> getReactionsAsList () {
        return metabolicNetworkElements.getReactionsAsList();
    }

    public List<Metabolite> getMetabolitesAsList() {
        return metabolicNetworkElements.getMetabolitesAsList();
    }

    public Map<Reaction, Map<String, String>> reactionsUnbalancedReason(List<Reaction> reactionsUnbalanced){
        return metabolicNetworkElements.reactionsUnbalancedReason(reactionsUnbalanced);
    }

    public List<Metabolite> getRootNoProductionGaps() {
        HashSet<Metabolite> rootNoProductionGaps = new HashSet<>(metabolicNetworkElements.getMetabolitesAsList());

        for (Reaction reaction : metabolicNetworkElements.getReactionsAsList()) {
            for (ReactionComponent metabolite : reaction.getProducts()) {
                rootNoProductionGaps.remove(metabolite.getMetabolite());
            }

            if (reaction.isReversible()) {
                for (ReactionComponent metabolite : reaction.getReactants()) {
                    rootNoProductionGaps.remove(metabolite.getMetabolite());
                }
            }
        }

        return new ArrayList<>(rootNoProductionGaps);
    }

    public List<Metabolite> getRootNoConsumptionGaps() {
        HashSet<Metabolite> rootNoProductionGaps = new HashSet<>(metabolicNetworkElements.getMetabolitesAsList());

        for (Reaction reaction : metabolicNetworkElements.getReactionsAsList()) {
            for (ReactionComponent metabolite : reaction.getReactants()) {
                rootNoProductionGaps.remove(metabolite.getMetabolite());
            }

            if (reaction.isReversible()) {
                for (ReactionComponent metabolite : reaction.getProducts()) {
                    rootNoProductionGaps.remove(metabolite.getMetabolite());
                }
            }
        }

        return new ArrayList<>(rootNoProductionGaps);
    }
}
