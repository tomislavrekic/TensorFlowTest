package hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb;

public class DescriptionDbSingleUnit {
    private String name;
    private String info;
    private String picture;
    private float guess;
    private int guessCount;
    private String lastSeen;

    public DescriptionDbSingleUnit(String name, String info, String  picture, float guess, int guessCount, String lastSeen) {
        this.name = name;
        this.info = info;
        this.picture = picture;
        this.guess = guess;
        this.guessCount = guessCount;
        this.lastSeen = lastSeen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setGuess(float guess) {
        this.guess = guess;
    }

    public void setGuessCount(int guessCount) {
        this.guessCount = guessCount;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPicture() {
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
