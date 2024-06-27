package metapenta.dto;

import metapenta.model.GeneProduct;
import metapenta.model.Reaction;

import java.util.List;

public class GeneProductReactionsDTO {
    private List<Reaction> reactions;

    private GeneProduct geneProduct;

    public GeneProductReactionsDTO(List<Reaction> reactions, GeneProduct geneProduct) {
        this.reactions = reactions;
        this.geneProduct = geneProduct;
    }

    public GeneProduct getGeneProduct() {
        return geneProduct;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }
}
