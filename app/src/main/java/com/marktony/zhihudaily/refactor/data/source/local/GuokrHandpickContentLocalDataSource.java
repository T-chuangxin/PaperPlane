package com.marktony.zhihudaily.refactor.data.source.local;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.marktony.zhihudaily.refactor.data.GuokrHandpickContentResult;
import com.marktony.zhihudaily.refactor.data.source.datasource.GuokrHandpickContentDataSource;
import com.marktony.zhihudaily.refactor.database.AppDatabase;
import com.marktony.zhihudaily.refactor.database.DatabaseCreator;

/**
 * Created by lizhaotailang on 2017/5/26.
 */

public class GuokrHandpickContentLocalDataSource implements GuokrHandpickContentDataSource {

    @Nullable
    private static GuokrHandpickContentLocalDataSource INSTANCE = null;

    @Nullable
    private AppDatabase mDb = null;

    private GuokrHandpickContentLocalDataSource(@NonNull Context context) {
        DatabaseCreator creator = DatabaseCreator.getInstance();
        if (!creator.isDatabaseCreated()) {
            creator.createDb(context);
        }
        mDb = creator.getDatabase();
    }

    public static GuokrHandpickContentLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GuokrHandpickContentLocalDataSource(context);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getGuokrHandpickContent(int id, @NonNull LoadGuokrHandpickContentCallback callback) {
        if (mDb == null) {
            callback.onDataNotAvailable();
            return;
        }

        new AsyncTask<Void, Void, GuokrHandpickContentResult>() {

            @Override
            protected GuokrHandpickContentResult doInBackground(Void... voids) {
                return mDb.guokrHandpickContentDao().loadGuokrHandpickNewsItem(id);
            }

            @Override
            protected void onPostExecute(GuokrHandpickContentResult content) {
                super.onPostExecute(content);
                if (content == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onContentLoaded(content);
                }
            }
        }.execute();
    }

    @Override
    public void favorite(boolean favorite) {

    }

    @Override
    public void saveContent(@NonNull GuokrHandpickContentResult content) {
        if (mDb != null) {
            mDb.beginTransaction();
            try {
                mDb.guokrHandpickContentDao().saveContent(content);
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
        }
    }
}