package metapenta.services.dto;

import metapenta.model.Reaction;

import java.util.List;

public class MetaboliteReactionsDTO {

    private List<Reaction> reactionsIsSubstrate;
    private List<Reaction> reactionsIsProduct;

    public MetaboliteReactionsDTO(List<Reaction> reactionsIsSubstrate, List<Reaction> reactionsIsProduct) {
        this.reactionsIsSubstrate = reactionsIsSubstrate;
        this.reactionsIsProduct = reactionsIsProduct;
    }

    public List<Reaction> getReactionsIsProduct() {
        return reactionsIsProduct;
    }

    public List<Reaction> getReactionsIsSubstrate() {
        return reactionsIsSubstrate;
    }
}
