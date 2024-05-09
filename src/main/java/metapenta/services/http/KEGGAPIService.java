package metapenta.services.http;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.tools.io.utils.kegg.KEGGEntities;
import metapenta.tools.io.utils.kegg.KEGGResponseParser;

public class KEGGAPIService {
    private KEGGEntities KEGGEntitiesUtils = new KEGGEntities();
    private MetabolicNetwork metabolicNetwork = new MetabolicNetwork();
    private KEGGResponseParser parser = new KEGGResponseParser();

}
