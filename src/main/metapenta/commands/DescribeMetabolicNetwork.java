package metapenta.commands;

import metapenta.io.jsonWriters.DescribeNetworkWriter;
import metapenta.services.MetabolicNetworkService;

/**
 * args[0]: Metabolic network file
 * args[1]: Output file prefixes
 */
public class DescribeMetabolicNetwork {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);

        DescribeNetworkWriter networkWriter = new DescribeNetworkWriter(network.getNetwork(), args[1]);
        networkWriter.write();
    }
}
