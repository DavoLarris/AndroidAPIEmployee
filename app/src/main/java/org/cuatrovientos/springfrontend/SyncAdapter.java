package org.cuatrovientos.springfrontend;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by David on 18/02/2017.
 */

public class SyncAdapter  extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;
    private ContentResolver contentResolver;
    private String contentUri = "content://myemployee.ddns.net:8080/springEmployDepart";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("PELLODEBUG", "SyncAdapter working for: " + account.name );
        int lastLocalId = 0;
        int lastBackendId = 0;
        Cursor cursor = null;

        try {

            /////////////////// UPDATE FROM BACKEND /////////////////////
            // Get Last backend_id locally
            cursor = provider.query(
                    Uri.parse(contentUri + "/tasks/last/backend"),   // The content URI of the words table
                    new String[]{"_id","task","id_backend","is_read"},
                    "",                        // The columns to return for each row
                    new String[]{""},                     // Selection criteria
                    "");
            if (cursor.getCount() > 0) {
                lastBackendId = cursor.getInt(2);
                Log.d("PELLODEBUG", "backend_id:" + cursor.getString(2));
            }
            Log.d("PELLODEBUG'", "Last backend Id: " + lastBackendId);

            // Get ToDo from the remote server
            List<Task> tasks =  backendAccess.getLast(lastBackendId);
            Log.d("PELLODEBUG", tasks.toString());
            for (Task task : tasks) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("task",task.getTask());
                contentValues.put("backend_id",task.getBackendId());

                // We finally make the request to the content provider
                Uri resultUri = provider.insert(
                        Uri.parse(contentUri),   // The content URI
                        contentValues
                );
                Log.d("PELLODEBUG","Inserted in local db: " + task.getTask());
            }
            /////////////////// UPDATE FROM LOCAL TO BACKEND
            // get all local record with id_backend = 0
            // send them to backend
            // update local id_backend with -1
            cursor = provider.query(
                    Uri.parse(contentUri + "/tasks/last/local"),   // The content URI of the words table
                    new String[]{"_id","task","id_backend","is_read"},
                    "",                        // The columns to return for each row
                    new String[]{""},                     // Selection criteria
                    "");
            if (cursor.getCount() > 0) {
                lastLocalId = cursor.getInt(0);
                Log.d("PELLODEBUG","Last local Id: " + cursor.getString(0));

                // TODO: send array of Tasks
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {

                    Task task = new Task();
                    task.setId(cursor.getInt(0));
                    task.setTask(cursor.getString(1));

                    backendAccess.insertTask(task);
                    Log.d("PELLODEBUG","Sent data to backend: " + task);

                    cursor.moveToNext();
                }
                // We finally make the request to the content provider
                // To mark local records as sent.
                // TODO, we should wait for the ACK from server
                int total = provider.update(
                        Uri.parse(contentUri),   // The content URI
                        null, "", new String[]{""}
                );
                Log.d("PELLODEBUG","Locally mark as sent " + total);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
