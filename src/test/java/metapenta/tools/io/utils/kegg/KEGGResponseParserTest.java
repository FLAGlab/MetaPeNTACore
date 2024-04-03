package metapenta.tools.io.utils.kegg;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KEGGResponseParserTest {

    @Test
    public void testParseBody() throws FileNotFoundException {
        String bodyTestFile = "src/test/resources/bodyTest.txt";
        BufferedReader clustersFile = new BufferedReader(new FileReader(bodyTestFile));
        String body = clustersFile.lines().collect(Collectors.joining("\n"));

        KEGGResponseParser parser = new KEGGResponseParser();
        Map<String, List<String>> map = parser.parseGetResponse(body);
        Assert.assertEquals(13, map.size());

        List<String> sysnameProperty = map.get("SYSNAME");
        Assert.assertEquals(1, sysnameProperty.size());

        String expectedSysValue = "ATP:protein phosphotransferase (non-specific)";
        Assert.assertEquals(expectedSysValue, sysnameProperty.get(0));

        String expectedEntryValue = "EC 2.7.11.1                 Enzyme";
        List<String> entryProperty = map.get("ENTRY");
        Assert.assertEquals(1, entryProperty.size());
        Assert.assertEquals(expectedEntryValue, entryProperty.get(0));
    }
}
