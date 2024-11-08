package metapenta.kegg;

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
