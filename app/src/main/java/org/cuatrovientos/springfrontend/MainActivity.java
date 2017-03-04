package org.cuatrovientos.springfrontend;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.cuatrovientos.springfrontend.Model.Employee;
import org.cuatrovientos.springfrontend.authority.Authenticator;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private String contentUri = "content://org.cuatrovientos.springfrontend.sqlcommand";
    private CustomListAdapter customizedListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentResolver contentResolver;
    private Account account;
    private ArrayList<Long> employeesSelected = new ArrayList<Long>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        setupCustomList(null);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.

        getLoaderManager().initLoader(0, null, this);

        account = CreateSyncAccount(this);

        String authority = "org.cuatrovientos.springfrontend.sqlcommand";

        // Simple option, will handle everything smartly
        contentResolver = getContentResolver();
        Bundle bundle = new Bundle();
        contentResolver.requestSync(account, authority, bundle);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, addActivity.class);
                startActivity(intent);
            }
        });

        // Listener for the swipe down gesture
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        MainActivity.this.syncNow(getWindow().getDecorView().getRootView());
                    }
                }
        );
    }

    private void setupCustomList(Cursor cursor) {

        customizedListAdapter = new CustomListAdapter(this, cursor);

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(customizedListAdapter);
        registerForContextMenu(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                count = count+1;
                mode.setTitle(count + " items selected");
                employeesSelected.add(id);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.my_context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_id:
                        for (int i = 0; i<employeesSelected.size(); i++){
                            getContentResolver().delete(
                                    Uri.parse(contentUri + "/delete/employees"),
                                    null,
                                    new String[]{String.valueOf(employeesSelected.get(i))}
                        );
                        }
                        Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT);
                        count = 0;
                        employeesSelected = null;
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    public void syncNow(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
        // Simple option, will handle everything smartly
        contentResolver = getContentResolver();
        contentResolver.requestSync(account, "org.cuatrovientos.springfrontend.sqlcommand", bundle);

        // Stop refresh effect
        Snackbar.make(view, "Done!", Snackbar.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;

        baseUri = Uri.parse(this.contentUri + "/employees");


        return new CursorLoader(this, baseUri,  // The content URI of the words table
                null,               // The columns to return for each row
                null,                        // Selection criteria parameters
                null,                     // Selection criteria values
                null);                            // The sort order for the returned rows

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        customizedListAdapter.swapCursor(cursor);
        cursor.moveToFirst();
        String data = "";
        while (!cursor.isAfterLast()) {
            data += "\n" + cursor.getString(1);
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        customizedListAdapter.swapCursor(null);
    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
}
