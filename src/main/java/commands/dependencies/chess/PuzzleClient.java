package commands.dependencies.chess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y0ga.Networking.WebClient;

import java.net.MalformedURLException;
import java.net.URL;

public class PuzzleClient {

    private static final String BLUNDER_TYPE_STRING = "{\"type\":\"explore\"}";
    private static final URL GET_RANDOM_BLUNDER_ENDPOINT = getURL("https://chessblunders.org/api/blunder/get");
    private final WebClient client = new WebClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public PuzzleClient() {
        this.client.Headers().put("Content-Type", "application/json");
    }

    private static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ignored) {
            throw new IllegalStateException("Unreachable");
        }
    }

    public PuzzleResponse getRandomPuzzle() {
        try {
            final String responseJson = client.uploadStringAsync(GET_RANDOM_BLUNDER_ENDPOINT, BLUNDER_TYPE_STRING).await();
            return mapper.readValue(responseJson, PuzzleResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}