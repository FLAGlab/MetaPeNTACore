package metapenta.commands;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.services.IMetabolicNetworkService;
import metapenta.services.MetabolicNetworkService;
import metapenta.tools.io.writers.DescribeNetworkWriter;

/**
 * args[0]: Metabolic network file
 * args[1]: Output file prefixes
 */
public class DescribeMetabolicNetwork {
    public static void main(String[] args) throws Exception {
        IMetabolicNetworkService network = new MetabolicNetworkService(args[0]);

        DescribeNetworkWriter networkWriter = new DescribeNetworkWriter(network.getNetwork(), args[1]);
        networkWriter.write();
    }
}
