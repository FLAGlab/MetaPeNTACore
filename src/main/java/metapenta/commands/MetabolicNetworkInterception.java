package metapenta.commands;

import metapenta.model.MetabolicNetwork;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;
import metapenta.tools.io.writers.MetabolicNetworkJSONWriter;
import metapenta.tools.io.writers.Writer;

/**
 * args[0]: First metabolic network in XML format
 * args[1]: Second metabolic network in XML format
 */
public class MetabolicNetworkInterception {

    public static void main(String[] args) throws Exception {
        MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();

        MetabolicNetwork firstNetwork = loader.loadNetwork(args[0]);
        MetabolicNetwork secondNetwork = loader.loadNetwork(args[1]);

        MetabolicNetwork resultNetwork = firstNetwork.interception(secondNetwork);

        Writer metabolicNetworkWriter = new MetabolicNetworkJSONWriter(resultNetwork, args[2]);
        metabolicNetworkWriter.write();
    }
}
