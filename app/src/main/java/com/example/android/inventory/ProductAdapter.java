package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.inventory.data.ProductContract.ProductEntry;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    Context context;
    Cursor cursor;
    private static final String TAG = "ProductAdapter";
    public ProductAdapter(Context context, Cursor products) {
        this.context = context;
        this.cursor = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onViewRecycled(@NonNull ProductViewHolder holder) {
        super.onViewRecycled(holder);
        cursor.close();
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {

        cursor.moveToPosition(position);
        int id = 0;
        String currentName = null,currentPrice = null,currentCategory = null;
        int currentQuantity = 0;


            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_CATEGORY);
            Product currProduct;

             id = cursor.getInt(idColumnIndex);
             currentName = cursor.getString(nameColumnIndex);
             currentPrice = cursor.getString(priceColumnIndex);
             currentQuantity = cursor.getInt(quantityColumnIndex);
             currentCategory = cursor.getString(categoryColumnIndex);

            holder.txtvName.setText(currentName);
            holder.txtvPrice.setText(currentPrice);
            holder.txtvQuantity.setText(String.valueOf(currentQuantity));
            holder.btnSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });





    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        this.cursor = cursor;
    }
}
