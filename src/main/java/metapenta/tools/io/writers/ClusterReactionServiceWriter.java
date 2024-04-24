package metapenta.tools.io.writers;

import metapenta.commands.OrthogroupDTO;
import metapenta.tools.io.utils.MetabolicNetworkJSONUtils;
import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EntityList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClusterReactionServiceWriter {

    List<EntityList> enzymeReaction = new ArrayList<>();
    OrthogroupDTO orthogroupDTO;

    private String prefix;

    private static final String REACTION_BY_ENZYME_FILE = "_enzyme_reactions.txt";

    private static final String REACTION_BY_CLUSTER = "_cluster_reactions.txt";

    public ClusterReactionServiceWriter(String prefix) {
        this.prefix = prefix;
    }

    public void setAndWriteEnzymeReaction(List<EntityList> enzymeReaction){
        setEnzymeReaction(enzymeReaction);
        writeEnzymeReactionObject();
    }

    public void setEnzymeReaction(List<EntityList> enzymeReaction) {
        this.enzymeReaction = enzymeReaction;

    }

    public void setAndWriteClusterReactions(OrthogroupDTO orthogroups){
        setOrthogroup(orthogroups);
        writeClusterReactions();
    }

    private void setOrthogroup(OrthogroupDTO orthogroups) {
        this.orthogroupDTO = orthogroups;
    }

    private void writeClusterReactions() {
        JSONObject object = clusterReactions();
        write(object, this.prefix + REACTION_BY_CLUSTER);
    }

    private JSONObject clusterReactions() {
        JSONObject clusterReactionsObject = new JSONObject();
        for(Integer group: orthogroupDTO.clusteReactions().keySet()){
            JSONArray clusterReactions = MetabolicNetworkJSONUtils.getCollectionsJsonArray(orthogroupDTO.reactions(group));
            clusterReactionsObject.put(group, clusterReactions);
        }

        return clusterReactionsObject;
    }

    private void write(JSONObject object, String path) {
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

    private void writeEnzymeReactionObject() {
        JSONObject clusterObject = enzymeReactionObjects();
        write(clusterObject, this.prefix + REACTION_BY_ENZYME_FILE);
    }

    private JSONObject enzymeReactionObjects() {
        JSONObject clusterReactionsObject = new JSONObject();
        for(EntityList enzyme: enzymeReaction) {
            JSONArray enzymeReactions = MetabolicNetworkJSONUtils.getCollectionsJsonArray(enzyme.getList());
            if(!enzymeReactions.isEmpty()) {
                clusterReactionsObject.put(enzyme.ID(), enzymeReactions);
            }
        }
        return clusterReactionsObject;
    }
}
