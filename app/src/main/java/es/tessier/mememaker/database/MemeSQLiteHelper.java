package es.tessier.mememaker.database;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import es.tessier.mememaker.database.MemeContract.MemesEntry;
import es.tessier.mememaker.database.MemeContract.AnnotationsEntry;


public class MemeSQLiteHelper extends SQLiteOpenHelper {

    public final static String DB_NAME ="memes.db";
    public final static int DB_VERSION = 2;
    public static final String TAG = MemeSQLiteHelper.class.getName();

    public static final String CREATE_TABLE_MEMES = "CREATE TABLE " + MemesEntry.TABLE_NAME + " ( "+
            MemesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MemesEntry.COLUMN_ASSET + " TEXT NOT NULL," +
            MemesEntry.COLUMN_NAME + " TEXT NOT NULL,\" +" +
            MemesEntry.COLUMN_CREATE_DATE + " INTEGER );";

    static final String CREATE_TABLE_ANNOTATIONS = "CREATE TABLE " + AnnotationsEntry.TABLE_NAME + " ( "+
            AnnotationsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AnnotationsEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            AnnotationsEntry.COLUMN_X + " INTEGER NOT NULL," +
            AnnotationsEntry.COLUMN_Y + " INTEGER NOT NULL," +
            AnnotationsEntry.COLUMN_COLOR + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + AnnotationsEntry.COLUMN_FK_MEME + ") " +
                       " REFERENCES MEME("+MemesEntry.COLUMN_ID +") );";

    static final String ALTER_ADD_CREATE_DATE ="ALTER TABLE "+ MemesEntry.TABLE_NAME +
                                                " ADD COLUMN "+ MemesEntry.COLUMN_CREATE_DATE+ " INTEGER ;";

    public MemeSQLiteHelper(Context context){

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
        switch (oldVersion){
            case 1: db.execSQL(ALTER_ADD_CREATE_DATE );
        }
    }



}

