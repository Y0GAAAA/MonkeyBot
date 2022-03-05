package parsing.arguments;

public class Sentence {

    private final String stringValue;

    public Sentence(String[] words) {
        StringBuilder builder = new StringBuilder();

        for (String s : words) {
            builder.append(s);
            builder.append(' ');
        }
        
        builder.setLength(builder.length() - 1);
        this.stringValue = builder.toString();
    }

    @Override
    public String toString() {
        return stringValue;
    }

}