package hr.ferit.rekca.tensorflowtest;

public class DescriptionDbSingleUnit {
    private String name;
    private String info;
    private byte[] picture;
    private float guess;
    private int guessCount;
    private String lastSeen;

    public DescriptionDbSingleUnit(String name, String info, byte[] picture, float guess, int guessCount, String lastSeen) {
        this.name = name;
        this.info = info;
        this.picture = picture;
        this.guess = guess;
        this.guessCount = guessCount;
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public byte[] getPicture() {
        return picture;
    }

    public float getGuess() {
        return guess;
    }

    public int getGuessCount() {
        return guessCount;
    }

    public String getLastSeen() {
        return lastSeen;
    }
}
