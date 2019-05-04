package hr.ferit.rekca.tensorflowtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hr.ferit.rekca.tensorflowtest.DescriptionDb.DescriptionDbSingleUnit;
import hr.ferit.rekca.tensorflowtest.DescriptionDb.DescriptionDbUpdateManager;

public class MainActivity extends AppCompatActivity {

    //TODO: Clean this code, put in an another class

    ImageView imageView;
    TextView textView;
    Button switchToCamera;
    Button switchToDatabase;

    private static String TAG = "ScreenOne";

    private List<String> labels;

    Intent intent;
    Intent intentDatabase;

    Bitmap inputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        long startTime = SystemClock.elapsedRealtime();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.ivPreview);
        textView = findViewById(R.id.tvGuess);
        switchToDatabase = findViewById(R.id.btnDatabase);
        switchToCamera = findViewById(R.id.btnSwitch);
        switchToCamera.setEnabled(false);

        intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);



        intentDatabase = new Intent(this, DatabaseActivity.class);
        intentDatabase.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        initBtnListeners();

        initLabels();

        long endTime = SystemClock.elapsedRealtime();
        long elapsedTime = endTime - startTime;
        Log.d(TAG, String.valueOf(elapsedTime));
        Log.d(TAG, "onCreate: check");

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: check");
        //TODO: make it not run when back button is pressed, and only when the picture is taken
        classifier();

    }

    private void initBtnListeners() {
        switchToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    switchToCamera.setEnabled(false);
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


    private void initLabels() {
        labels = new ArrayList<>();
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
        inputImage = GetImage();
        displayImage(inputImage);
        displayLoading();
        temptemp();
        switchToCamera.setEnabled(true);
    }



    private void temptemp(){
        Classifier temp = new Classifier(Constants.TF_MODEL_PATH,Constants.TF_LABEL_PATH, getBaseContext());
        temp.Classify(new ClassifierResponse() {
            @Override
            public void processFinished(int guessedLabelIndex, float guessedActivation) {
                Log.d("CLS", "onPostExecute: " + String.valueOf(guessedLabelIndex) + "   "  + String.valueOf(guessedActivation));
                displayResults(guessedLabelIndex, guessedActivation);
            }
        });
    }

    private void updateDb(int guessedLabelIndex, float guessedActivation) {
        DescriptionDbUpdateManager manager = new DescriptionDbUpdateManager(this);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);



        DescriptionDbSingleUnit tempUnit = new DescriptionDbSingleUnit(labels.get(guessedLabelIndex), null, createImageFromBitmapDB(inputImage), guessedActivation, 0, formattedDate);
        //TODO: make a factory class to unfuck this and to decide if the pic gets replaced

        manager.UpdateRow(tempUnit);
    }

    public String createImageFromBitmapDB(Bitmap bitmap) {
        String fileName = "tempNameFile";//no .png or .jpg needed
        try {
            Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    private void displayImage(Bitmap image) {
        imageView.setImageBitmap(image);
    }

    private void displayLoading() {
        textView.setText("Calculating...");
    }


    private void displayResults(int guessedLabelIndex, Float guessedActivation) {
        textView.setText("Guessed: " + labels.get(guessedLabelIndex) + "\n" + String.valueOf(guessedActivation));
    }

    private Bitmap GetImage() {
        Bitmap bitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(this.openFileInput(Constants.TEMP_IMAGE_KEY));
            //TODO: could scale the image before saving it
            return Bitmap.createScaledBitmap(bitmap,Constants.DIM_IMG_SIZE_X,Constants.DIM_IMG_SIZE_Y, true);

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
            File file = new File(dir,Constants.TEMP_IMAGE_KEY);
            if (!file.exists()){
                return null;
            }
            file.delete();
            return null;
        }
    }

}
