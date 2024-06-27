package metapenta.commands;

import metapenta.services.MetabolicNetworkService;
import metapenta.dto.PathsDTO;
import metapenta.io.jsonWriters.FindAllPathsWriter;
import metapenta.services.FindAllPathsParams;

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
