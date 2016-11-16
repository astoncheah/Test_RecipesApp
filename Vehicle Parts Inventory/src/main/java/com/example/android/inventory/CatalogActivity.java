/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.Entry;
import com.example.android.inventory.data.InventoryCursorAdaptor;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER = 0;
    private InventoryCursorAdaptor habitCursorAdaptor;

    /** Database helper that will provide us access to the database */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        View emptyView = findViewById(R.id.empty_view);
        ListView displayView = (ListView)findViewById(R.id.list_view_habit);
        displayView.setEmptyView(emptyView);

        habitCursorAdaptor = new InventoryCursorAdaptor(CatalogActivity.this,null);
        displayView.setAdapter(habitCursorAdaptor);
        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI,id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(URL_LOADER,null,this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        return new CursorLoader(
                CatalogActivity.this,   // Parent activity context
                InventoryContract.CONTENT_URI,        // Table to query
                null,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null);             // Default sort order
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor data){
        habitCursorAdaptor.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        habitCursorAdaptor.swapCursor(null);
    }
    private void addNew() {
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_PART_NAME,getString(R.string.oil_filter));
        values.put(Entry.COLUMN_QUANTITY,"2");
        values.put(Entry.COLUMN_UNIT_PRICE,"199.99");
        Uri newUri = getContentResolver().insert(InventoryContract.CONTENT_URI,values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.new_record_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.new_record_saved)+": "+ newUri.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalog, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    final String where = Entry.COLUMN_PART_NAME+" = ?";
                    final String[] args = new String[]{query.trim()};
                    Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI,null,where,args,null);
                    habitCursorAdaptor.swapCursor(cursor);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener(){
            @Override
            public boolean onClose(){
                Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI,null,null,null,null);
                habitCursorAdaptor.swapCursor(cursor);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                addNew();
                return true;
            case R.id.action_delete_all_entries:
                getContentResolver().delete(InventoryContract.CONTENT_URI,null,null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
