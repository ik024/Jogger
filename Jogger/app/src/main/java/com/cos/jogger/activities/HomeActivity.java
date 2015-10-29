package com.cos.jogger.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.cos.jogger.R;
import com.cos.jogger.fragments.HomeTab;
import com.cos.jogger.fragments.MapTab;
import com.cos.jogger.fragments.RecordFragment;
import com.cos.jogger.utils.RealPathUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.janmuller.android.simplecropimage.CropImage;

public class HomeActivity extends AppCompatActivity implements RecordFragment.OnFragmentInteractionListener, HomeTab.OnFragmentInteractionListener, MapTab.OnFragmentInteractionListener{

    private final String TAG = HomeActivity.class.getSimpleName();

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    CircleImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        imageview = (CircleImageView) view.findViewById(R.id.profile_image);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //get instance of record fragment
        Fragment recordFragment = RecordFragment.newInstance();

        //show record fragment by default
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, recordFragment, "recordFragment");
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openCropActivity(View view){
        Intent intent = new Intent (this, CropImageActivity.class);
        startActivity(intent);
       /* Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), 2);*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // gallery and camera ommitted
            case 1:
                if(data != null) {
                    Log.d("path", "after: " + data.getData());
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    // if nothing received
                    if (path == null) {
                        return;
                    }
                    // cropped bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageview.setImageBitmap(bitmap);
                }
                break;
            case 2:
                Intent intent = new Intent(this, CropImage.class);
                Uri otherPath = Uri.parse("android.resource://com.cos.jogger/drawable/splash_image");

                intent.putExtra(CropImage.IMAGE_PATH, getRealPathFromURI(data));
                intent.putExtra(CropImage.SCALE, true);
                intent.putExtra(CropImage.ASPECT_X, 2);//change ration here via intent
                intent.putExtra(CropImage.ASPECT_Y, 2);
                Log.d("path", "data: " + data.getData());
                Log.d("path", "result: "+getRealPathFromURI(data));
                startActivityForResult(intent, 1);//final static int 1
            default:
                break;
        }
    }

    private String getRealPathFromURI(Intent data) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
        return realPath;
    }
}
