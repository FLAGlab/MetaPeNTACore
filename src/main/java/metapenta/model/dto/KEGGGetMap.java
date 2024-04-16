package metapenta.model.dto;

import java.util.List;

public class KEGGGetMap {

    String key;
    List<String> value;

    public KEGGGetMap(String key, List<String> value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
