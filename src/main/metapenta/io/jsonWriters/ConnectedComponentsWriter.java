package metapenta.io.jsonWriters;

import metapenta.model.Metabolite;
import metapenta.services.dto.ConnectedComponentsDTO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectedComponentsWriter implements Writer {
    private ConnectedComponentsDTO connectedComponents;

    private String filename;

    public ConnectedComponentsWriter(ConnectedComponentsDTO connectedComponents, String filename) {
        this.connectedComponents = connectedComponents;
        this.filename = filename;
    }
    
    @Override
	public void write() throws IOException {
		writeJson();
	}

	public void writeJson() throws IOException {
        JSONObject connectedComponentsJson = new JSONObject();
        Map<Integer, List<Metabolite>>  metabolitesConnected = connectedComponents.getConnectedMetabolites();
        System.out.println("Number of connected components: "+metabolitesConnected.size());
        Set<Integer> keySet = metabolitesConnected.keySet();
        for (Integer key: keySet) {
        	List<Metabolite> group = metabolitesConnected.get(key);
            JSONArray metabolitesJson = createMetaboliteJsonArray(group);
            System.out.println("Next component Size: "+group.size());
            if(group.size()<10) {
            	for(Metabolite m:group) System.out.println("Next metabolite: "+m.getId()+" "+m.getName()+" "+m.getChemicalFormula().getChemicalFormula());
            }
            connectedComponentsJson.put(key, metabolitesJson);
        }

        Files.write(Paths.get(filename), connectedComponentsJson.toJSONString().getBytes());
    }

    private JSONArray createMetaboliteJsonArray(List<Metabolite> metabolites) {
        JSONArray metabolitesJson = new JSONArray();
        for (Metabolite m: metabolites) {
            metabolitesJson.add(m);
        }

        return metabolitesJson;
    }



	
}
