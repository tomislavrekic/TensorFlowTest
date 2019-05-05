package hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb;


import static hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionContract.DescriptionEntry;

public final class DescriptionDatabaseSQLCommands {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DescriptionEntry.TABLE_NAME + " (" +
            DescriptionEntry._ID + " INTEGER PRIMARY KEY," +
            DescriptionEntry.COLUMN_NAME_NAME + " VARCHAR(25), " +
            DescriptionEntry.COLUMN_NAME_INFO + " TEXT, " +
            DescriptionEntry.COLUMN_NAME_PICTURE + " VARCHAR(50)," +
            DescriptionEntry.COLUMN_NAME_GUESS + " FLOAT, " +
            DescriptionEntry.COLUMN_NAME_GUESS_COUNT + " INTEGER, " +
            DescriptionEntry.COLUMN_NAME_LAST_SEEN + " DATE)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DescriptionEntry.TABLE_NAME;
}



/*



 */