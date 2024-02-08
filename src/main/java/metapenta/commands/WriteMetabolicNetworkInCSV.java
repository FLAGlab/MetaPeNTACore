package metapenta.commands;

import metapenta.model.MetabolicNetwork;
import metapenta.tools.io.loaders.MetabolicNetworkXMLLoader;
import metapenta.tools.io.writers.MetabolicNetworkCSVWriter;
import metapenta.tools.io.writers.Writer;

/**
 * args[0]: Metabolic network in XML format
 * args[1]: Output file
 */
public class WriteMetabolicNetworkInCSV {

    public static void main(String[] args) throws Exception {
        MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();
        MetabolicNetwork network = loader.loadNetwork(args[0]);

        Writer metabolicWriter = new MetabolicNetworkCSVWriter(network, args[1]);
        metabolicWriter.write();
    }
}
