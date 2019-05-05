package hr.ferit.tomislavrekic.tensorflowtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;


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

    Classifier classifier;

    DescriptionDbUpdater dbUpdater;

    BroadcastReceiver receiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initIntents();

        initBtnListeners();

        configureReceiver();

        classifier = new Classifier(Constants.TF_MODEL_PATH,Constants.TF_LABEL_PATH, getBaseContext());
        labels = classifier.getLabels();

        dbUpdater = new DescriptionDbUpdater(this, labels);
    }

    private void configureReceiver() {
        filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_KEY1);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                classifier();

                try {
                    unregisterReceiver(receiver);
                }
                catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        };
    }


    private void initIntents() {
        intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        intentDatabase = new Intent(this, DatabaseActivity.class);
        intentDatabase.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    private void initViews() {
        imageView = findViewById(R.id.ivPreview);
        textView = findViewById(R.id.tvGuess);
        switchToDatabase = findViewById(R.id.btnDatabase);
        switchToCamera = findViewById(R.id.btnSwitch);
        switchToCamera.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver,filter);

        switchToCamera.setEnabled(true);
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
    public void classifier(){
        inputImage = GetImage();
        displayImage(inputImage);
        displayLoading();

        classifier.Classify(new ClassifierResponse() {
            @Override
            public void processFinished(int guessedLabelIndex, float guessedActivation) {
                displayResults(guessedLabelIndex, guessedActivation);
                dbUpdater.updateDb(guessedLabelIndex, guessedActivation, inputImage);
            }
        });
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
