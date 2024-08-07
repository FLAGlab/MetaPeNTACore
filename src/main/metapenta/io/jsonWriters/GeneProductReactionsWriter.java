package metapenta.io.jsonWriters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import metapenta.services.dto.GeneProductReactionsDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeneProductReactionsWriter implements Writer {
    private GeneProductReactionsDTO geneProductReactions;
    private String fileName;

    public GeneProductReactionsWriter(GeneProductReactionsDTO geneProductReactions, String filename) {
        this.geneProductReactions = geneProductReactions;
        this.fileName = filename;
    }

    public void write() throws IOException {
        JSONObject reactionsObject = new JSONObject();

        JSONArray reactions = MetabolicNetworkJSONUtils.getReactionsJsonArray(geneProductReactions.getReactions());
        reactionsObject.put(geneProductReactions.getGeneProduct().getName(), reactions);

        Files.write(Paths.get(this.fileName), reactionsObject.toJSONString().getBytes());
    }
}
