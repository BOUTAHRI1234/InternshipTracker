package eirb.mobile.internshiptracker.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "InternshipSession";

    public static void saveUser(Context context, int id, String email, String imapPass, String mistralKey) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("IS_LOGGED_IN", true);
        editor.putInt("ID", id);
        editor.putString("EMAIL", email);
        editor.putString("IMAP_PASS", imapPass);
        editor.putString("MISTRAL_KEY", mistralKey);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean("IS_LOGGED_IN", false);
    }

    public static int getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt("ID", -1);
    }
    // ... autres getters (getEmail, getImapPassword, etc.)

    public static String getEmail(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("EMAIL", null);
    }

    public static String getImapPassword(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("IMAP_PASS", null);
    }

    public static String getMistralKey(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("MISTRAL_KEY", null);
    }
}