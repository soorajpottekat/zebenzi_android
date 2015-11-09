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

        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.navigationDrawerHeaderIcon);
        TextView textViewName = (TextView) view.findViewById(R.id.navigationDrawerHeaderName);
        TextView textViewEmail = (TextView) view.findViewById(R.id.navigationDrawerHeaderEmail);

        //TODO: Get customer profile pic
        if (Customer.getInstance().getCustomerImageUrl().equalsIgnoreCase("")){
        imageViewIcon.setImageResource(R.drawable.profile_pic_default);}
        else {
            Picasso.with(MainActivity.getAppContext()).load(Customer.getInstance().getCustomerImageUrl()).into(imageViewIcon);
        }
        textViewName.setText(Customer.getInstance().getCustomerName());
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
