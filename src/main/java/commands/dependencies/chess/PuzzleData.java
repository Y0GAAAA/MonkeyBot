package commands.dependencies.chess;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "blunderMove",
        "elo",
        "fenBefore",
        "forcedLine",
        "game_id",
        "id",
        "move_index"
})

@Generated("jsonschema2pojo")
public class PuzzleData {

    @JsonProperty("blunderMove")
    public String blunderMove;

    @JsonProperty("elo")
    public Integer elo;

    @JsonProperty("fenBefore")
    public String fenBefore;

    @JsonProperty("forcedLine")
    public List<String> forcedLine = null;

    @JsonProperty("game_id")
    public String gameId;

    @JsonProperty("id")
    public String id;

    @JsonProperty("move_index")
    public Integer moveIndex;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PuzzleData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("blunderMove");
        sb.append('=');
        sb.append(((this.blunderMove == null) ? "<null>" : this.blunderMove));
        sb.append(',');
        sb.append("elo");
        sb.append('=');
        sb.append(((this.elo == null) ? "<null>" : this.elo));
        sb.append(',');
        sb.append("fenBefore");
        sb.append('=');
        sb.append(((this.fenBefore == null) ? "<null>" : this.fenBefore));
        sb.append(',');
        sb.append("forcedLine");
        sb.append('=');
        sb.append(((this.forcedLine == null) ? "<null>" : this.forcedLine));
        sb.append(',');
        sb.append("gameId");
        sb.append('=');
        sb.append(((this.gameId == null) ? "<null>" : this.gameId));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("moveIndex");
        sb.append('=');
        sb.append(((this.moveIndex == null) ? "<null>" : this.moveIndex));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    public boolean whiteToMove() {
        return moveIndex % 2 != 0;
    }

    public String getNormalizedSolutionString() {

        return this.forcedLine
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "))
                .trim();

    }

}