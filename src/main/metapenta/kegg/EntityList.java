package metapenta.kegg;

import java.util.List;

public class EntityList {

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
