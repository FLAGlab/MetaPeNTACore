package metapenta.services;

import metapenta.model.dto.ConnectedComponentsDTO;
import metapenta.model.dto.GeneProductReactionsDTO;
import metapenta.model.dto.MetaboliteReactionsDTO;
import metapenta.model.dto.PathsDTO;
import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.errors.MetaboliteDoesNotExistsException;
import metapenta.model.errors.SourceAndTargetPlacesAreEqualException;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.params.FindAllPathsParams;

import java.util.List;
import java.util.Map;

public interface IMetabolicNetworkService {
    ConnectedComponentsDTO connectedComponents();
    PathsDTO getAllPaths(FindAllPathsParams params) throws SourceAndTargetPlacesAreEqualException, MetaboliteDoesNotExistsException;
    MetabolicNetwork intercept(MetabolicNetwork targetMetabolicNetwork);

    GeneProductReactionsDTO getGeneProductReactions(String geneID) throws GeneProductDoesNotExitsException;
    MetaboliteReactionsDTO getMetaboliteReactions(String metaboliteId) throws MetaboliteDoesNotExistsException;

    MetabolicNetwork getNetwork();

    Map<String, List<Metabolite>> getMetabolitesByCompartment(String compartment);
}
