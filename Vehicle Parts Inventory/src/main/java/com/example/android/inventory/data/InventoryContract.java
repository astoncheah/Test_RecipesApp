package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cheah on 24/10/16.
 */

public class InventoryContract{

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    // Creates a UriMatcher object.
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final String PATH_INVENTORY = "inventory";
    public static final String PATH_INVENTORY_QUERY_NAME = "inventory/name";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class Entry implements BaseColumns{
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "HabitTracker";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * do exercise 1 hour per day
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PART_NAME ="part_name";

        /**
         * drink at least 7 litre water per day
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * lear a new word per day
         * type: TEXT
         */
        public final static String COLUMN_UNIT_PRICE = "unit_price";

        public static final int NO = 0;
        public static final int YES = 1;
    }
}
