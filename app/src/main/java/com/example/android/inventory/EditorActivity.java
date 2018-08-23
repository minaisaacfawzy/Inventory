package com.example.android.inventory;

import android.content.ContentValues;
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


public class EditorActivity extends AppCompatActivity {
    private EditText etxtName,etxtPrice,etxtQuantity,etxtSupplier;
    private Spinner spinnerCategory;
    private String category;
    private static final String TAG = "EditorActivity";
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



}
