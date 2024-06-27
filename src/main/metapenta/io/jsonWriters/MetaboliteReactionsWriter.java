package metapenta.io.jsonWriters;

import metapenta.model.Reaction;
import metapenta.services.dto.MetaboliteReactionsDTO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MetaboliteReactionsWriter implements Writer {

    private static final String IS_SUBSTRATE = "is_substrate";

    private static final String IS_PRODUCT = "is_product";

    private MetaboliteReactionsDTO metaboliteReactions;

    private String fileName;

    public MetaboliteReactionsWriter(MetaboliteReactionsDTO metaboliteReactions, String fileName) {
        this.metaboliteReactions = metaboliteReactions;
        this.fileName = fileName;
    }

    @Override
    public void write() throws IOException {
        JSONObject jsonObject = getJsonObject();

        Files.write(Paths.get(this.fileName), jsonObject.toJSONString().getBytes());
    }


    private JSONObject getJsonObject() {
        JSONObject object = new JSONObject();
        List<Reaction> products = getReactionsByCriteria(IS_PRODUCT);
        List<Reaction> substrate = getReactionsByCriteria(IS_SUBSTRATE);

        object.put(IS_PRODUCT, products);
        object.put(IS_SUBSTRATE, substrate);

        return object;
    }

    private JSONArray getReactionJsonByCriteria(String criteria) {
        JSONArray reactionsArray = new JSONArray();
        List<Reaction> reactions = getReactionsByCriteria(criteria);

        for(Reaction reaction: reactions) {
            reactionsArray.add(reaction);
        }

        return reactionsArray;
    }

    private List<Reaction> getReactionsByCriteria(String criteria) {
        switch (criteria){
            case IS_PRODUCT -> {
                return metaboliteReactions.getReactionsIsProduct();
            }
            case IS_SUBSTRATE -> {
                return metaboliteReactions.getReactionsIsSubstrate();
            }
        }

        return new ArrayList();
    }


}
