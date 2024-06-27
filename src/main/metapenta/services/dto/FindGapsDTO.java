package metapenta.services.dto;

import metapenta.model.Metabolite;

import java.util.List;

public class FindGapsDTO {
    private List<Metabolite> rootNoProduction;
    private List<Metabolite> rootNoConsumption;

    public FindGapsDTO(List<Metabolite> rootNoProduction, List<Metabolite> rootNoConsumption){
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
