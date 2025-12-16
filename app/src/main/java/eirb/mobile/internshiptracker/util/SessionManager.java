package eirb.mobile.internshiptracker.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "InternshipSession";
    private static SharedPreferences sharedPreferences;

    private static synchronized SharedPreferences getEncryptedSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            try {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                sharedPreferences = EncryptedSharedPreferences.create(
                        context,
                        PREF_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException("Could not create EncryptedSharedPreferences", e);
            }
        }
        return sharedPreferences;
    }

    public static void saveUser(Context context, int id, String email, String imapPass, String mistralKey) {
        SharedPreferences.Editor editor = getEncryptedSharedPreferences(context).edit();
        editor.putBoolean("IS_LOGGED_IN", true);
        editor.putInt("ID", id);
        editor.putString("EMAIL", email);
        editor.putString("IMAP_PASS", imapPass);
        editor.putString("MISTRAL_KEY", mistralKey);
        editor.apply();
    }


    public static boolean isLoggedIn(Context context) {
        return getEncryptedSharedPreferences(context).getBoolean("IS_LOGGED_IN", false);
    }

    public static int getUserId(Context context) {
        return getEncryptedSharedPreferences(context).getInt("ID", -1);
    }

    public static String getEmail(Context context) {
        return getEncryptedSharedPreferences(context).getString("EMAIL", null);
    }

    public static String getImapPassword(Context context) {
        return getEncryptedSharedPreferences(context).getString("IMAP_PASS", null);
    }

    public static String getMistralKey(Context context) {
        return getEncryptedSharedPreferences(context).getString("MISTRAL_KEY", null);
    }
    public static void logout(Context context) {
        SharedPreferences.Editor editor = getEncryptedSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}