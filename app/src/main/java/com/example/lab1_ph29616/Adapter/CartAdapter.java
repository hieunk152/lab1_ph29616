package com.example.lab1_ph29616.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.lab1_ph29616.DTO.CatDTO;
import com.example.lab1_ph29616.R;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    Context context;
    ArrayList<CatDTO> listCat;

    public CartAdapter(Context context, ArrayList<CatDTO> listCat) {
        this.context = context;
        this.listCat = listCat;
//        this.id = id;
    }


    @Override
    public int getCount() {
        return listCat.size();
    }

    @Override
    public Object getItem(int i) {
        return listCat.get(i);
    }
    @Override
    public long getItemId(int i) {
        return listCat.get(i).getId();
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        View row;
        if (v != null)
            row = v;
        else {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.item_list_cart,null);
        }
        CatDTO objCat = listCat.get(i);
        TextView tvId = row.findViewById(R.id.tv_id);
        TextView tvName = row.findViewById(R.id.tv_name);

        tvId.setText(i+1 + "");
        tvName.setText(objCat.getName());

        return row;
    }
}
