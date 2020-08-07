package com.example.covapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_ContactTracer extends BaseAdapter {
    private final String[] phoneList;
    Context context;
    String[] nameList = new ArrayList<String>().toArray(new String[0]);
    //String[] phoneList = new ArrayList<String>().toArray(new String[0]);

   // String nameList[];
    //String phoneList[];
    LayoutInflater inflter;



    public CustomAdapter_ContactTracer(Context applicationContext, ArrayList<String> nameList, ArrayList<String> phoneList) {
        this.context = context;
        this.nameList = nameList.toArray(new String[0]);
        this.phoneList = phoneList.toArray(new String[0]);
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return nameList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_items, null);
        TextView fname = (TextView) view.findViewById(R.id.list_name);
        TextView phone = (TextView) view.findViewById(R.id.list_phone_number);
        fname.setText(nameList[i]);
        phone.setText(phoneList[i]);
        return view;
    }
}