package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText etxtName,etxtPrice,etxtQuantity,etxtSupplier;
    private Button btnIncreaseQuantity, btnDecreaseQuantity,btnDel,btnOrder;
    private ImageView imgProduct;
    private Spinner spinnerCategory;
    private String category;
    private static final String TAG = "EditorActivity";
    private static final int PET_LOADER = 0;
    private boolean productChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productChanged = true;
            return false;
        }
    };
    Uri uri;
    int currentQuantity,mOrderQuantity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        uri =  getIntent().getData();
        if(uri == null){
            this.setTitle(getResources().getString(R.string.editor_activity_title_add));
            invalidateOptionsMenu();
        }else {
            this.setTitle(getResources().getString(R.string.editor_activity_title_edit));
            getLoaderManager().initLoader(0,null,this);
        }
        etxtName = (EditText) findViewById(R.id.etxt_name);
        etxtPrice = (EditText) findViewById(R.id.etxt_price);
        etxtQuantity = (EditText) findViewById(R.id.etxt_quanitity);
        etxtSupplier = (EditText) findViewById(R.id.etxt_supplier);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        imgProduct = findViewById(R.id.img_product);
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
        etxtName.setOnTouchListener(mTouchListener);
        etxtQuantity.setOnTouchListener(mTouchListener);
        etxtPrice.setOnTouchListener(mTouchListener);
        etxtSupplier.setOnTouchListener(mTouchListener);
        spinnerCategory.setOnTouchListener(mTouchListener);

        btnDecreaseQuantity = findViewById(R.id.btn_editor_sub);
        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderQuantityDialog(false);
            }
        });

        btnIncreaseQuantity = findViewById(R.id.btn_editor_add);
        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderQuantityDialog(true);
            }
        });

        btnDel = findViewById(R.id.btn_editor_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        if(uri == null)
            btnDel.setVisibility(View.GONE);

        btnOrder = findViewById(R.id.btn_editor_order);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupSpinner();
        //uri is null in case of adding new product


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getExtras() != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgProduct.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//
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
            case android.R.id.home:
                if (!productChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
        String name = etxtName.getText().toString().trim();
        String price = etxtPrice.getText().toString().trim();
        String quantity = etxtQuantity.getText().toString().trim();
        String supplier = etxtSupplier.getText().toString().trim();
        imgProduct.setDrawingCacheEnabled(true);
        Bitmap pic = imgProduct.getDrawingCache(true);
        byte[] picBytes  = null;
        if(pic != null) {
            picBytes = getBitmapAsByteArray(pic);
        }

        if(uri == null && TextUtils.isEmpty(name)&& TextUtils.isEmpty(price)
                && TextUtils.isEmpty(String.valueOf(quantity))&& TextUtils.isEmpty(supplier))
            return;
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,name);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,price);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,Integer.parseInt(quantity));
        values.put(ProductContract.ProductEntry.COLUMN_RRODUCT_CATEGORY,category);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,supplier);
        values.put(ProductEntry.COLUMN_RRODUCT_PICTURE, picBytes);


        if(uri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsAffected = getContentResolver().update(uri,values,null,null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

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
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_RRODUCT_PICTURE
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
        byte[] picBytes;
        int id;

        if(cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_CATEGORY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int picColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_PICTURE);

            id = cursor.getInt(idColumnIndex);
            currentName = cursor.getString(nameColumnIndex);
            currentPrice = cursor.getString(priceColumnIndex);
            currentQuantity = cursor.getInt(quantityColumnIndex);
            currentCategory = cursor.getString(categoryColumnIndex);
            currentSupplier = cursor.getString(supplierColumnIndex);
            picBytes = cursor.getBlob(picColumnIndex);
            Log.i(TAG, "populateData: " + id + " " + currentName + currentPrice + currentCategory + " " + currentSupplier);

            etxtName.setText(currentName);
            etxtPrice.setText(String.valueOf(currentPrice));
            etxtQuantity.setText(String.valueOf(currentQuantity));
            if(picBytes != null) {
                Bitmap pic = BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length);
                imgProduct.setImageBitmap(pic);
            }
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

    @Override
    public void onBackPressed() {
        if(!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener onClickListener  = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(onClickListener);

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null)
                    dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showOrderQuantityDialog(final boolean add){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(EditorActivity.this.getResources().getString(R.string.alert_dialog_order_quantity));

         // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOrderQuantity = Integer.parseInt(input.getText().toString());
                ContentValues value = new ContentValues();
                if(add) {
                    value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity + mOrderQuantity);
                    getContentResolver().update(uri, value, null, null);
                }else {
                    value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,currentQuantity-mOrderQuantity);
                    if(currentQuantity - mOrderQuantity > 0)
                        getContentResolver().update(uri,value,null,null);
                    else
                        Toast.makeText(EditorActivity.this,getResources().getString(R.string.cannot_sell),Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteProduct() {

        if(uri != null) {
            int rowsDeleted = getContentResolver().delete(uri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
