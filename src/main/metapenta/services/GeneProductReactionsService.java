package metapenta.services;

import metapenta.dto.GeneProductReactionsDTO;
import metapenta.model.GeneProduct;
import metapenta.model.Reaction;
import metapenta.model.MetabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class GeneProductReactionsService {

    private MetabolicNetwork metabolicNetwork;
    public GeneProductReactionsService(MetabolicNetwork metabolicNetwork) {
        this.metabolicNetwork = metabolicNetwork;
    }

    public GeneProductReactionsDTO getGeneProductReactions(String geneProductId) {
        GeneProduct geneProduct = metabolicNetwork.getGeneProduct(geneProductId);
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
