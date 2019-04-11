package hr.ferit.rekca.tensorflowtest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DescriptionDbHelper dbHelper;

    static String TAG = "DBDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseInit();

        long startTime = SystemClock.elapsedRealtime();

        db = dbHelper.getWritableDatabase();

        long endTime = SystemClock.elapsedRealtime();
        long elapsedTime = endTime - startTime;
        Log.d(TAG, String.valueOf(elapsedTime));
        Log.d(TAG, "onCreate: check");

        ContentValues values = new ContentValues();
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME, "testAnimal");
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO, "testINFORMANTION");
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE, new byte[2000]);
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS, 0.83f);
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT, 32);
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN, "01/02/1929");

        long newRowId = db.insert(DescriptionContract.DescriptionEntry.TABLE_NAME, null, values);
        Log.d(TAG, "onCreate: " + String.valueOf(newRowId));



        startTime = SystemClock.elapsedRealtime();

        db = dbHelper.getReadableDatabase();

        endTime = SystemClock.elapsedRealtime();
        elapsedTime = endTime - startTime;
        Log.d(TAG, String.valueOf(elapsedTime));
        Log.d(TAG, "onCreate: check");



        String[] projection = {
                BaseColumns._ID,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN
        };

        String selection = DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {"testAnimal"};

        String sortOrder = DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                DescriptionContract.DescriptionEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry._ID));

            String tempName = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME));
            String tempInfo = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO));
            Float tempGuess = cursor.getFloat(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS));
            Integer tempGuessCount = cursor.getInt(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT));
            String tempDate = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN));
            Log.d(TAG, "DB:  ID: " + itemId + "  NAME: " + tempName + "  INFO: " + tempInfo + " GUESS: "+ String.valueOf(tempGuess) + " GUESSCOUNT: "+
                    String.valueOf(tempGuessCount) + " DATE: " + tempDate);

            itemIds.add(itemId);
        }
        cursor.close();





    }

    /*

     */



    private void databaseInit() {
        dbHelper = new DescriptionDbHelper(getApplicationContext());
    }

    private class GetDatabase extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db = dbHelper.getWritableDatabase();
            return null;
        }

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
        db.close();
        super.onDestroy();
    }


}


