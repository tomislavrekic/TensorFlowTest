package hr.ferit.rekca.tensorflowtest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Classifier {
    private Context mContext;
    private Interpreter tflite;
    private List<String> labels;
    ClassifierResponse mDelegate;

    public Classifier(String modelFileName, String labelFileName, Context context){
        mContext = context;
        initModel(modelFileName);
        initLabels(labelFileName);
    }

    public void Classify(ClassifierResponse delegate){
        mDelegate = delegate;
        new FetchImageTask(new FetchImageResponse() {
            @Override
            public void processFinish(ByteBuffer output) {
                new RunNeuralNetworkTask().execute(output);
            }
        }, mContext).execute(Constants.TEMP_IMAGE_KEY);

    }

    class RunNeuralNetworkTask extends AsyncTask<ByteBuffer, Void,float[][]> {

        @Override
        protected float[][] doInBackground(ByteBuffer... params) {
            ByteBuffer imgData = params[0];
            float[][] output = new float[1][labels.size()];

            if(imgData == null) return output;

            tflite.run(imgData, output);

            return output;
        }

        @Override
        protected void onPostExecute(float[][] floats) {
            super.onPostExecute(floats);
            int guessedLabelIndex = processLabelProb(floats);
            float guessedActivation = floats[0][guessedLabelIndex];

            mDelegate.processFinished(guessedLabelIndex,guessedActivation);


        }
    }

    private int processLabelProb(float[][] input) {
        int guessedLabelIndex = 0;
        for(int i=1; i<labels.size();i++){
            if(input[0][i]>input[0][guessedLabelIndex]){
                guessedLabelIndex = i;
            }
        }
        return guessedLabelIndex;
    }



    private void initLabels(String labelFileName) {
        labels = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open(labelFileName)));
            String line;
            while((line = reader.readLine()) != null){
                labels.add(line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void initModel(String modelFileName) {
        try {
            tflite = new Interpreter(getModelFile(modelFileName));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private File getModelFile(String modelFileName) throws IOException {
        File file = new File(mContext.getFilesDir(), Constants.TF_MODEL_KEY);
        if(file.exists()) {
            return file;
        }
        InputStream inputStream = mContext.getAssets().open(modelFileName);
        copyFile(inputStream, new FileOutputStream(file));
        return file;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
