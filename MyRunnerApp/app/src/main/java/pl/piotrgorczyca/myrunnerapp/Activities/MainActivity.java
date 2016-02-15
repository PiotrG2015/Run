package pl.piotrgorczyca.myrunnerapp.Activities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.EditProfileFragment;
import pl.piotrgorczyca.myrunnerapp.MailboxFragment;
import pl.piotrgorczyca.myrunnerapp.SearchFragment;
import pl.piotrgorczyca.myrunnerapp.StatisticsFragment;
import pl.piotrgorczyca.myrunnerapp.adapters.NavDrawItem;
import pl.piotrgorczyca.myrunnerapp.adapters.NavDrawerListAdapter;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.TrainingListFragment;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;
import pl.piotrgorczyca.myrunnerapp.helper.SessionManager;

public class MainActivity extends ActionBarActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private View header;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles = {"Profile","Trainings list","Statistics", "Messages","Search", "Settings", "Logout"};
    private int[] navMenuIcons={R.drawable.ic_training_list,R.drawable.ic_statistics,R.drawable.ic_messages,R.drawable.ic_search, R.drawable.ic_settings, R.drawable.ic_logout};


    /*TODO: remote data download*/

    private String name;
    private String email;
    private String user_pid;
    int profilePicture = R.drawable.profile_picture;

    private ArrayList<NavDrawItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private SQLiteHandler db;
    private SessionManager session;
    @Bind(R.id.drawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.list_slidermenu) protected ListView mDrawerList;
    protected TextView mName;
    protected TextView mEmail;
    protected ImageView mPicture;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mTitle = mDrawerTitle = getTitle();
        header = getLayoutInflater().inflate(R.layout.nav_header, mDrawerList, false);
        mName = ButterKnife.findById(header, R.id.name);
        mEmail = ButterKnife.findById(header, R.id.nav_header_show_profile);
        mPicture = ButterKnife.findById(header, R.id.circleView);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        email = user.get("email");
        user_pid = user.get("id");
        mName.setText(name);
        mEmail.setText(email);
        mPicture.setImageResource(profilePicture);
        mDrawerList.addHeaderView(header, null, true);


        navDrawerItems = new ArrayList<NavDrawItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawItem(navMenuTitles[1], navMenuIcons[0]));
        navDrawerItems.add(new NavDrawItem(navMenuTitles[2], navMenuIcons[1]));
        navDrawerItems.add(new NavDrawItem(navMenuTitles[3], navMenuIcons[2]));
        navDrawerItems.add(new NavDrawItem(navMenuTitles[4], navMenuIcons[3]));
        navDrawerItems.add(new NavDrawItem(navMenuTitles[5], navMenuIcons[4]));
        navDrawerItems.add(new NavDrawItem(navMenuTitles[6], navMenuIcons[5]));


        // Recycle the typed array
        //navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nava drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);


        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.menu_icon);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(1);
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            if(position==6){
                logoutUser();
            } else
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", user_pid);
                startActivity(intent);
                break;
            case 1:
                fragment = new TrainingListFragment();
                break;
            case 2:
                fragment = new StatisticsFragment();
                break;
            case 3:
                fragment = new MailboxFragment();
                break;
            case 4:
                fragment = new SearchFragment();
                break;
            case 5:
                fragment = new EditProfileFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            if(position!=0) {
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
            }
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
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

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
