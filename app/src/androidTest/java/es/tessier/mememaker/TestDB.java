package es.tessier.mememaker;

/**
 *
 * Created by carlosfernandez on 05/01/15.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import es.tessier.mememaker.database.MemeSQLiteHelper;

public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MemeSQLiteHelper.DB_NAME);
        SQLiteDatabase db = new MemeSQLiteHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MemeSQLiteHelper dbHelper = new MemeSQLiteHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createJulioMeme();

        long memeRowId;
        memeRowId = db.insert(MemeSQLiteHelper.MEMES_TABLE, null, testValues);

        // Verify we got a row back.
        assertTrue(memeRowId != -1);
        Log.d(LOG_TAG, "New row id: " + memeRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MemeSQLiteHelper.MEMES_TABLE,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a meme, add some annotations!
        ContentValues annotationsValues = createAnnotationsValues(memeRowId);

        long weatherRowId = db.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationsValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                MemeSQLiteHelper.ANNOTATIONS_TABLE,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(weatherCursor, annotationsValues);

        dbHelper.close();
    }

    static ContentValues createAnnotationsValues(long memeRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_NAME, "frase arriba");
        weatherValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_TITLE, "frase arriba");
        weatherValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_X, 1);
        weatherValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_Y, 1);
        weatherValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_COLOR, 0xFFFFFF);
        weatherValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeRowId);

        return weatherValues;
    }

    static ContentValues createJulioMeme() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MemeSQLiteHelper.COLUMN_MEMES_ASSET, "imagen.jpg");
        testValues.put(MemeSQLiteHelper.COLUMN_MEMES_NAME, "Julio Iglesias");

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
