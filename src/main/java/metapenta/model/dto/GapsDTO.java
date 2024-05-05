package metapenta.model.dto;

import metapenta.model.metabolic.network.Metabolite;

import java.util.List;

public class GapsDTO {
    private List<Metabolite> rootNoProduction;
    private List<Metabolite> rootNoConsumption;

    public GapsDTO(List<Metabolite> rootNoProduction, List<Metabolite> rootNoConsumption){
        this.rootNoProduction = rootNoProduction;
        this.rootNoConsumption = rootNoConsumption;
    }

    public List<Metabolite> getRootNoProduction() {
        return rootNoProduction;
    }

    public List<Metabolite> getRootNoConsumption() {
        return rootNoConsumption;
    }

}
