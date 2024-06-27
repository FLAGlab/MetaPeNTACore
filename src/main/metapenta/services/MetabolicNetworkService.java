package metapenta.services;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

import metapenta.dto.ConnectedComponentsDTO;
import metapenta.dto.FindGapsDTO;
import metapenta.dto.GeneProductReactionsDTO;
import metapenta.dto.MetaboliteReactionsDTO;
import metapenta.dto.NetworkBoundaryDTO;
import metapenta.dto.PathsDTO;
import metapenta.dto.ShortestPathsDTO;
import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.petrinet.PetriNetElements;
import metapenta.petrinet.Place;

public class MetabolicNetworkService {
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
    	PetriNetElements petriNet = new PetriNetElements(metabolicNetwork);
        ConnectedComponentsService connectedComponentsService = new ConnectedComponentsService(petriNet);

        return connectedComponentsService.getConnectedComponents();
    }

    public NetworkBoundaryDTO findNetworkBoundary() {
        NetworkBoundaryService networkBoundaryService = new NetworkBoundaryService(metabolicNetwork.inferExchangeReactions());

        return networkBoundaryService.getNetworkBoundary();
    }

    public MetaboliteReactionsDTO getMetaboliteReactions(String metaboliteId) {
        Metabolite metabolite = metabolicNetwork.getMetabolite(metaboliteId);
        MetaboliteReactionsService service = new MetaboliteReactionsService(metabolicNetwork, metabolite);

        return service.getMetaboliteReactions();
    }

    public MetabolicNetwork getNetwork() {
        return this.metabolicNetwork;
    }

    public Map<String, List<Metabolite>> getMetabolitesByCompartment(String compartment) {
        return metabolicNetwork.getMetabolitesByCompartments();
    }

    public ShortestPathsDTO getShortestPaths(String metaboliteId) {
    	PetriNetElements petriNet = new PetriNetElements(metabolicNetwork);
        Place<Metabolite> origin = petriNet.getPlace(metaboliteId);
        ShortestPathByTransitionNumberService service = new ShortestPathByTransitionNumberService(petriNet, origin);

        return service.getShortestPath();
    }

    public PathsDTO getAllPaths(FindAllPathsParams params) {
    	PetriNetElements petriNet = new PetriNetElements(metabolicNetwork);
        AllPathsService service = new AllPathsService(petriNet, params);
        return service.getAllPaths();
    }

    public GeneProductReactionsDTO getGeneProductReactions(String geneID) {
        GeneProductReactionsService geneProductReactionsService = new GeneProductReactionsService(metabolicNetwork);

        return geneProductReactionsService.getGeneProductReactions(geneID);
    }

    public MetabolicNetwork intercept(MetabolicNetwork targetMetabolicNetwork) {
        InterceptMetabolicNetworksService metabolicNetworksService = new InterceptMetabolicNetworksService();

        return metabolicNetworksService.interception(this.metabolicNetwork, targetMetabolicNetwork);
    }

    public FindGapsDTO findGaps() {
        FindGapsService findGapsService = new FindGapsService(metabolicNetwork.getRootNoProductionGaps(), metabolicNetwork.getRootNoConsumptionGaps());
        return findGapsService.getRootGaps();
    }
}
