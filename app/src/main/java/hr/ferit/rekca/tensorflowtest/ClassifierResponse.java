package hr.ferit.rekca.tensorflowtest;

public interface ClassifierResponse {
    void processFinished(int guessedLabelIndex, float guessedActivation);
}
