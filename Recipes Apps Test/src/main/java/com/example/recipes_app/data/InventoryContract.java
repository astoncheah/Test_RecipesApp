package com.example.recipes_app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract{

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    // Creates a UriMatcher object.
    static final String CONTENT_AUTHORITY = "com.example.recipes_app";
    static final String PATH_RECIPE = "recipeType";
    public static final String PATH_RECIPE_QUERY_NAME = "recipeType/name";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECIPE);

    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class Entry implements BaseColumns{
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public final static String TABLE_NAME = "RecipesApp";

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
        public final static String COLUMN_RECIPE_NAME ="recipe_name";

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
