package metapenta.commands;

import metapenta.model.dto.GapsDTO;
import metapenta.services.MetabolicNetworkService;
import metapenta.tools.io.writers.FindGapsWriter;

public class FindGaps {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);
        GapsDTO gaps = network.findGaps();

        FindGapsWriter gapsWriter = new FindGapsWriter(gaps, args[1]);
        gapsWriter.write();
    }
}
