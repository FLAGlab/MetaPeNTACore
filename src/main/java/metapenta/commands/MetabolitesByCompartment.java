package metapenta.commands;

import metapenta.services.IMetabolicNetworkService;
import metapenta.services.MetabolicNetworkService;

public class MetabolitesByCompartment {
    public static void main(String[] args) throws Exception {
        IMetabolicNetworkService metabolicNetwork = new  MetabolicNetworkService(args[0]);

        metabolicNetwork.getMetabolitesByCompartment("");
    }

}
