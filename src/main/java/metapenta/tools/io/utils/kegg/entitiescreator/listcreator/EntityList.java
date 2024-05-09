package metapenta.tools.io.utils.kegg.entitiescreator.listcreator;

import java.util.List;

public class EntityList implements metapenta.model.metabolic.network.ID {

    List<String> list;

    private String ID;

    public EntityList(List<String> list, String ID) {
        this.list = list;
        this.ID = ID;
    }

    public List<String> getList() {
        return list;
    }

    public String ID() {
        return ID;
    }
}
