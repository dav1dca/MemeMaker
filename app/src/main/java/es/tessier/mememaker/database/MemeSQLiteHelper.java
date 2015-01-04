package es.tessier.mememaker.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemeSQLiteHelper extends SQLiteOpenHelper {

    final static String DB_NAME ="memes.db";
    final static int DB_VERSION = 1;
    static final String TAG = MemeSQLiteHelper.class.getName();
    //Meme Table functionality
    static final String MEMES_TABLE = "memes";
    static final String COLUMN_MEMES_ASSET = "asset";
    static final String COLUMN_MEMES_NAME = "name";
    static final String CREATE_TABLE_MEMES = "_id INTEGER PRIMARY KEY AUTOINCREMENT "+
            COLUMN_MEMES_ASSET + " TEXT," +
            COLUMN_MEMES_NAME + " TEXT);";

    //Meme Table Annotations functionality
    static final String ANNOTATIONS_TABLE = "annotations";
    static final String COLUMN_ANNOTATIONS_NAME = "name";

    MemeSQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}

