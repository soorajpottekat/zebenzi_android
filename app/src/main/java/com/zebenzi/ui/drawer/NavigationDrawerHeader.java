package com.zebenzi.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.users.Customer;

/**
 * Created by Vaugan.Nayagar on 2015/09/27.
 */
public class NavigationDrawerHeader extends ListItem {

    public String email;

    // Constructor.
    public NavigationDrawerHeader(int icon, String name, String email) {
        super(name, icon);
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

        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.nav_drawer_header_icon);
        TextView textViewName = (TextView) view.findViewById(R.id.nav_drawer_header_name);
        TextView textViewEmail = (TextView) view.findViewById(R.id.nav_drawer_header_email);
        imageViewIcon.setImageResource(R.drawable.ic_account);

        try {
            Picasso.with(MainActivity.getAppContext()).load(Customer.getInstance().getCustomerImageUrl()).into(imageViewIcon);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        textViewName.setText(Customer.getInstance().getCustomerFirstName());
//        textViewEmail.setText(Customer.getInstance().getCustomerMobileNumber());

        return view;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getIcon() {
        return this.icon;
    }
}
