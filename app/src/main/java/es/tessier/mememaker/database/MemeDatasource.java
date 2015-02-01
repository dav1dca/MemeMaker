package es.tessier.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;

import es.tessier.mememaker.database.MemeContract.AnnotationsEntry;
import es.tessier.mememaker.database.MemeContract.MemesEntry;
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
                MemesEntry.TABLE_NAME,
                new String[]{MemesEntry.COLUMN_NAME, BaseColumns._ID, MemesEntry.COLUMN_ASSET},
                null, // selection
                null, // selection Args
                null, //Group by
                null, // Having
                MemesEntry.COLUMN_CREATE_DATE + " DESC"  //OrderBy
        );

        ArrayList<Meme> memes = new ArrayList<Meme>();

        if (cursor.moveToFirst()) {
            do {
                Meme meme = new Meme(getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, MemesEntry.COLUMN_ASSET),
                        getStringFromColumnName(cursor, MemesEntry.COLUMN_NAME),
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

            cursor = database.rawQuery("SELECT * FROM " + AnnotationsEntry.TABLE_NAME +
                    " WHERE " + AnnotationsEntry.COLUMN_FK_MEME + " = " + meme.getId(), null);

            if (cursor.moveToFirst()) {
                do {
                    annotation = new MemeAnnotation(getIntFromColumnName(cursor, BaseColumns._ID),
                            getStringFromColumnName(cursor, AnnotationsEntry.COLUMN_COLOR),
                            getStringFromColumnName(cursor, AnnotationsEntry.COLUMN_TITLE),
                            getIntFromColumnName(cursor, AnnotationsEntry.COLUMN_Y),
                            getIntFromColumnName(cursor, AnnotationsEntry.COLUMN_X)
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
        memeValues.put(MemesEntry.COLUMN_NAME, meme.getName());
        memeValues.put(MemesEntry.COLUMN_ASSET, meme.getAssetLocation());
        memeValues.put(MemesEntry.COLUMN_CREATE_DATE, new Date().getTime());

        long memeId = database.insert(MemesEntry.TABLE_NAME, null, memeValues);

        for (MemeAnnotation memeAnnotation : meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(AnnotationsEntry.COLUMN_TITLE, memeAnnotation.getTitle());
            annotationValues.put(AnnotationsEntry.COLUMN_X, memeAnnotation.getLocationX());
            annotationValues.put(AnnotationsEntry.COLUMN_Y, memeAnnotation.getLocationY());
            annotationValues.put(AnnotationsEntry.COLUMN_COLOR, memeAnnotation.getColor());
            annotationValues.put(AnnotationsEntry.COLUMN_FK_MEME, memeId);

            database.insert(AnnotationsEntry.TABLE_NAME, null, annotationValues);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }



    public void update(Meme meme)    {

        SQLiteDatabase database = openWriteable();
        database.beginTransaction();

        ContentValues updateMemeValues = new ContentValues();

        updateMemeValues.put(MemesEntry.COLUMN_NAME, meme.getName());

        database.update(MemesEntry.TABLE_NAME,
                updateMemeValues,
                String.format("%s=%d", BaseColumns._ID, meme.getId()),
                null);

        for (MemeAnnotation memeAnnotation : meme.getAnnotations()) {
            ContentValues updateAnnotation = new ContentValues();
            updateAnnotation.put(AnnotationsEntry.COLUMN_TITLE, memeAnnotation.getTitle());
            updateAnnotation.put(AnnotationsEntry.COLUMN_X, memeAnnotation.getLocationX());
            updateAnnotation.put(AnnotationsEntry.COLUMN_Y, memeAnnotation.getLocationY());
            updateAnnotation.put(AnnotationsEntry.COLUMN_COLOR, memeAnnotation.getColor());
            updateAnnotation.put(AnnotationsEntry.COLUMN_FK_MEME, meme.getId());

            if(memeAnnotation.hasBeenSaved()) {
                database.update(AnnotationsEntry.TABLE_NAME,
                        updateAnnotation,
                        String.format("%s=%d", AnnotationsEntry.COLUMN_FK_MEME, memeAnnotation.getId()),
                        null);
            }
            else{
                database.insert(AnnotationsEntry.TABLE_NAME, null, updateAnnotation);

            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }


    public void delete(int memeId){
        SQLiteDatabase database = openWriteable();
        database.beginTransaction();

        database.delete(AnnotationsEntry.TABLE_NAME,
                String.format("%s=%d", AnnotationsEntry.COLUMN_FK_MEME, memeId),
                null);

        database.delete(MemesEntry.TABLE_NAME,
                String.format("%s=%d", BaseColumns._ID, memeId),
                null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }


}



