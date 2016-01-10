package com.zebenzi.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zebenzi.job.JobRequest;
import com.zebenzi.json.model.job.Job;
import com.zebenzi.ui.drawer.ListItem;
import com.zebenzi.ui.drawer.NavigationDrawerAdapter;
import com.zebenzi.ui.drawer.NavigationDrawerHeader;
import com.zebenzi.ui.drawer.NavigationDrawerItem;
import com.zebenzi.users.Customer;

import java.util.ArrayList;
import java.util.List;

import static com.zebenzi.ui.FragmentsLookup.*;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity implements FragmentListener {

    public static Context appContext;
    private FragmentManager fm = getSupportFragmentManager();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle = "drawer title";
    private CharSequence mActionBarTitle = "drawer title";
    private ImageView mToolbarProfileImage;
    private Menu mMenuOptions;
    private int mId = 2;

    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        mMenuOptions = menu;

        // Associate searchable configuration with the SearchView and Toolbar
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        MenuItem searchItem = menu.findItem(R.id.action_toolbar_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getApplicationContext();

        setContentView(R.layout.activity_main);


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            mToolbarProfileImage = (ImageView) findViewById(R.id.toolbar_user_image);
            mToolbarProfileImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    try {
                        if (Customer.getInstance().isLoggedIn()) {
                            //launch profile screen
                            changeFragment(ACCOUNT, null);

                        } else {
                            //launch login screen
                            changeFragment(LOGIN, null);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            //Launch new job fragment unless otherwise specified in the intent.
            String fragmentName = getIntent().getStringExtra("fragment_to_launch");
            String data = getIntent().getStringExtra("fragment_data");
            FragmentsLookup fragmentToLaunch = FragmentsLookup.findByName(fragmentName);
            if (fragmentToLaunch != null) {
                changeFragment(fragmentToLaunch, data);
            } else {
                changeFragment(NEW_JOB, null);
            }

//            // Create a new Fragment to be placed in the activity layout
//            SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            searchResultsFragment.setArguments(getIntent().getExtras());
//            // Add the fragment to the 'fragment_container' FrameLayout
//            fm.beginTransaction().add(R.id.fragment_container, searchResultsFragment).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        List<ListItem> drawerItems = new ArrayList<ListItem>();
        drawerItems.add(new NavigationDrawerHeader(R.drawable.profile,
                Customer.getInstance().getCustomerFirstName(), Customer.getInstance().getCustomerEmail()));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_register, getString(R.string.nav_drawer_item_new_job)));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_account, getString(R.string.nav_drawer_item_account)));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_history, getString(R.string.nav_drawer_item_history)));
//        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_search, getString(R.string.search)));
//        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_sign_in, getString(R.string.login)));
//        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_register, getString(R.string.register)));

        mActionBarTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this, drawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mActionBarTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (Customer.getInstance().isLoggedIn()) {
            hideMenuOption(R.id.action_login);
            hideMenuOption(R.id.action_register);
            showMenuOption(R.id.action_logout);
        } else {
            showMenuOption(R.id.action_login);
            showMenuOption(R.id.action_register);
            hideMenuOption(R.id.action_logout);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            Customer.getInstance().signOut();
            changeFragment(LOGIN, null);
        } else if (item.getItemId() == R.id.action_login) {
            changeFragment(LOGIN, null);
        } else if (item.getItemId() == R.id.action_register) {
            changeFragment(REGISTER, null);
        } else if (item.getItemId() == R.id.action_notify1) {
            displayNotification();
        }
        return true;
    }

    private void hideMenuOption(int id) {
        MenuItem item = mMenuOptions.findItem(id);
        item.setVisible(false);
    }

    private void showMenuOption(int id) {
        MenuItem item = mMenuOptions.findItem(id);
        item.setVisible(true);
    }

    public static Context getAppContext() {
        return appContext;
    }


    public void changeFragment(FragmentsLookup fragment, Object data) {

        //Update the title in the actionbar
        setTitle(fragment.getName());


        FragmentTransaction transaction = fm.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        switch (fragment) {
            case NEW_JOB:
                NewJobFragment newJobFragment = new NewJobFragment();
                transaction.replace(R.id.fragment_container, newJobFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case QUOTE:
                //We get the job request from the user's input, then on the Quote fragment, get and display the quote from the server.
                if (data != null) {
                    JobRequest r = (JobRequest) data;
                    QuoteFragment quoteFragment = QuoteFragment.newInstance(r);
                    transaction.replace(R.id.fragment_container, quoteFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(this, "There is no valid job request to display!", Toast.LENGTH_LONG).show();
                }
                break;
            case HISTORY:
                HistoryFragment historyFragment = new HistoryFragment();
                transaction.replace(R.id.fragment_container, historyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case ACCOUNT:
                AccountFragment accountFragment = new AccountFragment();
                transaction.replace(R.id.fragment_container, accountFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case REGISTER:
                RegisterFragment registerFragment = new RegisterFragment();
                transaction.replace(R.id.fragment_container, registerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case LOGIN:
                LoginFragment loginFragment = new LoginFragment();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case JOB_DETAILS:
                if (data != null) {
//                    Job j = (Job) data;
//                    JobDetailsFragment jobDetailsFragment = JobDetailsFragment.newInstance(j.getJobId());
                    int jobId = Integer.valueOf((String)data);
                    JobDetailsFragment jobDetailsFragment = JobDetailsFragment.newInstance(jobId);

                    transaction.replace(R.id.fragment_container, jobDetailsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(this, "There is no valid job to display!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                System.out.println("Fragment not found. Id = " + fragment);
                break;
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectNavDrawerItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectNavDrawerItem(int position) {
        FragmentsLookup id = NEW_JOB;

        switch (position) {
            case 0:
                //Title bar
                break;
            case 1:
                id = NEW_JOB;
                break;
            case 2:
                id = ACCOUNT;
                break;
            case 3:
                id = HISTORY;
                break;
        }
        changeFragment(id, null);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        if ((title != null) && (getSupportActionBar() != null)) {
            mActionBarTitle = title;
            getSupportActionBar().setTitle(mActionBarTitle);
        }


        try {
            Picasso.with(MainActivity.getAppContext()).load(Customer.getInstance().getCustomerImageUrl()).into(mToolbarProfileImage);
        }
        catch (Exception e){
            Picasso.with(MainActivity.getAppContext()).load(R.drawable.ic_account).into(mToolbarProfileImage);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
}



