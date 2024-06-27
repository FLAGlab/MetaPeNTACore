package metapenta.commands;

import metapenta.model.MetabolicNetwork;
import metapenta.services.MetabolicNetworkService;
import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.io.jsonWriters.MetabolicNetworkJSONWriter;

/**
 * args[0]: First metabolic network in XML format
 * args[1]: Second metabolic network in XML format
 */
public class MetabolicNetworkInterception {

    public static void main(String[] args) throws Exception {
        MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();

        MetabolicNetworkService service = new MetabolicNetworkService(args[0]);
        MetabolicNetwork secondNetwork = loader.loadNetwork(args[1]);

        MetabolicNetwork resultNetwork = service.intercept(secondNetwork);

        MetabolicNetworkJSONWriter metabolicNetworkWriter = new MetabolicNetworkJSONWriter(resultNetwork, args[2]);
        metabolicNetworkWriter.write();
    }
}
