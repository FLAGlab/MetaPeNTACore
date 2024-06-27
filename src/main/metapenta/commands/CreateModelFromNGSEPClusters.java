package metapenta.commands;

import metapenta.services.ClusterReactionsService;

public class CreateModelFromNGSEPClusters {

    /**
     * args[0]: NGSEP File
     * args[2]: Output prefix
     */
    public static void main(String[] args) throws Exception {
        ClusterReactionsService service = new ClusterReactionsService(args[0], args[1]);
        service.generateNetwork();
    }
}
