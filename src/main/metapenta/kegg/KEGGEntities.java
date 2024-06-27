package metapenta.kegg;

import metapenta.model.GeneProduct;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;

import java.util.Collection;

public class KEGGEntities {

    private CompletionStagesFactory<Reaction> reactionFactory;
    private CompletionStagesFactory<Metabolite> metabolitesFactory;

    private CompletionStagesFactory<GeneProduct> geneProductFactory;
    private CompletionStagesFactory<EntityList> enzymeIDsListCompletionStagesFactory;

    private CompletionStagesFactory<EntityList> reactionIDsFactory;

    public static final String ENTRY = "ENTRY";
    public static final String NAME = "NAME";

    public static final String COMPOUND_FORMULA = "FORMULA";

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
        this.reactionFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<Reaction> createReactionFactoryParams() {
        CompletionStateParams<Reaction> params = new CompletionStateParams();
        params.setCreator(new ReactionKEGGAPICreator());

        return params;
    }

    private void initMetaboliteFactory() {
        CompletionStateParams<Metabolite> params = createMetaboliteFactoryParams();
        this.metabolitesFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<Metabolite> createMetaboliteFactoryParams() {
       CompletionStateParams<Metabolite> params = new CompletionStateParams<>();
       params.setCreator(new MetaboliteKEGGAPICreator());

       return params;
    }


    private void initGeneProductFactory() {
        CompletionStateParams<GeneProduct> params = createGeneProductFactoryParams();
        this.geneProductFactory = new CompletionStagesFactory<>(params);
    }

    private CompletionStateParams<GeneProduct> createGeneProductFactoryParams() {
        CompletionStateParams<GeneProduct> params = new CompletionStateParams<>();
        params.setCreator(new GeneProductKEGGAPICreator());

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

    public Collection <EntityList> getEnzymesFromGeneIDs(Collection<String> enzymeIDs) {
       enzymeIDsListCompletionStagesFactory.setIds(enzymeIDs);

       return enzymeIDsListCompletionStagesFactory.createEntities();
    }

    public Collection<EntityList> getReactionsFromEnzymes(Collection<String> geneIDs) {
        reactionIDsFactory.setIds(geneIDs);

        return reactionIDsFactory.createEntities();
    }

    public Collection<Reaction> getReactionsFromIDs(Collection<String> reactionIDs) {
        this.reactionFactory.setIds(reactionIDs);

        return this.reactionFactory.createEntities();
    }

    public Collection<Metabolite> getMetabolitesFromIDs(Collection<String> metabolitesIDs) {
        this.metabolitesFactory.setIds(metabolitesIDs);

        return this.metabolitesFactory.createEntities();
    }

    public Collection<GeneProduct> getGeneProductsFromIDs(Collection<String> geneProductIDs) {
        this.geneProductFactory.setIds(geneProductIDs);

        return this.geneProductFactory.createEntities();
    }
}
