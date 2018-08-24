package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyProductCursorAdapter extends CursorRecyclerViewAdapter<MyProductCursorAdapter.ViewHolder> {

    public MyProductCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout parent;
        TextView txtvName,txtvPrice,txtvQuantity;
        Button btnSell;
        public ViewHolder(View view) {
            super(view);
            parent = itemView.findViewById(R.id.recycler_item_parent);
            txtvName = itemView.findViewById(R.id.txtv_product_name);
            txtvPrice = itemView.findViewById(R.id.txtv_price);
            txtvQuantity = itemView.findViewById(R.id.txtv_quantity);
            btnSell = itemView.findViewById(R.id.btn_sell);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, Cursor cursor) {
        final Product currProduct = Product.fromCursor(cursor);

        viewHolder.txtvName.setText(currProduct.getmName());
        viewHolder.txtvPrice.setText(currProduct.getmPrice());
        viewHolder.txtvQuantity.setText(String.valueOf(currProduct.getmQuantity()));
        viewHolder.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currProduct.getmQuantity() > 0)
                    currProduct.setmQuantity(currProduct.getmQuantity()-1);
                viewHolder.txtvQuantity.setText(String.valueOf(currProduct.getmQuantity()));
            }
        });

    }
}
