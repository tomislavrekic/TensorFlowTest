package hr.ferit.rekca.tensorflowtest;

import java.nio.ByteBuffer;

public interface FetchImageResponse {
    void processFinish(ByteBuffer output);
}
