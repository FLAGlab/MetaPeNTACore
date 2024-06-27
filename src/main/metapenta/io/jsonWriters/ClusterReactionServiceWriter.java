package metapenta.io.jsonWriters;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import metapenta.io.MetabolicNetworkJSONUtils;
import metapenta.io.MetabolicNetworkXMLWriter;
import metapenta.kegg.EntityList;
import metapenta.model.MetabolicNetwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ClusterReactionServiceWriter {

    private String prefix;

    private static final String GEN_ENZYMES_FILES = "_gen_enzymes.txt";

    private static final String CLUSTER_ENZYMES = "_cluster_enzymes.txt";

    private static final String ENZYME_REACTIONS = "_enzyme_reactions.txt";

    private static final String REACTIONS = "_reactions.txt";

    private static final String METABOLITES = "_metabolites.txt";

    private static final String ENZYMES = "_enzymes.txt";

    private static final String METABOLIC_NETWORK = "_metabolic_newtwork.xml";

    public ClusterReactionServiceWriter(String prefix) {
        this.prefix = prefix;
    }

    public void writeReactions(List entities) {
        writeList(entities, this.prefix + REACTIONS);
    }

    public void writeMetabolites(List metabolites){
        writeList(metabolites, this.prefix + METABOLITES);
    }

    public void writeGeneProduct(List geneProducts){
        writeList(geneProducts, this.prefix + ENZYMES);
    }

    private void writeList(List entities, String path){
    	//TODO: Build JSON aware objects
        JSONArray enzymeReactions = MetabolicNetworkJSONUtils.getCollectionsJsonArray(entities);
        write(enzymeReactions,  path);
    }

    public void writeGenEnzymes(List<EntityList> entities) {
        JSONObject clusterObject = enzymeReactionObjects(entities);
        write(clusterObject, this.prefix + GEN_ENZYMES_FILES);
    }

    public void writeEnzymeReactions(List<EntityList> entities){
        JSONObject clusterObject = enzymeReactionObjects(entities);
        write(clusterObject, this.prefix + ENZYME_REACTIONS);
    }


    private JSONObject enzymeReactionObjects(List<EntityList> entities) {
        JSONObject clusterReactionsObject = new JSONObject();
        for(EntityList enzyme: entities) {
            JSONArray enzymeReactions = MetabolicNetworkJSONUtils.getCollectionsJsonArray(enzyme.getList());
            if(!enzymeReactions.isEmpty()) {
                clusterReactionsObject.put(enzyme.ID(), enzymeReactions);
            }
        }
        return clusterReactionsObject;
    }

    public void writeMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
        MetabolicNetworkXMLWriter writer = new MetabolicNetworkXMLWriter();
        String path = this.prefix + METABOLIC_NETWORK;
        try {
            writer.saveNetwork(metabolicNetwork, path);
        } catch (IOException e) {
            try {
                writer.saveNetwork(metabolicNetwork, "./" + METABOLIC_NETWORK);
            }
            catch (Exception exception){
                throw new RuntimeException(exception);
            }
        }
    }

    private void write(JSONAware object, String path) {
        try {
            Files.write(Paths.get(path), object.toJSONString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error writing cluster reactions file in the param route. Writing in the current directory.");
            try {
                Files.write(Paths.get("./" + Math.random() + ".txt"), object.toJSONString().getBytes());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
