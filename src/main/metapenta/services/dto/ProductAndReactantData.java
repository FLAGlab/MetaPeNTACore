package metapenta.services.dto;

import metapenta.model.ReactionComponent;

import java.util.ArrayList;
import java.util.List;

public class ProductAndReactantData {
    private List<ReactionComponent> reactantsList = new ArrayList<>();
    private List<ReactionComponent> productsList = new ArrayList<>();

    public ProductAndReactantData(List<ReactionComponent> reactantsList, List<ReactionComponent> productsList) {
        this.reactantsList = reactantsList;
        this.productsList = productsList;
    }
    public List<ReactionComponent> getProductsList() {
        return productsList;
    }

    public List<ReactionComponent> getReactantsList() {
        return reactantsList;
    }

    public void setProductsList(List<ReactionComponent> productsList) {
        this.productsList = productsList;
    }

    public void setReactantsList(List<ReactionComponent> reactantsList) {
        this.reactantsList = reactantsList;
    }
}
