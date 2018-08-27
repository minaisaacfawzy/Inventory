package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductContract.ProductEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText etxtName,etxtPrice,etxtQuantity,etxtSupplier;
    private Spinner spinnerCategory;
    private String category;
    private static final String TAG = "EditorActivity";
    private static final int PET_LOADER = 0;
    Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        etxtName = (EditText) findViewById(R.id.etxt_name);
        etxtPrice = (EditText) findViewById(R.id.etxt_price);
        etxtQuantity = (EditText) findViewById(R.id.etxt_quanitity);
        etxtSupplier = (EditText) findViewById(R.id.etxt_supplier);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);


        setupSpinner();
        uri =  getIntent().getData();
        if(uri == null){
            this.setTitle(getResources().getString(R.string.editor_activity_title_add));
        }else {
            this.setTitle(getResources().getString(R.string.editor_activity_title_edit));
            getLoaderManager().initLoader(0,null,this);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.homeAsUp:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinner(){
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options,android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(categorySpinnerAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection))
                {
                    if(selection.equals(getResources().getString(R.string.category_Beverages)))
                        category = getResources().getString(R.string.category_Beverages);
                    else if(selection.equals(getResources().getString(R.string.category_Diary)))
                        category = getResources().getString(R.string.category_Diary);
                    else if(selection.equals(getResources().getString(R.string.category_fresh_food)))
                        category = getResources().getString(R.string.category_fresh_food);
                    else if(selection.equals(getResources().getString(R.string.category_Frozen)))
                        category = getResources().getString(R.string.category_Frozen);
                    else if(selection.equals(getResources().getString(R.string.category_Poultry)))
                        category = getResources().getString(R.string.category_Poultry);
                    else
                        category = getResources().getString(R.string.category_Miscellaneous);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = getResources().getString(R.string.category_Miscellaneous);
            }
        });

    }

    private void saveProduct(){
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,etxtName.getText().toString().trim());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,etxtPrice.getText().toString().trim());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                Integer.parseInt(etxtQuantity.getText().toString().trim()));
        values.put(ProductContract.ProductEntry.COLUMN_RRODUCT_CATEGORY,category);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,etxtSupplier.getText().toString().trim());
        Log.i(TAG, "saveProduct: " + values.toString());

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI,values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_RRODUCT_CATEGORY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER
        };

        return new CursorLoader(this,
                uri,
                projection,
                null,
                null,
                null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String currentName,currentPrice,currentCategory,currentSupplier ;
        int currentQuantity,id;

        if(cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_CATEGORY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);


            id = cursor.getInt(idColumnIndex);
            currentName = cursor.getString(nameColumnIndex);
            currentPrice = cursor.getString(priceColumnIndex);
            currentQuantity = cursor.getInt(quantityColumnIndex);
            currentCategory = cursor.getString(categoryColumnIndex);
            currentSupplier = cursor.getString(supplierColumnIndex);
            Log.i(TAG, "populateData: " + id + " " + currentName + currentPrice + currentCategory + " " + currentSupplier);

            etxtName.setText(currentName);
            etxtPrice.setText(String.valueOf(currentPrice));
            etxtQuantity.setText(String.valueOf(currentQuantity));
            int index = getCategoreyNum(currentCategory);
            if(index >= 0)
                spinnerCategory.setSelection(index);
            else
                spinnerCategory.setSelection(0);
            etxtSupplier.setText(currentSupplier);
        }

    }

    private int getCategoreyNum(String category){
        String[] categories = getResources().getStringArray(R.array.array_category_options);
        for(int i = 0,n = categories.length; i < n; i++){
            if(categories[i].equals(category))
                return i;
        }
        return -1;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
