package metapenta.services;

import metapenta.model.dto.GeneProductReactionsDTO;
import metapenta.model.errors.GeneProductDoesNotExitsException;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.networks.MetabolicNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneProductReactionsService {

    private MetabolicNetwork metabolicNetwork;
    public GeneProductReactionsService(MetabolicNetwork metabolicNetwork) {
        this.metabolicNetwork = metabolicNetwork;
    }

    public GeneProductReactionsDTO getGeneProductReactions(String geneProductId) throws GeneProductDoesNotExitsException {
        GeneProduct geneProduct = metabolicNetwork.getGeneProduct(geneProductId);
        if (geneProduct == null) {
            throw new GeneProductDoesNotExitsException(geneProductId);
        }

        List<Reaction> reactions = getReactionsCatalyzedBy(geneProductId);

        return new GeneProductReactionsDTO(reactions, geneProduct);
    }

    private List<Reaction> getReactionsCatalyzedBy(String geneProductName){
        List<Reaction> catalyzedReactions = new ArrayList<>();
        List<Reaction> reactions = metabolicNetwork.getReactionsAsList();

        for (Reaction reaction : reactions) {
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
}
