package metapenta.services.http;

import metapenta.model.metabolic.network.Reaction;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class KEGGServiceTest {
    @Test
    public void testGetReactions() throws Exception {
        KEGGService service = new KEGGService();
        Set<String> reactions = service.getReactions("ath:AT5G17820");

        System.out.println("Test passed");
    }
}
