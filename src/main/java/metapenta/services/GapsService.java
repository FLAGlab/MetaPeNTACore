package metapenta.services;

import metapenta.model.dto.GapsDTO;
import metapenta.model.metabolic.network.Metabolite;

import java.util.List;

public class GapsService {
    private List<Metabolite> rootNoProduction;
    private List<Metabolite> rootNoConsumption;

    public GapsService(List<Metabolite> rootNoProduction, List<Metabolite> rootNoConsumption) {
        this.rootNoProduction = rootNoProduction;
        this.rootNoConsumption = rootNoConsumption;
    }

    public GapsDTO getRootGaps() {
        return new GapsDTO(rootNoProduction, rootNoConsumption);
    }
}
