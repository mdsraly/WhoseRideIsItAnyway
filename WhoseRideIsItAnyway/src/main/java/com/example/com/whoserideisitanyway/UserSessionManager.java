package com.example.com.whoserideisitanyway;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;

/**
 * Created by admin on 3/2/2016.
 */
public class UserSessionManager {

    SharedPreferences sharedPreferences;

    Context _context;

    SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;

    static final String MyPreferences = "MyPrefs";

    static final String IS_USER_LOGIN = "UserLoggedIn";

    static final String keyUname = "sessionusername";

    static final String keyPwd = "sessionpassword";

    public UserSessionManager(Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(keyUname, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    //create Login Session
    public void createUserLoginSession(String uname, String pwd) {
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(keyUname, uname);

        editor.putString(keyPwd, pwd);

        editor.commit();
    }

    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {

            Intent i = new Intent(_context, Login.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);

            return true;
        }
        return false;
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Login.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);


    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);
    }

    public String getUser() {
        String uname=sharedPreferences.getString(keyUname,null);
        return uname;
    }
    public String getPhone() {
        String phone=sharedPreferences.getString(keyPwd,null);
        return phone;
    }
}


