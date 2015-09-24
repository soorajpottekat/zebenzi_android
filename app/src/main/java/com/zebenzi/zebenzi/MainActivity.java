package com.zebenzi.zebenzi;

import android.support.v4.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends ActionBarActivity {

    public static final int LOGIN_REQUEST = 1;
    public static final int REGISTER_REQUEST = 2;
    public static Context appContext;
    private FragmentManager fm = getSupportFragmentManager();
    private String mSearchString;

    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_actions, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

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
        getSupportActionBar().setIcon(R.drawable.ic_menu_zebenzi);
        appContext = getApplicationContext();

        handleIntent(getIntent());



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = fm.beginTransaction();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                SearchResultsFragment searchFragment = new SearchResultsFragment();
                transaction.replace(R.id.fragment_container, searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.action_account:
                return true;
            case R.id.action_register:
                Intent intent = new Intent(this, RegisterCustomerActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST);
                return true;
            case R.id.action_login:
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                LoginFragment loginFragment = new LoginFragment();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
}



