package metapenta.services;

import metapenta.model.dto.FindGapsDTO;
import metapenta.model.metabolic.network.Metabolite;

import java.util.List;

public class FindGapsService {
    private List<Metabolite> rootNoProduction;
    private List<Metabolite> rootNoConsumption;

    public FindGapsService(List<Metabolite> rootNoProduction, List<Metabolite> rootNoConsumption) {
        this.rootNoProduction = rootNoProduction;
        this.rootNoConsumption = rootNoConsumption;
    }

    public FindGapsDTO getRootGaps() {
        return new FindGapsDTO(rootNoProduction, rootNoConsumption);
    }
}
