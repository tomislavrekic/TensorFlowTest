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
    private String TAG = "kekkek";
    private SQLiteDatabase db;
    private DescriptionDbHelper dbHelper;

    public enum Mode {UPDATE_COUNTER, UPDATE_PICTURE, UPDATE_FULL }

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

        long newRowId = db.insert(DescriptionContract.DescriptionEntry.TABLE_NAME, null, InputToContentVal(input, Mode.UPDATE_FULL));

        Log.d(TAG, "onCreate: " + String.valueOf(newRowId));

        closeDb();
        return newRowId;
    }

    public List<DescriptionDbSingleUnit> readAll(){
        return readDb("%");
    }

    public List<DescriptionDbSingleUnit> readDb(String searchName){
        openDbForRead();

        String[] projection = {
                BaseColumns._ID,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT,
                DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN
        };

        String selection = DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME + " LIKE ?";
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

        List<DescriptionDbSingleUnit> outputRows = new ArrayList<>();

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

    public void updateRow(DescriptionDbSingleUnit inputRow, Mode mode){
        db = dbHelper.getWritableDatabase();

        String selection = DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { inputRow.getName() };

        ContentValues values = InputToContentVal(inputRow, mode);

        db.update(
            DescriptionContract.DescriptionEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs);

    }

    private ContentValues InputToContentVal(DescriptionDbSingleUnit input, Mode mode){
        ContentValues values = new ContentValues();

        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS_COUNT, input.getGuessCount());
        values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_LAST_SEEN, input.getLastSeen());

        if(mode == Mode.UPDATE_PICTURE || mode == Mode.UPDATE_FULL){
            values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_PICTURE, input.getPicture());
            values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_GUESS, input.getGuess());
        }
        if(mode == Mode.UPDATE_FULL){
            values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_NAME, input.getName());
            values.put(DescriptionContract.DescriptionEntry.COLUMN_NAME_INFO, input.getInfo());
        }

        return values;
    }


}

//TODO: Make a method that will initialize the database with names and infos of all animal classes. You have labels for names, infos will be in a seperate file. (or in strings file)