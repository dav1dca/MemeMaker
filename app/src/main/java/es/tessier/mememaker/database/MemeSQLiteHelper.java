package es.tessier.mememaker.database;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemeSQLiteHelper extends SQLiteOpenHelper {

    final static String DB_NAME ="memes.db";
    final static int DB_VERSION = 1;
    static final String TAG = MemeSQLiteHelper.class.getName();

    //Meme Table functionality
    static final String MEMES_TABLE = "MEMES";
    static final String COLUMN_MEMES_ASSET = "asset";
    static final String COLUMN_MEMES_NAME = "name";
    static final String COLUMN_MEMES_ID = "_id";

    static final String CREATE_TABLE_MEMES = "CREATE TABLE " + MEMES_TABLE + " ( "+
            COLUMN_MEMES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MEMES_ASSET + " TEXT NOT NULL," +
            COLUMN_MEMES_NAME + " TEXT NOT NULL );";

    //Meme Table Annotations functionality
    static final String ANNOTATIONS_TABLE = "ANNOTATIONS";
    static final String COLUMN_ANNOTATIONS_ID = "_id";
    static final String COLUMN_ANNOTATIONS_NAME = "name";
    static final String COLUMN_ANNOTATIONS_TITLE = "title";
    static final String COLUMN_ANNOTATIONS_X = "x";
    static final String COLUMN_ANNOTATIONS_Y = "y";
    static final String COLUMN_ANNOTATIONS_COLOR = "color";
    static final String COLUMN_FOREIGN_KEY_MEME = "fk_meme_id";

    static final String CREATE_TABLE_ANNOTATIONS = "CREATE TABLE " + ANNOTATIONS_TABLE + " ( "+
            COLUMN_ANNOTATIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ANNOTATIONS_NAME + " TEXT NOT NULL," +
            COLUMN_ANNOTATIONS_TITLE + " TEXT NOT NULL," +
            COLUMN_ANNOTATIONS_X + " INTEGER NOT NULL," +
            COLUMN_ANNOTATIONS_Y + " INTEGER NOT NULL," +
            COLUMN_ANNOTATIONS_COLOR + " INTEGER NOT NULL, " +
            COLUMN_FOREIGN_KEY_MEME + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + COLUMN_FOREIGN_KEY_MEME + ") " +
                       " REFERENCES MEME("+COLUMN_MEMES_ID +") );";

    MemeSQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_MEMES);
            db.execSQL(CREATE_TABLE_ANNOTATIONS);
        } catch (SQLException e) {

            Log.e(TAG, "Android SQLException caught" + e);
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}

