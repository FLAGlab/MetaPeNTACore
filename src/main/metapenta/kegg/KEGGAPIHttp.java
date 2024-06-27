package metapenta.kegg;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KEGGAPIHttp {

    public String get(String link){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(link)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 ) {
                System.out.println("There was an error processing the request: " + response.statusCode() + " " + response.body());
                return "";
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
