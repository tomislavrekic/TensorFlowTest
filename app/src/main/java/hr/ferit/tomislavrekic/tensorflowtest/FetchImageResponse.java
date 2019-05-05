package hr.ferit.tomislavrekic.tensorflowtest;

import java.nio.ByteBuffer;

public interface FetchImageResponse {
    void processFinish(ByteBuffer output);
}
