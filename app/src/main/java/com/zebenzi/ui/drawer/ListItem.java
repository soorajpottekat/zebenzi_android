package com.zebenzi.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */
public interface ListItem {
        public int getViewType();
        public View getView(LayoutInflater inflater, View convertView);
}
