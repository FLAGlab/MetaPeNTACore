package metapenta.tools.io.writers;

import metapenta.model.dto.FindGapsDTO;
import metapenta.model.metabolic.network.Metabolite;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FindGapsWriter {
    private static final String ROOT_NO_PRODUCTION_JSON_KEY = "RootNoProduction";
    private static final String ROOT_NO_CONSUMPTION_JSON_KEY = "RootNoConsumption";
    private FindGapsDTO gapsDTO;
    private String outputFile;

    public FindGapsWriter(FindGapsDTO gapsDTO, String outputFile){
        this.gapsDTO = gapsDTO;
        this.outputFile = outputFile;
    }

    private JSONObject getJsonGapsObject() {
        JSONObject gap = new JSONObject();
        gap.put(ROOT_NO_PRODUCTION_JSON_KEY, getRootNoProductionGapsJsonArray());
        gap.put(ROOT_NO_CONSUMPTION_JSON_KEY, getRootNoConsumptionGapsJsonArray());

        return gap;
    }

    private JSONArray getRootNoProductionGapsJsonArray() {
        JSONArray metabolites = new JSONArray();
        for(Metabolite metabolite: gapsDTO.getRootNoProduction()){
            metabolites.add(metabolite);
        }

        return metabolites;
    }

    private JSONArray getRootNoConsumptionGapsJsonArray() {
        JSONArray metabolites = new JSONArray();
        for(Metabolite metabolite: gapsDTO.getRootNoConsumption()){
            metabolites.add(metabolite);
        }

        return metabolites;
    }

    public void write() throws IOException {
        JSONObject jsonObject = getJsonGapsObject();

        Files.write(Paths.get(outputFile), jsonObject.toJSONString().getBytes());
    }
}
