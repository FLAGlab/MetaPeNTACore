package metapenta.model;

import metapenta.model.dto.*;
import metapenta.model.errors.MetaboliteDoesNotExistsException;
import metapenta.model.errors.SourceAndTargetPlacesAreEqualException;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.params.FindAllPathsParams;
import metapenta.model.petrinet.Place;
import metapenta.services.*;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;
import metapenta.tools.io.loaders.MetabolicPetriNetLoader;

public class MetaPenta{
    private MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
    private MetabolicPetriNet petriNet;
    public MetaPenta(String networkFile) throws Exception{
        MetabolicPetriNetLoader petriNetLoader = new MetabolicPetriNetLoader();
        petriNet = petriNetLoader.load(networkFile);
    }

    public ConnectedComponentsDTO connectedComponents() {
        ConnectedComponentsService connectedComponentsService = new ConnectedComponentsService(this.petriNet);

        return connectedComponentsService.getConnectedComponents();
    }

    public NetworkBoundaryDTO findNetworkBoundary() {
        NetworkBoundaryService networkBoundaryService = new NetworkBoundaryService(petriNet.getSinks(), petriNet.getSources());

        return networkBoundaryService.getNetworkBoundary();
    }

    public MetaboliteReactionsDTO getMetaboliteReactions(String metaboliteId) throws MetaboliteDoesNotExistsException{
        Place<Metabolite> metabolitePlace = getPlace(metaboliteId);
        MetaboliteReactionsService service = new MetaboliteReactionsService(petriNet, metabolitePlace.getObject());

        return service.getMetaboliteReactions();
    }


    public ShortestPathsDTO getShortestPaths(String metaboliteId) throws MetaboliteDoesNotExistsException {
        Place<Metabolite> origin = getPlace(metaboliteId);
        ShortestPathByTransitionNumberService service = new ShortestPathByTransitionNumberService(petriNet, origin);

        return service.getShortestPath();
    }

    private Place getPlace(String placeId) throws MetaboliteDoesNotExistsException {
        Place place = petriNet.getPlace(placeId);

        if (place == null) {
            throw new MetaboliteDoesNotExistsException();
        }

        return place;
    }

    public PathsDTO getAllPaths(FindAllPathsParams params) throws SourceAndTargetPlacesAreEqualException {
        AllPathsService service = new AllPathsService(petriNet, params);

        return service.getAllPaths();
    }

    public MetabolicPetriNet getPetriNet() {
        return petriNet;
    }
}
