package metapenta.services;

import metapenta.model.dto.*;
import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.errors.MetaboliteDoesNotExistsException;
import metapenta.model.errors.SourceAndTargetPlacesAreEqualException;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.params.FindAllPathsParams;
import metapenta.model.petrinet.Place;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MetabolicNetworkService implements IMetabolicNetworkService {
    private final MetabolicNetwork metabolicNetwork;
    public MetabolicNetworkService(String networkFile) throws Exception{
        MetabolicNetworkXMLLoader networkLoader = new MetabolicNetworkXMLLoader();
        metabolicNetwork = networkLoader.loadNetwork(networkFile);
    }
    public MetabolicNetworkService(InputStream is) throws Exception{
        MetabolicNetworkXMLLoader networkLoader = new MetabolicNetworkXMLLoader();
        metabolicNetwork = networkLoader.loadNetwork(is);
    }
    public ConnectedComponentsDTO connectedComponents() {
        ConnectedComponentsService connectedComponentsService = new ConnectedComponentsService(this.metabolicNetwork);

        return connectedComponentsService.getConnectedComponents();
    }

    public NetworkBoundaryDTO findNetworkBoundary() {
        NetworkBoundaryService networkBoundaryService = new NetworkBoundaryService(metabolicNetwork.getSinks(), metabolicNetwork.getSources());

        return networkBoundaryService.getNetworkBoundary();
    }

    public MetaboliteReactionsDTO getMetaboliteReactions(String metaboliteId) throws MetaboliteDoesNotExistsException{
        Place<Metabolite> metabolitePlace = metabolicNetwork.getPlace(metaboliteId);
        MetaboliteReactionsService service = new MetaboliteReactionsService(metabolicNetwork, metabolitePlace.getObject());

        return service.getMetaboliteReactions();
    }

    public MetabolicNetwork getNetwork() {
        return this.metabolicNetwork;
    }

    public Map<String, List<Metabolite>> getMetabolitesByCompartment(String compartment) {
        return metabolicNetwork.getReactionsByCompartments();
    }

    public ShortestPathsDTO getShortestPaths(String metaboliteId) throws MetaboliteDoesNotExistsException {
        Place<Metabolite> origin = metabolicNetwork.getPlace(metaboliteId);
        ShortestPathByTransitionNumberService service = new ShortestPathByTransitionNumberService(metabolicNetwork, origin);

        return service.getShortestPath();
    }

    public PathsDTO getAllPaths(FindAllPathsParams params) throws SourceAndTargetPlacesAreEqualException, MetaboliteDoesNotExistsException {
        AllPathsService service = new AllPathsService(metabolicNetwork, params);

        return service.getAllPaths();
    }

    public GeneProductReactionsDTO getGeneProductReactions(String geneID) throws GeneProductDoesNotExitsException {
        GeneProductReactionsService geneProductReactionsService = new GeneProductReactionsService(metabolicNetwork);

        return geneProductReactionsService.getGeneProductReactions(geneID);
    }

    public MetabolicNetwork intercept(MetabolicNetwork targetMetabolicNetwork) {
        InterceptMetabolicNetworksService metabolicNetworksService = new InterceptMetabolicNetworksService();

        return metabolicNetworksService.interception(this.metabolicNetwork, targetMetabolicNetwork);
    }
}
