package metapenta.tools.io.utils.kegg;

import java.util.*;

public class KEGGResponseParser {

    private static final String GET_END_OF_RESPONSE = "///";
    private static final String EMPTY_STRING = "";

    public Map<String, List<String>> parseGetResponse(String body) {
       String[] lines = body.split("\n");

       return processGetBodyLines(lines);
    }

    private Map<String, List<String>> processGetBodyLines(String[] lines ) {
        Map<String, List<String>> bodyMap = new TreeMap<>();

        String lastKey = "";

        for (String line : lines) {
            if (!line.equals(GET_END_OF_RESPONSE)) {
                String[] keyValues = line.split("\s+");
                    if (keyValues[0].equals(EMPTY_STRING)) {
                       bodyMap.get(lastKey).add(line.trim());
                    } else {
                        String [] value = line.trim().split(keyValues[0]);

                        List<String> values = new ArrayList<>();
                        values.add(value[1].trim());
                        bodyMap.put(keyValues[0], values);
                        lastKey = keyValues[0];
                    }
            }
        }
        return bodyMap;
    }

    public List<String> parseLinkResponse(String body) {
        String[] lines = body.split("\n");

        return processLinkBodyLines(lines);
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
