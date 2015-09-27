package com.zebenzi.zebenzi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */


public class NavigationDrawerAdapter extends ArrayAdapter<ListItem> {

    private LayoutInflater mInflater;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public NavigationDrawerAdapter(Context mContext, List<ListItem> data) {
        super(mContext, 0, data);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

}