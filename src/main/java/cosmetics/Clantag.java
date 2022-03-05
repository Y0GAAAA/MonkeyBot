package cosmetics;

public class Clantag {

    public static String[] getCycle(String text) {
        int textLength = text.length();
        String[] cycleFrames = new String[textLength * 2 - 1];

        for (int i = 0; i < textLength; i++) {
            cycleFrames[i] = text.substring(0, i + 1);
            cycleFrames[i + textLength - 1] = text.substring(0, textLength - i);
        }
        
        return cycleFrames;
    }

    public static StringBuilder getPyramid(String text) {
        StringBuilder pyramid = new StringBuilder(text.length() ^ 2 + text.length() * 2);

        for (String pyramidLayer : getCycle(text)) {
            pyramid.append(pyramidLayer);
            pyramid.append('\n');
        }

        return pyramid;
    }

}