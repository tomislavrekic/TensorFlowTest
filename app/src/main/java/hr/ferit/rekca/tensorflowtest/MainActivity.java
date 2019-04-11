package hr.ferit.rekca.tensorflowtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button switchActivity;
    Button switchToDatabase;
    private ByteBuffer imgData = null;

    private static String TAG = "ScreenOne";

    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    static final int DIM_IMG_SIZE_X = 224;
    static final int DIM_IMG_SIZE_Y = 224;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private static final String MODEL_PATH = "Mobile2.tflite";

    private List<String> labels;

    private int[] intValues;

    float[][] output = null;

    Interpreter tflite;

    Bitmap inputImage;

    int guessedLabel;
    float guessedActivation;

    Intent intent;
    Intent intentDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        long startTime = SystemClock.elapsedRealtime();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.ivPreview);
        textView = findViewById(R.id.tvGuess);
        switchActivity = findViewById(R.id.btnSwitch);
        switchActivity.setEnabled(false);
        intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        switchToDatabase = findViewById(R.id.btnDatabase);
        intentDatabase = new Intent(this, DatabaseActivity.class);
        intentDatabase.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);


        initBtnListeners();


        initLabels();
        initModel();
        initializeVariables();

        long endTime = SystemClock.elapsedRealtime();
        long elapsedTime = endTime - startTime;
        Log.d(TAG, String.valueOf(elapsedTime));
        Log.d(TAG, "onCreate: check");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: check");
        classifier();


    }

    private void initBtnListeners() {
        switchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    switchActivity.setEnabled(false);
                    startActivityIfNeeded(intent,0);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        switchToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(intentDatabase);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initModel() {
        try {
            tflite = new Interpreter(getModelFile());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void initLabels() {
        labels = new ArrayList<String>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
            String line;
            while((line = reader.readLine()) != null){
                labels.add(line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



    public void classifier(){

        new GetImageTask().execute();


    }


    class RunNeuralNetworkTask extends AsyncTask<Void, Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(inputImage==null){
                return null;
            }

            convertBitmapToByteBuffer(inputImage);

            tflite.run(imgData, output);

            guessedLabel = processLabelProb();

            Log.d("result",labels.get(guessedLabel));
            Log.d("result",String.valueOf(output[0][guessedLabel]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayResults();
            switchActivity.setEnabled(true);
        }
    }

    class GetImageTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            inputImage = GetImage();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayImage();

            displayLoading();


            new RunNeuralNetworkTask().execute();
        }
    }

    private void displayImage() {
        imageView.setImageBitmap(inputImage);
    }

    private void displayLoading() {
        textView.setText("Calculating...");
    }

    private int processLabelProb() {
        guessedLabel = 0;
        for(int i=1; i<labels.size();i++){
            if(output[0][i]>output[0][guessedLabel]){
                guessedLabel = i;
            }
        }
        guessedActivation = output[0][guessedLabel];
        return guessedLabel;
    }

    private void displayResults() {
        textView.setText("Guessed: " + labels.get(guessedLabel) + "\n" + String.valueOf(guessedActivation));
    }

    private Bitmap GetImage() {
        Bitmap bitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(this.openFileInput("TensorFlowTestImage"));
            return Bitmap.createScaledBitmap(bitmap,DIM_IMG_SIZE_X,DIM_IMG_SIZE_Y, true);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        new DeleteImageTask().execute();
        Log.d(TAG, "onStop: check");
        //firkeGay
    }

    class DeleteImageTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            File dir = getFilesDir();
            File file = new File(dir,"TensorFlowTestImage");
            if (!file.exists()){
                return null;
            }
            file.delete();
            return null;
        }
    }


    private void initializeVariables() {
        output = new float[1][labels.size()];
        imgData = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
    }

    private File getModelFile() throws IOException {
        File file = new File(getApplicationContext().getFilesDir(), "test.tflite");
        if(file.exists()) {
            return file;
        }
        InputStream inputStream = this.getAssets().open(MODEL_PATH);
        copyFile(inputStream, new FileOutputStream(file));
        return file;
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.

        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat(((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD));
                imgData.putFloat(((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD));
                imgData.putFloat(((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD));
            }
        }
    }



    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}
