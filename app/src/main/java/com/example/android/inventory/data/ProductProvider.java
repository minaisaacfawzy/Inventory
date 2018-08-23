package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Switch;

import com.example.android.inventory.data.ProductContract.ProductEntry;

/**
 * {@link ContentProvider} for inventory app.
 */

public class ProductProvider extends ContentProvider {
    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.inventory/products" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS,  PRODUCTS);

        // The content URI of the form "content://com.example.android.inventory/products/#" will map to the
        // integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.inventory/products/3" matches, but
        // "content://com.example.android.inventory/products" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the PRODUCTS table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the PRODUCTS table.
                // TODO: Perform database query on products table
                cursor = database.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder

                );
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventory/products/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if(name == null)
            throw new IllegalArgumentException("Product requires a name");

        String price = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
        if(name == null )
            throw new IllegalArgumentException("Product requires a price");

        int quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if(quantity < 0)
            throw new IllegalArgumentException("quantity should be greater than zero");


        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);
        if(id == -1)
            return  null;
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
            case PRODUCT_ID:
                long id = ContentUris.parseId(uri);
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(id)};
                return database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw  new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri,values,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                long id = ContentUris.parseId(uri);
                selectionArgs = new String[]{String.valueOf(id)};
                return updateProduct(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)){
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if(name == null)
                throw new IllegalArgumentException("Product name is not valid");
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)){
            String price = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
            if(price == null)
                throw new IllegalArgumentException("Product price is not valid");
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)){
            int quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if(quantity < 0)
                throw new IllegalArgumentException("Product quantity is not valid");
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER)){
            String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if(supplier == null)
                throw new IllegalArgumentException("Product supplier is not valid");
        }

        if(values.size() == 0)
            return 0;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(ProductEntry.TABLE_NAME,values,selection,selectionArgs);
    }
}
