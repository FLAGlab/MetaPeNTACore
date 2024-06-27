package metapenta.commands;

import metapenta.model.MetabolicNetwork;
import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.io.jsonWriters.MetabolicNetworkCSVWriter;

/**
 * args[0]: Metabolic network in XML format
 * args[1]: Output file
 */
public class WriteMetabolicNetworkInCSV {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
        MetabolicNetwork network = loader.loadNetwork(args[0]);

        MetabolicNetworkCSVWriter metabolicWriter = new MetabolicNetworkCSVWriter(network, args[1]);
        metabolicWriter.write();
    }
}
