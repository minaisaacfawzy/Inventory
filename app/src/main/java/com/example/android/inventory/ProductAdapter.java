package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        String currentName,currentPrice,currentCategory ;
        final int currentQuantity;


            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_RRODUCT_CATEGORY);


             id = cursor.getInt(idColumnIndex);
             currentName = cursor.getString(nameColumnIndex);
             currentPrice = cursor.getString(priceColumnIndex);
             currentQuantity = cursor.getInt(quantityColumnIndex);

             currentCategory = cursor.getString(categoryColumnIndex);

            holder.setTagId(id);
            holder.txtvName.setText(currentName);
            holder.txtvPrice.setText(currentPrice);
            holder.txtvQuantity.setText(String.valueOf(currentQuantity));
            holder.btnSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = holder.getTagId();
                    Uri uri = Uri.withAppendedPath(ProductEntry.CONTENT_URI,String.valueOf(id));
                    ContentValues value = new ContentValues();
                    value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,currentQuantity-1);
                    if(currentQuantity > 0)
                        context.getContentResolver().update(uri,value,null,null);
                    else
                        Toast.makeText(context,context.getResources().getString(R.string.cannot_sell),Toast.LENGTH_LONG).show();

                }
            });
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,EditorActivity.class);
                    int id = holder.getTagId();
                    Uri uri = Uri.withAppendedPath(ProductEntry.CONTENT_URI,String.valueOf(id));
                    intent.setData(uri);
                    context.startActivity(intent);

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
