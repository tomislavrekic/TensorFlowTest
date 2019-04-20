package hr.ferit.rekca.tensorflowtest.DescriptionDb;

import android.provider.BaseColumns;

public final class DescriptionContract {

    private DescriptionContract() {}

    public static class DescriptionEntry implements BaseColumns {
        //picture, top percentage guess, name, description, number of guesses, date last seen

        public static final String TABLE_NAME = "description";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_GUESS = "guess";
        public static final String COLUMN_NAME_GUESS_COUNT = "guessCount";
        public static final String COLUMN_NAME_LAST_SEEN = "lastSeen";
    }
}
