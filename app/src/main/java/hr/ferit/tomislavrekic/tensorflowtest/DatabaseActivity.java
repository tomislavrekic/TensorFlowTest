package hr.ferit.tomislavrekic.tensorflowtest;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionDbController;
import hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionDbInputInit;
import hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionDbSingleUnit;

public class DatabaseActivity extends AppCompatActivity {
    DescriptionDbController controller;
    List<DescriptionDbSingleUnit> items;

    private static String TAG = "DBDebug";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private OnClickListener listener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        DescriptionDbInputInit.initDb(this);

        controller = new DescriptionDbController(this);

        items = controller.readAll();

        recyclerView = (RecyclerView) findViewById(R.id.rvDescriptionDb);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new DbListAdapter(items, this, new OnClickListener() {
            @Override
            public void onClick(int pos) {
                //show dialog
                Log.d("DBD", "onClick: " + String.valueOf(pos));
                showDialogFragment(pos);
            }
        });
        recyclerView.setAdapter(mAdapter);


    }

    void showDialogFragment(int pos) {
        DialogFragment df = DBDialogFragment.newInstance(items.get(pos), this);
        df.show(this.getSupportFragmentManager(), "DBD");
    }


    private void initDbase() {
        DescriptionDbInputInit.initDb(this);

        controller = new DescriptionDbController(this);

        items = controller.readAll();

        for (int i=0;i<items.size();i++){
            DescriptionDbSingleUnit tempUnit = items.get(i);
            Log.d(TAG, "DB: NAME: " + tempUnit.getName() + " INFO: " + tempUnit.getInfo() + " GUESS: " +
                    tempUnit.getGuess() + " GUESSCOUNT: " + tempUnit.getGuessCount() + " LASTSEEN: " + tempUnit.getLastSeen());
        }

        Log.d(TAG, "onCreate: check");
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


