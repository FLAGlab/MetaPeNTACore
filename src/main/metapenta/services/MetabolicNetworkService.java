package metapenta.services;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.model.MetabolicNetwork;
import metapenta.services.dto.ConnectedComponentsDTO;
import metapenta.services.dto.FindGapsDTO;
import metapenta.services.dto.GeneProductReactionsDTO;
import metapenta.services.dto.MetaboliteReactionsDTO;
import metapenta.services.dto.NetworkBoundaryDTO;
import metapenta.services.dto.PathsDTO;
import metapenta.services.dto.ShortestPathsDTO;

public class MetabolicNetworkService {
    private final MetabolicNetwork metabolicNetwork;

    public MetabolicNetworkService (String networkFile) throws Exception{
        metabolicNetwork = MetabolicNetwork.load(networkFile);
    }

    public MetabolicNetworkService (InputStream is) throws Exception{
        MetabolicNetworkXMLLoader networkLoader = new MetabolicNetworkXMLLoader();
        metabolicNetwork = networkLoader.loadNetwork(is);
    }
    
    public MetabolicNetwork getNetwork() {
        return this.metabolicNetwork;
    }

    public ConnectedComponentsDTO getConnectedComponents () {
        ConnectedComponentsService connectedComponentsService = new ConnectedComponentsService();
        connectedComponentsService.setMetabolicNetwork(metabolicNetwork);
        return connectedComponentsService.getConnectedComponents();
    }

    public NetworkBoundaryDTO findNetworkBoundary () {
        NetworkBoundaryService networkBoundaryService = new NetworkBoundaryService();
        networkBoundaryService.setMetabolicNetwork(metabolicNetwork);
        return networkBoundaryService.getNetworkBoundary();
    }

    public MetaboliteReactionsDTO getMetaboliteReactions (String metaboliteId) throws IOException {
        MetaboliteReactionsService service = new MetaboliteReactionsService();
        service.setMetabolicNetwork(metabolicNetwork);
        service.setMetabolite(metaboliteId);

        return service.getMetaboliteReactions();
    }

    
    public ShortestPathsDTO getShortestPaths(String metaboliteId) {
        ShortestPathService service = new ShortestPathService();
        service.setMetabolicNetwork(metabolicNetwork);
        service.setMetaboliteId(metaboliteId);
        return service.getShortestPath();
    }

    public PathsDTO getAllPaths(List<String> metaboliteIds, String targetId) {
        AllPathsService service = new AllPathsService();
        service.setMetabolicNetwork(metabolicNetwork);
        service.setInitialMetaboliteIds(metaboliteIds);
        service.setTargetId(targetId);
        return service.getAllPaths();
    }

    public GeneProductReactionsDTO getGeneProductReactions(String geneID) throws IOException {
        GeneProductReactionsService geneProductReactionsService = new GeneProductReactionsService();
        geneProductReactionsService.setMetabolicNetwork(metabolicNetwork);
        geneProductReactionsService.setGeneProduct(geneID);
        return geneProductReactionsService.getGeneProductReactions();
    }

    public MetabolicNetwork intersect(MetabolicNetwork targetMetabolicNetwork) {
        IntersectMetabolicNetworksService metabolicNetworksService = new IntersectMetabolicNetworksService();

        return metabolicNetworksService.intersect(this.metabolicNetwork, targetMetabolicNetwork);
    }

    public FindGapsDTO findGaps() {
        FindGapsService findGapsService = new FindGapsService();
        findGapsService.setMetabolicNetwork(metabolicNetwork);
        return findGapsService.getRootGaps();
    }
}
