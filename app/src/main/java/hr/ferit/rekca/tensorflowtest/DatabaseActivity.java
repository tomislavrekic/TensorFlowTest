package hr.ferit.rekca.tensorflowtest;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    DescriptionDbController controller;
    List<DescriptionDbSingleUnit> items;

    private static String TAG = "DBDebug";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DescriptionDbInputInit.initDb(this);

        controller = new DescriptionDbController(this);

        items = controller.readAll();

        for (int i=0;i<items.size();i++){
            DescriptionDbSingleUnit tempUnit = items.get(i);
            Log.d(TAG, "DB: NAME: " + tempUnit.getName() + " INFO: " + tempUnit.getInfo() + " GUESS: " +
                    tempUnit.getGuess() + " GUESSCOUNT: " + tempUnit.getGuessCount() + " LASTSEEN: " + tempUnit.getLastSeen());
        }

        Log.d(TAG, "onCreate: check");

        //TODO: Recycler view to show this info in

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


