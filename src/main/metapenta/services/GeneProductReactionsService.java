package metapenta.services;

import metapenta.io.jsonWriters.GeneProductReactionsWriter;
import metapenta.model.GeneProduct;
import metapenta.model.Reaction;
import metapenta.services.dto.GeneProductReactionsDTO;
import metapenta.model.MetabolicNetwork;

import java.util.ArrayList;
import java.util.List;

public class GeneProductReactionsService {

    private MetabolicNetwork metabolicNetwork;

    public MetabolicNetwork getMetabolicNetwork() {
		return metabolicNetwork;
	}


	public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
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
	public static void main(String[] args) throws Exception  {
		GeneProductReactionsService instance = new GeneProductReactionsService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));

		GeneProductReactionsDTO geneProductReactions = instance.getGeneProductReactions(args[1]);
		GeneProductReactionsWriter geneProductReactionsWriter = new GeneProductReactionsWriter(geneProductReactions, args[2]);
		geneProductReactionsWriter.write();

	}
}
