package metapenta.commands;

import metapenta.services.MetabolicNetworkService;

public class MetabolitesByCompartment {
    public static void main(String[] args) throws Exception {
        MetabolicNetworkService metabolicNetwork = new  MetabolicNetworkService(args[0]);

        metabolicNetwork.getMetabolitesByCompartment(args[1]);
    }

}
