package com.zebenzi.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */
public abstract class ListItem {
    protected ListItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public abstract int getViewType();

    public abstract View getView(LayoutInflater inflater, View convertView);

    public abstract String getName();

    public abstract int getIcon();

    protected int icon;
    protected String name;
}
