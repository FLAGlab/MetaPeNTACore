package metapenta.services;

import metapenta.commands.OrthogroupDTO;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.tools.io.loaders.ClusterReactionsFileLoader;
import metapenta.tools.io.utils.kegg.KEGGEntities;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EntityList;
import metapenta.tools.io.writers.ClusterReactionServiceWriter;

import java.io.*;
import java.util.*;

import static metapenta.tools.io.utils.kegg.entitiescreator.reaction.ReactionKEGGAPICreator.COBRA_DEFAULT_LOWER_BOUND;
import static metapenta.tools.io.utils.kegg.entitiescreator.reaction.ReactionKEGGAPICreator.COBRA_DEFAULT_UPPER_BOUND;

public class ClusterReactionsService {
    OrthogroupDTO orthogroups;
    ClusterReactionsFileLoader clusterReactionsFileLoader = new ClusterReactionsFileLoader();

    KEGGEntities keggEntities = new KEGGEntities();
    String NGSEP_file;
    ClusterReactionServiceWriter writer;

    List<EntityList> genEnzymes = new ArrayList<>();

    List<EntityList> enzymeReactions = new ArrayList<>();

    List<Reaction> reactions = new ArrayList<>();

    List<Metabolite> metabolites = new ArrayList<>();

    List<GeneProduct> enzymes = new ArrayList<>();

    public ClusterReactionsService(String NGSEP_file, String prefix) throws IOException {
        this.NGSEP_file = NGSEP_file;
        this.orthogroups = clusterReactionsFileLoader.load(NGSEP_file);
        this.writer = new ClusterReactionServiceWriter(prefix);
    }

    public void generateNetwork() {
        fetchGenesEnzymes();
        fetchEnzymeReactionsIDs();
        fetchBaseReactionData();

        fetchMetabolites();
        enrichReactionWithMetabolitesInformation();

        fetchEnzymes();
        enrichReactionsWithEnzymes();

        createAndWriteNetwork();
    }

    private void fetchGenesEnzymes() {
        Set<String> enzymes = orthogroups.getAllGenesIDs();
        System.out.printf("Found %s enzymes, processing \n", enzymes.size());
        Collection<EntityList> enzymeList = keggEntities.getEnzymesFromGeneIDs(enzymes);

        setGenEnzymesAndWriteIt(List.copyOf(enzymeList));
        writer.writeGeneProduct(new ArrayList<>(enzymeList));
    }

    private void setGenEnzymesAndWriteIt(List<EntityList> enzymeReactionLists) {
        this.genEnzymes = enzymeReactionLists;
        this.orthogroups.calculateEnzymeClusters(this.genEnzymes);
        System.out.printf("Found %s enzymes, processing \n", enzymeReactionLists.size());

        writer.writeGenEnzymes(enzymeReactionLists);
    }

    private void fetchEnzymeReactionsIDs() {
        Set<String> enzymesIDs = calculateAllEnzymesIDs();
        Collection<EntityList> enzymeReactions = keggEntities.getReactionsFromEnzymes(enzymesIDs);

        setEnzymeReactionsAndWriteIt(List.copyOf(enzymeReactions));
    }

    private Set<String> calculateAllEnzymesIDs() {
        Set<String> enzymesIDs = new HashSet<>();
        for(EntityList enzymeReactions: genEnzymes) {
            enzymesIDs.addAll(enzymeReactions.getList());
        }

        return enzymesIDs;
    }

    private void setEnzymeReactionsAndWriteIt(List<EntityList> enzymeReactions) {
        this.enzymeReactions = enzymeReactions;
        System.out.println("Found " + enzymeReactions.size() + " reactions, processing \n");

        writer.writeEnzymeReactions(enzymeReactions);
    }

    private void fetchBaseReactionData() {
        Set<String> reactionsIDs = calculateAllReactionIDs();
        Collection<Reaction> reaction = keggEntities.getReactionsFromIDs(reactionsIDs);

        this.reactions = List.copyOf(reaction);

        writer.writeReactions(new ArrayList(this.reactions));
    }


    private Set<String> calculateAllReactionIDs() {
        Set<String> reactions = new HashSet<>();
        for(EntityList enzymeReactions: enzymeReactions) {
            reactions.addAll(enzymeReactions.getList());
        }

        return reactions;
    }
    private void fetchMetabolites() {
        List<String> metabolitesIDs = calculateAllMetabolitesIDs();
        System.out.printf("Found %s metabolitesIDs processing \n", metabolitesIDs.size());
        Collection<Metabolite> metabolites = keggEntities.getMetabolitesFromIDs(metabolitesIDs);
        this.metabolites = new ArrayList<>(metabolites);

        writer.writeMetabolites(new ArrayList<>(this.metabolites));
    }

    private void enrichReactionWithMetabolitesInformation() {
        for(Reaction reaction: reactions) {
            List<ReactionComponent> reactants = reaction.getReactants();
            enrichReactionComponentList(reactants);

            List<ReactionComponent> products = reaction.getProducts();
            enrichReactionComponentList(products);
        }
    }

    private void enrichReactionComponentList( List<ReactionComponent> reactants){
        for (ReactionComponent reactant: reactants) {
            Optional<Metabolite> metabolite = this.metabolites.stream().filter(metabolite1 -> metabolite1.getId().equals(reactant.getMetaboliteID())).findFirst();
            metabolite.ifPresent(reactant::setMetabolite);
        }
    }

    private void enrichReactionsWithEnzymes(){
        for (Reaction reaction: reactions){
            List<GeneProduct> enrichedEnzymes = new ArrayList<>();
            List<GeneProduct> enzymes = reaction.getEnzymes();
            for(GeneProduct enzyme: enzymes){
                Optional<GeneProduct> geneProduct = this.enzymes.stream().filter(enzyme1 -> enzyme1.ID().equals(enzyme.ID())).findFirst();
                if (geneProduct.isPresent()){
                    enrichedEnzymes.add(geneProduct.get());
                }
            }
            reaction.setEnzymes(enrichedEnzymes);
        }
    }

    private void createAndWriteNetwork() {
        MetabolicNetwork metabolicNetwork = new MetabolicNetwork();
        metabolicNetwork.addReactions(this.reactions);

        metabolicNetwork.addParameter(COBRA_DEFAULT_LOWER_BOUND, "-1000");
        metabolicNetwork.addParameter(COBRA_DEFAULT_UPPER_BOUND, "1000");

        writer.writeMetabolicNetwork(metabolicNetwork);
    }

    private List<String> calculateAllMetabolitesIDs() {
        List<String> metabolitesIDs = new ArrayList<>();
        for(Reaction reaction: reactions) {
           List<ReactionComponent> reactants = reaction.getReactants();
           for (ReactionComponent reactant: reactants) {
               metabolitesIDs.add(reactant.getMetaboliteID());
           }

           List<ReactionComponent> products = reaction.getProducts();
           for (ReactionComponent product: products) {
               metabolitesIDs.add(product.getMetaboliteID());
           }
        }

        return metabolitesIDs;
    }


    private void fetchEnzymes() {
        Set<String> ids = calculateEnzymeIDs();
        Collection<GeneProduct> geneProducts = keggEntities.getGeneProductsFromIDs(ids);

        this.enzymes = new ArrayList<>(geneProducts);

    }

    private Set<String> calculateEnzymeIDs() {
        Set<String> ids = new HashSet();
        for(Reaction reaction: reactions) {
            List<GeneProduct> enzymes = reaction.getEnzymes();
            for (GeneProduct enzyme: enzymes){
                ids.add(enzyme.ID());
            }
        }

        return ids;
    }
}
