package metapenta.commands;

import metapenta.model.dto.FindGapsDTO;
import metapenta.services.MetabolicNetworkService;
import metapenta.tools.io.writers.FindGapsWriter;

public class FindGaps {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);
        FindGapsDTO gaps = network.findGaps();

        FindGapsWriter findGapsWriter = new FindGapsWriter(gaps, args[1]);
        findGapsWriter.write();
    }
}
