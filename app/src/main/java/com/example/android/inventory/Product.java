package com.example.android.inventory;

import android.database.Cursor;
import android.graphics.Bitmap;
import com.example.android.inventory.data.ProductContract.ProductEntry;
public class Product {
    private String mName;
    private String mPrice;
    private int mQuantity;
    private Bitmap mPicture;
    private String mSupplier;
    private String mCategory;

    public Product(String mName, String mPrice, int mQuantity) {
        this.mName = mName;
        this.mPrice = mPrice;
        this.mQuantity = mQuantity;
    }

    public Product(String name, String price, int quantity, Bitmap picture, String supplier,String category) {
        this.mName = name;
        this.mPrice = price;
        this.mQuantity = quantity;
        this.mPicture = picture;
        this.mSupplier = supplier;
        this.mCategory = category;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public Bitmap getmPicture() {
        return mPicture;
    }

    public void setmPicture(Bitmap mPicture) {
        this.mPicture = mPicture;
    }

    public String getmSupplier() {
        return mSupplier;
    }

    public void setmSupplier(String mSupplier) {
        this.mSupplier = mSupplier;
    }

    public static Product fromCursor(Cursor cursor){
        Product product;
        String currentName = null,currentPrice = null;
        int currentQuantity = 0;
        try {

            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_CATEGORY);

            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                currentName = cursor.getString(nameColumnIndex);
                currentPrice = cursor.getString(priceColumnIndex);
                currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentCategory = cursor.getString(categoryColumnIndex);


            }
        }finally {
            cursor.close();
        }
        product = new Product(currentName, currentPrice, currentQuantity);
        return product;
    }
}
