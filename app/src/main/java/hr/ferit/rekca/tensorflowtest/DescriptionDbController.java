package hr.ferit.rekca.tensorflowtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DescriptionDbController {
    static String TAG = "kekkek";
    SQLiteDatabase db;
    DescriptionDbHelper dbHelper;
    public DescriptionDbController(Context context){
        dbHelper = new DescriptionDbHelper(context);
    }

    private void openDbForWrite(){
        db = dbHelper.getWritableDatabase();
    }

    private void openDbForRead(){
        db = dbHelper.getReadableDatabase();
    }

    private void closeDb(){
        db.close();
    }

    public long insertRow(DescriptionDbSingleUnit input){
        openDbForWrite();

        ContentValues values = new ContentValues();
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME, input.getName());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO, input.getInfo());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE, input.getPicture());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS, input.getGuess());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT, input.getGuessCount());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN, input.getLastSeen());

        long newRowId = db.insert(DescriptionContract.DescriptionEntry.TABLE_NAME, null, values);
        Log.d(TAG, "onCreate: " + String.valueOf(newRowId));

        closeDb();
        return newRowId;
    }

    public List<DescriptionDbSingleUnit> readAll(){
        return readDb("*");
    }

    public List<DescriptionDbSingleUnit> readDb(String searchName){
        openDbForRead();

        List<DescriptionDbSingleUnit> outputRows = new ArrayList<>();


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
        String[] selectionArgs = {searchName};

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

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry._ID));
            String tempName = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME));
            String tempInfo = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO));
            byte[] tempPicture = cursor.getBlob(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE));
            Float tempGuess = cursor.getFloat(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS));
            Integer tempGuessCount = cursor.getInt(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT));
            String tempDate = cursor.getString(cursor.getColumnIndexOrThrow(DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN));

            DescriptionDbSingleUnit tempUnit = new DescriptionDbSingleUnit(tempName, tempInfo, tempPicture, tempGuess, tempGuessCount,tempDate);

            outputRows.add(tempUnit);

            Log.d(TAG, "DB:  ID: " + itemId + "  NAME: " + tempName + "  INFO: " + tempInfo + " GUESS: "+ String.valueOf(tempGuess) + " GUESSCOUNT: "+
                    String.valueOf(tempGuessCount) + " DATE: " + tempDate);

        }
        cursor.close();
        closeDb();
        return outputRows;
    }
}

//TODO: Make a DescriptionDbUpdateManager, which will determine when the db data is updated with new ones. It will use this class for reading and writing.
//TODO: Make options for updating and deleting data, right now there is only add row and read row