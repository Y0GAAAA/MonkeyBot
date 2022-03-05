package commands.dependencies.tiktok;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y0ga.Networking.WebClient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TiktokVideoUrlResolver {
    
    private final WebClient tiktokClient = new WebClient();
    private final ObjectMapper mapper = new ObjectMapper();
    
    public String resolve(URL tiktokURL) {
    
        URL finalUrl = getMapURL(tiktokURL);
        
        try {
    
            final String videoMapJson = tiktokClient.downloadStringAsync(finalUrl).await();
            
            return mapper.readValue(videoMapJson, new TypeReference<Map<String, String>>() {})
                         .get("no_watermark");
            
        } catch (Exception ex) { return null; }
        
    }
    
    private static URL getMapURL(URL tiktokUrl) {
        try {
            return new URL("https://tokconv.herokuapp.com/?url=" + tiktokUrl.toExternalForm());
        } catch (MalformedURLException ignored) {
            throw new IllegalStateException();
        }
    }
    
}