package metapenta.services;

import metapenta.commands.OrthogroupDTO;
import metapenta.tools.io.loaders.ClusterReactionsFileLoader;
import metapenta.tools.io.utils.kegg.KEGGEntities;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EntityList;
import metapenta.tools.io.writers.ClusterReactionServiceWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterReactionsService {
    OrthogroupDTO orthogroups;
    ClusterReactionsFileLoader clusterReactionsFileLoader = new ClusterReactionsFileLoader();

    KEGGEntities keggEntities = new KEGGEntities();
    String NGSEP_file;
    ClusterReactionServiceWriter writer;

    List<EntityList> genEnzymes = new ArrayList<>();


    public ClusterReactionsService(String NGSEP_file, String prefix) throws FileNotFoundException {
        this.NGSEP_file = NGSEP_file;
        this.orthogroups = clusterReactionsFileLoader.load(NGSEP_file);
        this.writer = new ClusterReactionServiceWriter(prefix);
    }


    private void getGenesEnzymes() {
        Set<String> enzymes = orthogroups.getAllGenesIDs();
        Map<String, EntityList> enzymeList = keggEntities.getEnzymesFromGeneIDs(enzymes);

        setGenEnzymeListAndLog(List.copyOf(enzymeList.values()));
    }

    private void getEnzymeReactions() {

    }


    public void generateNetwork() {
        getGenesEnzymes();

    }

    private void setGenEnzymeListAndLog(List<EntityList> enzymeReactionLists) {
        this.genEnzymes = enzymeReactionLists;
        this.orthogroups.calculateEnzymeClusters(this.genEnzymes);
        System.out.printf("Found %s reactions, processing%n", enzymeReactionLists.size());

        writer.setAndWriteEnzymeReaction(enzymeReactionLists);
        writer.setAndWriteClusterReactions(this.orthogroups);

    }


    public static void main(String[] args) throws Exception {
        String prefix = "/home/jose/Documents/Valerie/Repositories/FLAG/MetaPeNTACore/out-examples/cluster-reactions/cluster_reaction";
        ClusterReactionsService service = new ClusterReactionsService("data/NGSEP_Cluster_notations_reduced.txt", prefix);

        service.generateNetwork();
    }

}
