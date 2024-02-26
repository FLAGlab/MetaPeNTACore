package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.model.dto.PathsDTO;
import metapenta.model.params.FindAllPathsParams;
import metapenta.tools.io.writers.FindAllPathsWriter;

/**
 * args[0]: XML model
 * args[1]: Init metabolites separated by comma
 * args[2]: Target metabolite
 * args[3] Output file
 */
public class FindAllPaths {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService network = new MetabolicNetworkService(args[0]);

        FindAllPathsParams findAllPathsParams = new FindAllPathsParams(args[1], args[2]);
        PathsDTO paths = network.getAllPaths(findAllPathsParams);

        FindAllPathsWriter findAllPathsWriter = new FindAllPathsWriter(args[3], paths);
        findAllPathsWriter.write();
    }
}
