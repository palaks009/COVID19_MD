package com.example.covapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.covapp.R;

import java.util.ArrayList;

class CustomAdapterTestingCenter extends BaseAdapter {

    Context applicationContext;
    String[] nameList;
    String[] locationList ;
    String[] countyList;
    String[] infoList;
    LayoutInflater inflter;


    public CustomAdapterTestingCenter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> locationList,
                                      ArrayList<String> countyList, ArrayList<String> infoList)
    {
        this.applicationContext = applicationContext;
        this.nameList = nameList.toArray(new String[0]);
        this.locationList = locationList.toArray(new String[0]);
        this.countyList = countyList.toArray(new String[0]);
        this.infoList = infoList.toArray(new String[0]);
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

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.testingcenter_listitem, null);
        TextView name = (TextView) view.findViewById(R.id.list_testing_name);
        TextView location = (TextView) view.findViewById(R.id.list_testing_location);
        TextView county = (TextView) view.findViewById(R.id.list_testing_county);
        TextView info = (TextView) view.findViewById(R.id.list_testing_schedule);
        name.setText(nameList[i]);
        location.setText(locationList[i]);
        county.setText(countyList[i]);
        info.setText(infoList[i]);
        return view;
    }
}