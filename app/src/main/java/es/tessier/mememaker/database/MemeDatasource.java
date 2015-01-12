package es.tessier.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import es.tessier.mememaker.models.Meme;
import es.tessier.mememaker.models.MemeAnnotation;


public class MemeDatasource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {
        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper(mContext);

    }

    public SQLiteDatabase openReadable(){
        return  mMemeSqlLiteHelper.getReadableDatabase();
    }

    public SQLiteDatabase openWriteable(){
        return  mMemeSqlLiteHelper.getWritableDatabase();
    }

    public void close(SQLiteDatabase database){
        database.close();
    }

    public void create(Meme meme){

        SQLiteDatabase database = openWriteable();
        database.beginTransaction();
        ContentValues memeValues = new ContentValues();
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_NAME,meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_ASSET,meme.getAssetLocation());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_NAME,meme.getName());

        long memeId = database.insert(MemeSQLiteHelper.MEMES_TABLE,null,memeValues);

        for(MemeAnnotation memeAnnotation: meme.getAnnotations()){
            ContentValues annotationValues = new ContentValues();
            memeValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_TITLE,memeAnnotation.getTitle());
            memeValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_X,memeAnnotation.getLocationX());
            memeValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_Y,memeAnnotation.getLocationY());
            memeValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_COLOR,memeAnnotation.getColor());
            memeValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME,memeId);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,null,memeValues);
        }

        database.setTransactionSuccessful();
        close(database);

    }
}



