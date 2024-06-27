package metapenta.services;

import metapenta.io.jsonWriters.GeneProductReactionsWriter;
import metapenta.model.GeneProduct;
import metapenta.model.Reaction;
import metapenta.services.dto.GeneProductReactionsDTO;
import metapenta.model.MetabolicNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneProductReactionsService {

    private MetabolicNetwork metabolicNetwork;
    private GeneProduct geneProduct;

    public MetabolicNetwork getMetabolicNetwork() {
		return metabolicNetwork;
	}


	public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
		this.metabolicNetwork = metabolicNetwork;
	}
	public GeneProduct getGeneProduct() {
		return geneProduct;
	}
	public void setGeneProduct(GeneProduct geneProduct) {
		this.geneProduct = geneProduct;
	}
	public void setGeneProduct(String value) throws IOException {
		this.geneProduct = metabolicNetwork.getGeneProduct(value);
		if(geneProduct==null) throw new IOException("A gene product with id: "+value+" was not found in the network");
	}


	public GeneProductReactionsDTO getGeneProductReactions() {
		if(geneProduct==null) throw new RuntimeException("The gene product must be initialized");
        List<Reaction> reactions = getReactionsCatalyzedBy(geneProduct);

        return new GeneProductReactionsDTO(reactions, geneProduct);
    }

    private List<Reaction> getReactionsCatalyzedBy(GeneProduct geneProduct){
        List<Reaction> catalyzedReactions = new ArrayList<>();
        List<Reaction> reactions = metabolicNetwork.getReactionsAsList();

        for (Reaction reaction : reactions) {
            List<GeneProduct> enzymes= reaction.getEnzymes();

            for (GeneProduct enzyme : enzymes) {
                if(enzyme==geneProduct || enzyme.getId().equals(geneProduct.getId())) {
                    catalyzedReactions.add(reaction);
                    System.out.println("Found gene product in reaction: "+reaction);
                    break;
                }
            }
        }

        return catalyzedReactions;
    }
	public static void main(String[] args) throws Exception  {
		GeneProductReactionsService instance = new GeneProductReactionsService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
        instance.setGeneProduct(args[1]);

		GeneProductReactionsDTO geneProductReactions = instance.getGeneProductReactions();
		GeneProductReactionsWriter geneProductReactionsWriter = new GeneProductReactionsWriter(geneProductReactions, args[2]);
		geneProductReactionsWriter.write();

	}
}
