package es.tessier.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;

import es.tessier.mememaker.models.Meme;
import es.tessier.mememaker.models.MemeAnnotation;


public class MemeDatasource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {
        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper(mContext);

    }

    public SQLiteDatabase openReadable() {
        return mMemeSqlLiteHelper.getReadableDatabase();
    }


    public SQLiteDatabase openWriteable() {
        return mMemeSqlLiteHelper.getWritableDatabase();
    }

    public void close(SQLiteDatabase database) {
        database.close();
    }

    public ArrayList<Meme> read() {

        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);

        return memes;
    }

    public ArrayList<Meme> readMemes() {
        SQLiteDatabase database = openReadable();

        Cursor cursor = database.query(
                MemeSQLiteHelper.MEMES_TABLE,
                new String[]{MemeSQLiteHelper.COLUMN_MEMES_NAME, BaseColumns._ID, MemeSQLiteHelper.COLUMN_MEMES_ASSET},
                null, // selection
                null, // selection Args
                null, //Group by
                null, // Having
                null  //OrderBy
        );

        ArrayList<Meme> memes = new ArrayList<Meme>();

        if (cursor.moveToFirst()) {
            do {
                Meme meme = new Meme(getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEMES_ASSET),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEMES_NAME),
                        null);

                memes.add(meme);
            }
            while (cursor.moveToNext());



        }
        cursor.close();
        database.close();

        return memes;

    }

    public void addMemeAnnotations(ArrayList<Meme> memes) {
        SQLiteDatabase database = openReadable();
        ArrayList<MemeAnnotation> annotations;
        Cursor cursor;
        MemeAnnotation annotation;

        for (Meme meme : memes) {
            annotations = new ArrayList<MemeAnnotation>();

            cursor = database.rawQuery("SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE +
                    " WHERE " + MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME + " = " + meme.getId(), null);

            if (cursor.moveToFirst()) {
                do {
                    annotation = new MemeAnnotation(getIntFromColumnName(cursor, BaseColumns._ID),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATIONS_COLOR),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATIONS_TITLE),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATIONS_Y),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATIONS_X)
                    );


                    annotations.add(annotation);

                } while (cursor.moveToNext());

                meme.setAnnotations(annotations);
                cursor.close();
            }
        }

        database.close();

    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }


    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }


    public void create(Meme meme) {

        SQLiteDatabase database = openWriteable();
        database.beginTransaction();
        ContentValues memeValues = new ContentValues();
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_NAME, meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_ASSET, meme.getAssetLocation());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEMES_NAME, meme.getName());

        long memeId = database.insert(MemeSQLiteHelper.MEMES_TABLE, null, memeValues);

        for (MemeAnnotation memeAnnotation : meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_TITLE, memeAnnotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_X, memeAnnotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_Y, memeAnnotation.getLocationY());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATIONS_COLOR, memeAnnotation.getColor());
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeId);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationValues);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }
}



