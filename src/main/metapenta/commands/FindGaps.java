package metapenta.commands;

import metapenta.dto.FindGapsDTO;
import metapenta.io.jsonWriters.FindGapsWriter;
import metapenta.services.MetabolicNetworkService;

public class FindGaps {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);
        FindGapsDTO gaps = network.findGaps();

        FindGapsWriter findGapsWriter = new FindGapsWriter(gaps, args[1]);
        findGapsWriter.write();
    }
}
