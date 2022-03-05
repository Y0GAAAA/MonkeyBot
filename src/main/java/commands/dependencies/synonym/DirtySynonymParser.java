package commands.dependencies.synonym;

import com.y0ga.Networking.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirtySynonymParser {

    private final WebClient client = new WebClient();
    private final Pattern synonymsPattern = Pattern.compile("<a href=\"\\?word=.*?\">(.*?)</a>");
    
    public List<String> getSynonyms(String word, Language lang) {
        final String html;

        try {
            html = client.downloadStringAsync(lang.getUrlForWord(word)).await();
        } catch (Exception ignored) { return null; }
    
        Matcher matcher = synonymsPattern.matcher(html);
        ArrayList<String> synonyms = new ArrayList<>();
    
        while (matcher.find())
            synonyms.add(matcher.group(1));
        
        return synonyms;
    }

    public enum Language {
        
        English("http://www.synonymy.com/synonym.php?word="),
        French("http://www.synonymes.com/synonyme.php?mot=");
        
        String urlPrefix;
        
        Language(String url) {
            urlPrefix = url;
        }
        
        public URL getUrlForWord(String word) {
            try {
                return new URL(urlPrefix + word);
            } catch (MalformedURLException ignored) { throw new IllegalStateException("unreachable"); }
        }
        
    }
    
}