package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.inventory.data.InventoryContract.Entry;

/**
 * Created by cheah on 25/10/16.
 */

public class InventoryProvider extends ContentProvider{
    private InventoryDbHelper mDbHelper;
    public static final int CODE_TABLE_ALL = 100;
    public static final int CODE_TABLE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, CODE_TABLE_ALL);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY+"/#", CODE_TABLE_ID);
    }
    @Override
    public boolean onCreate(){
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,
            String sortOrder){
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_TABLE_ALL:
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CODE_TABLE_ID:
                Log.e("query","query 2");
                selection = Entry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_TABLE_ALL:
                return Entry.CONTENT_LIST_TYPE;
            case CODE_TABLE_ID:
                return Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri,ContentValues values){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_TABLE_ALL:
                // If there are no values to update, then don't try to update the database
                if (values.size() == 0) {
                    return null;
                }
                if (values.containsKey(Entry.COLUMN_PART_NAME)) {
                    String name = values.getAsString(Entry.COLUMN_PART_NAME);
                    if (TextUtils.isEmpty(name)) {
                        throw new IllegalArgumentException("name cannot be empty");
                    }
                }

                if (values.containsKey(Entry.COLUMN_QUANTITY)) {
                    Integer val = values.getAsInteger(Entry.COLUMN_QUANTITY);
                    if (val==null || val==0) {
                        throw new IllegalArgumentException("quantity cannot be empty");
                    }
                }

                if (values.containsKey(Entry.COLUMN_UNIT_PRICE)) {
                    String name = values.getAsString(Entry.COLUMN_UNIT_PRICE);
                    if (TextUtils.isEmpty(name)) {
                        throw new IllegalArgumentException("price cannot be empty");
                    }
                }

                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                // Insert the new pet with the given values
                long id = database.insert(Entry.TABLE_NAME, null, values);
                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (id == -1) {
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_TABLE_ID:
                update(uri,values,null,null);
                return null;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match) {
            case CODE_TABLE_ALL:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_TABLE_ID:
                // Delete a single row given by the ID in the URI
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_TABLE_ALL:
                return updateHabit(uri, values, selection, selectionArgs);
            case CODE_TABLE_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateHabit(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateHabit(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return -1;
        }
        if (values.containsKey(Entry.COLUMN_PART_NAME)) {
            String name = values.getAsString(Entry.COLUMN_PART_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name cannot be empty");
            }
        }

        if (values.containsKey(Entry.COLUMN_QUANTITY)) {
            Integer val = values.getAsInteger(Entry.COLUMN_QUANTITY);
            if (val==null) {
                throw new IllegalArgumentException("quantity cannot be empty");
            }
        }

        if (values.containsKey(Entry.COLUMN_UNIT_PRICE)) {
            String name = values.getAsString(Entry.COLUMN_UNIT_PRICE);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("price cannot be empty");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new pet with the given values
        int rowsUpdated = database.update(Entry.TABLE_NAME, values ,selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
