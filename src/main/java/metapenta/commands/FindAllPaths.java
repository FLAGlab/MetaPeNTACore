package metapenta.commands;

import metapenta.model.MetaPenta;
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
        FindAllPathsParams findAllPathsParams = new FindAllPathsParams(args[1], args[2]);

        MetaPenta network = new MetaPenta(args[0]);
        PathsDTO paths = network.getAllPaths(findAllPathsParams);

        FindAllPathsWriter findAllPathsWriter = new FindAllPathsWriter(args[3], paths);
        findAllPathsWriter.write();
    }
}
