package com.zebenzi.ui;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zebenzi.ui.drawer.ListItem;
import com.zebenzi.ui.drawer.NavigationDrawerAdapter;
import com.zebenzi.ui.drawer.NavigationDrawerHeader;
import com.zebenzi.ui.drawer.NavigationDrawerItem;
import com.zebenzi.users.Customer;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends ActionBarActivity implements FragmentListener{

    public static final int LOGIN_REQUEST = 1;
    public static final int REGISTER_REQUEST = 2;
    public static Context appContext;
    private FragmentManager fm = getSupportFragmentManager();
    private String mSearchString;

    //TODO: Get this title text dynamically from the fragments
    private String[] mNavigationOptions = {"Header", "Search", "Account", "History", "Login", "Register"};

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle = "drawer title";
    private CharSequence mTitle = "drawer title";

    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        // Associate searchable configuration with the SearchView and Toolbar

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            // Create a new Fragment to be placed in the activity layout
            SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            searchResultsFragment.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            fm.beginTransaction().add(R.id.fragment_container, searchResultsFragment).commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(R.string.app_name);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_menu_zebenzi);
        appContext = getApplicationContext();

        List<ListItem> drawerItems = new ArrayList<ListItem>();
        drawerItems.add(new NavigationDrawerHeader(R.drawable.profile,
                Customer.getInstance().getCustomerName(), Customer.getInstance().getCustomerEmail()));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_search, "Search"));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_account, "Account"));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_history, "Job History"));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_sign_in, "Login"));
        drawerItems.add(new NavigationDrawerItem(R.drawable.ic_register, "Register"));

//        NavigationDrawerItem[] drawerItem = new NavigationDrawerItem[5];
//
//        drawerItem[0] = new NavigationDrawerItem(R.drawable.ic_search, "Search");
//        drawerItem[1] = new NavigationDrawerItem(R.drawable.ic_account, "Account");
//        drawerItem[2] = new NavigationDrawerItem(R.drawable.ic_history, "History");
//        drawerItem[3] = new NavigationDrawerItem(R.drawable.ic_sign_in, "Login");
//        drawerItem[4] = new NavigationDrawerItem(R.drawable.ic_register, "Register");

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
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
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        handleIntent(getIntent());
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        FragmentTransaction transaction = fm.beginTransaction();


        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
                    Toast.makeText(this, "TESTING!", Toast.LENGTH_LONG).show();
//                }
                return true;
            case R.id.action_search:
                SearchResultsFragment searchFragment = new SearchResultsFragment();
                transaction.replace(R.id.fragment_container, searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.search_title);
                return true;
            case R.id.action_history:
                JobHistoryFragment historyFragment = new JobHistoryFragment();
                transaction.replace(R.id.fragment_container, historyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.history);
                return true;
            case R.id.action_account:
                return true;
            case R.id.action_register:
                RegisterFragment registerFragment = new RegisterFragment();
                transaction.replace(R.id.fragment_container, registerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.register);

                return true;
            case R.id.action_login:
                LoginFragment loginFragment = new LoginFragment();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.login);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Context getAppContext(){
        return appContext;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        //launch search fragment and pass search string


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println("Search Query = " + query);

            doSearch(query);

        }
    }


    private void doSearch(String searchString) {

        //Have to recreate the searchFrag, as we have only one fragment_container currently, and it could be any one of login, search or registration.
//        SearchResultsFragment searchFrag = (SearchResultsFragment)  fm.findFragmentById(R.id.fragment_container);
//        if (searchFrag != null) {
//            searchFrag.doSearch(searchString);
//        } else
        {
            // Create fragment and give it an argument for the selected article
            SearchResultsFragment newFragment = new SearchResultsFragment();
            Bundle args = new Bundle();
            args.putString(SearchResultsFragment.SEARCH_STRING, searchString);
            newFragment.setArguments(args);

            FragmentTransaction transaction = fm.beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void changeFragment(int fragmentId) {

        FragmentTransaction transaction = fm.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        switch (fragmentId) {
            case R.id.action_search:
                SearchResultsFragment searchFragment = new SearchResultsFragment();
                transaction.replace(R.id.fragment_container, searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.action_history:
                JobHistoryFragment historyFragment = new JobHistoryFragment();
                transaction.replace(R.id.fragment_container, historyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.action_account:
                break;
            case R.id.action_register:
                RegisterFragment registerFragment = new RegisterFragment();
                transaction.replace(R.id.fragment_container, registerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.action_login:
                LoginFragment loginFragment = new LoginFragment();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                System.out.println("Fragment not found. Id = " + fragmentId);
                break;
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        int id = R.id.action_search;
        switch (position) {
            case 1:
                id = R.id.action_search;
                break;
            case 2:
                id = R.id.action_account;
                break;
            case 3:
                id = R.id.action_history;
                break;
            case 4:
                id = R.id.action_login;
                break;
            case 5:
                id = R.id.action_register;
                break;
        }

        changeFragment(id);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationOptions[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}



