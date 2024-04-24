package metapenta.tools.io.utils.kegg;

import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;

import java.util.Collection;

public class CompletionStateParams<T> {
    EntityCreator<T> creator;
    Collection<String> ids;

    public EntityCreator<T> getCreator() {
        return creator;
    }

    public Collection<String> getIds() {
        return ids;
    }


    public void setCreator(EntityCreator<T> creator) {
        this.creator = creator;
    }
}
