package com.example.android.inventory;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    ConstraintLayout parent;
    TextView txtvName,txtvPrice,txtvQuantity;
    Button btnSell;
    int tagId;
    public ProductViewHolder(View itemView) {
        super(itemView);

        parent = itemView.findViewById(R.id.recycler_item_parent);
        txtvName = itemView.findViewById(R.id.txtv_product_name);
        txtvPrice = itemView.findViewById(R.id.txtv_price);
        txtvQuantity = itemView.findViewById(R.id.txtv_quantity);
        btnSell = itemView.findViewById(R.id.btn_sell);
    }

    public int getTagId() {
        return tagId;
    }
    //set an id for each view which is equivalent to the id in database
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
