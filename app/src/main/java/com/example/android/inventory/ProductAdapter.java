package com.example.android.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    Context context;
    List<Product> products;
    private static final String TAG = "ProductAdapter";
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final Product currProduct = products.get(position);

        holder.txtvName.setText(currProduct.getmName());
        holder.txtvPrice.setText(currProduct.getmPrice());
        holder.txtvQuantity.setText(String.valueOf(currProduct.getmQuantity()));
        holder.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currProduct.getmQuantity() > 0)
                     currProduct.setmQuantity(currProduct.getmQuantity()-1);
                     holder.txtvQuantity.setText(String.valueOf(currProduct.getmQuantity()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
