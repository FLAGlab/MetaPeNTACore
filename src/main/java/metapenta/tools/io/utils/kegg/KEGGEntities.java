package metapenta.tools.io.utils.kegg;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EntityList;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EnzymeReactionsCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.GenEnzymesCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.enzymes.EnzymeKEGGAPICreator;
import metapenta.tools.io.utils.kegg.entitiescreator.metabolite.MetaboliteKEGGAPICreator;
import metapenta.tools.io.utils.kegg.entitiescreator.reaction.ReactionKEGGAPICreator;

import java.util.Collection;
import java.util.Map;

public class KEGGEntities {

    private CompletionStagesFactory<Reaction> reactionCompletionStagesFactory;
    private CompletionStagesFactory<Metabolite> metaboliteCompletionStagesFactory;

    private CompletionStagesFactory<GeneProduct> geneProductCompletionStagesFactory;
    private CompletionStagesFactory<EntityList> enzymeIDsListCompletionStagesFactory;

    private CompletionStagesFactory<EntityList> reactionIDsFactory;

    public static final String NAME = "NAME";

    public static final String COMPOUND_FORMULA = "FORMULA";

    private KEGGResponseParser parser = new KEGGResponseParser();

    public KEGGEntities() {
        initReactionFactory();
        initMetaboliteFactory();
        initGeneProductFactory();
        initReactionIDsListFactory();
        initReactionIDsFactory();
    }

    private void initReactionIDsFactory() {
        CompletionStateParams<EntityList> params = createReactionIDsFactoryParams();
        this.reactionIDsFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<EntityList> createReactionIDsFactoryParams() {
        CompletionStateParams<EntityList> params = new CompletionStateParams<>();
        params.setCreator(new EnzymeReactionsCreator());

        return params;
    }

    private void initReactionFactory() {
        CompletionStateParams<Reaction> params = createReactionFactoryParams();
        this.reactionCompletionStagesFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<Reaction> createReactionFactoryParams() {
        CompletionStateParams<Reaction> params = new CompletionStateParams();
        params.setCreator(new ReactionKEGGAPICreator());

        return params;
    }

    private void initMetaboliteFactory() {
        CompletionStateParams<Metabolite> params = createMetaboliteFactoryParams();
        this.metaboliteCompletionStagesFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<Metabolite> createMetaboliteFactoryParams() {
       CompletionStateParams<Metabolite> params = new CompletionStateParams<>();
       params.setCreator(new MetaboliteKEGGAPICreator());

       return params;
    }


    private void initGeneProductFactory() {
        CompletionStateParams<GeneProduct> params = createGeneProductFactoryParams();
        this.geneProductCompletionStagesFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<GeneProduct> createGeneProductFactoryParams() {
        CompletionStateParams<GeneProduct> params = new CompletionStateParams<>();
        params.setCreator(new EnzymeKEGGAPICreator());

        return params;
    }

    private void initReactionIDsListFactory() {
        CompletionStateParams<EntityList> params = createReactionIDsListFactoryParams();
        this.enzymeIDsListCompletionStagesFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<EntityList> createReactionIDsListFactoryParams() {
        CompletionStateParams<EntityList> params = new CompletionStateParams<>();
        params.setCreator(new GenEnzymesCreator());

        return params;
    }

    public Map<String, EntityList> getEnzymesFromGeneIDs(Collection<String> enzymeIDs) {
       enzymeIDsListCompletionStagesFactory.setIds(enzymeIDs);

       return enzymeIDsListCompletionStagesFactory.createEntities();
    }

    public Map<String, EntityList> getReactionsFromGeneIDs(Collection<String> geneIDs) {
        reactionIDsFactory.setIds(geneIDs);

        return reactionIDsFactory.createEntities();
    }
}
