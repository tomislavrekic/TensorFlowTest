package hr.ferit.tomislavrekic.tensorflowtest;

public interface ClassifierResponse {
    void processFinished(int guessedLabelIndex, float guessedActivation);
}
