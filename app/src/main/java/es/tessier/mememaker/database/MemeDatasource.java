package es.tessier.mememaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class MemeDatasource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {
        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper(mContext);

        SQLiteDatabase database = mMemeSqlLiteHelper.getReadableDatabase();
    }
}

