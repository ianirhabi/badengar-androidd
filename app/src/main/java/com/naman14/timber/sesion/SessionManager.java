package  com.naman14.timber.sesion;

/**
 * Modified by irhabi on 10/31/2017.
 */


/**
 * Created by irhabi on 4/24/2017.
 */
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.naman14.timber.fragments.StreamingMusicFragment;

@SuppressLint("CommitPrefEdits")
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    int employ = 5;

    // nama sharedpreference
    private static final String PREF_NAME = "Sesi";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER = "nama";
    public static final String KEY_ID = "id";
    public  static  final  String KEY_USERGRUP = "grup";
    public static  final String LONGTITUDE = "long";
    public static  final String LATITUDE = "lat";
    public static  final String KEY_IMAGE = "IMAGE";
    public static  final String LONGTITUDEstylish = "nautral";
    public static  final String LATITUDEstylish = "DIS";
    public static  final String KEY_ID_STYLISH = "1";
    public static  final String KEY_KARYAWAN = "netral" ;
    public static  final String TOKEN = "token";
    public static  final String STATUS_BARANG = "00";

    // public Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * ciptakan login sesion
     * */
    // hanya menyimpan email
    public void createLoginSession(String user,String usrgrup, String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_USERGRUP, usrgrup);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, StreamingMusicFragment.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    /**
     *mendapatkan user by teknologi programmer jalanan
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID,null));
        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_USERGRUP, pref.getString(KEY_USERGRUP, null));
        user.put(LONGTITUDE, pref.getString(LONGTITUDE, null));
        return user;
    }

    /**
     * Hapus sesi by programmer jalanan
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, StreamingMusicFragment.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}