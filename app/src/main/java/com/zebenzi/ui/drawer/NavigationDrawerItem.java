package com.zebenzi.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zebenzi.ui.R;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */
public class NavigationDrawerItem extends ListItem {

    // Constructor.
    public NavigationDrawerItem(int icon, String name) {
        super(name, icon);
    }

    @Override
    public int getViewType() {
        return NavigationDrawerAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_row_navigation_drawer, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.navigationDrawerRowIcon);
        TextView textViewName = (TextView) view.findViewById(R.id.navigationDrawerRowText);

        imageViewIcon.setImageResource(this.icon);
        textViewName.setText(this.name);

        return view;
    }

}
