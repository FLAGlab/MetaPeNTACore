package metapenta.tools.io.utils.kegg;

import metapenta.model.dto.KEGGGetMap;

import java.util.*;

public class KEGGResponseParser {

    private static final String GET_END_OF_RESPONSE = "///";
    private static final String EMPTY_STRING = "";

    public Map<String, List<String>> parseGET(String body) {
       String[] lines = body.split("\n");

       return processGetLines(lines);
    }

    public List<String> parseLINKResponse(String body) {
        String[] lines = body.split("\n");

        return processLinkBodyLines(lines);
    }

    private Map<String, List<String>> processGetLines(String[] lines ) {
        Map<String, List<String>> bodyMap = new TreeMap<>();
        String lastKey = "";

        for (String line : lines) {
            if (line.equals(GET_END_OF_RESPONSE)) {
                break;
            } else {
                KEGGGetMap keggGetMap = createKEGGGetMap(line, lastKey);
                bodyMap.computeIfAbsent(keggGetMap.getKey(), k -> new ArrayList<>()).addAll(keggGetMap.getValue());

                lastKey = keggGetMap.getKey();
            }
        }
        return bodyMap;
    }

    private KEGGGetMap createKEGGGetMap(String line, String lastKey) {
        String[] keyValues = line.split("\s+");
        KEGGGetMap keggGetMap = new KEGGGetMap(lastKey, createValueArray(line.trim()));

        if (!keyValues[0].equals(EMPTY_STRING)) {
            keggGetMap.setKey(keyValues[0]);
            String[] value = line.split(keyValues[0]);

            keggGetMap.setValue(createValueArray(value[1].trim()));
        }

        return keggGetMap;
    }

    private List<String> createValueArray(String value) {
        List<String> values = new ArrayList<>();
        values.add(value.trim());

        return values;
    }

    private List<String> processLinkBodyLines(String[] lines) {
        List<String> links = new ArrayList<>();

        for (String line : lines) {
            String[] linkParts = line.split("\t");
            links.add(linkParts[1]);
        }

        return links;
    }

}
