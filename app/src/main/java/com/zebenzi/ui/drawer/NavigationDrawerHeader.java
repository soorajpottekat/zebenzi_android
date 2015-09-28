package com.zebenzi.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zebenzi.ui.R;
import com.zebenzi.ui.drawer.ListItem;
import com.zebenzi.ui.drawer.NavigationDrawerAdapter;
import com.zebenzi.users.Customer;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */
public class NavigationDrawerHeader implements ListItem {
    public int icon;
    public String name;
    public String email;

    // Constructor.
    public NavigationDrawerHeader(int icon, String name, String email) {

        this.icon = icon;
        this.name = name;
        this.email = email;
    }

    @Override
    public int getViewType() {
        return NavigationDrawerAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.header_navigation_drawer, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.navigationDrawerHeaderIcon);
        TextView textViewName = (TextView) view.findViewById(R.id.navigationDrawerHeaderName);
        TextView textViewEmail = (TextView) view.findViewById(R.id.navigationDrawerHeaderEmail);

        //TODO: Get customer profile pic
        imageViewIcon.setImageResource(icon);
        textViewName.setText(Customer.getInstance().getCustomerName());
        textViewEmail.setText(Customer.getInstance().getCustomerEmail());

        return view;
    }
}
