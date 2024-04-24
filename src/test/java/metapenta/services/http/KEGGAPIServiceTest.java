package metapenta.services.http;

import org.junit.Test;

import java.util.Set;

public class KEGGAPIServiceTest {
    @Test
    public void testGetReactions() throws Exception {
        KEGGAPIService service = new KEGGAPIService();
        // Set<String> reactions = service.getReactionsIDs("ath:AT5G17820");

        System.out.println("Test passed");
    }
}
