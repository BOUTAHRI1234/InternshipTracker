package eirb.mobile.internshiptracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import eirb.mobile.internshiptracker.model.InternshipApplication;
import eirb.mobile.internshiptracker.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "internship_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASS_HASH = "passwordHash";
    private static final String COL_USER_IMAP_PASS = "imapPassword";
    private static final String COL_USER_API_KEY = "mistralApiKey";

    // Table Applications
    private static final String TABLE_APPS = "applications";
    private static final String COL_APP_ID = "id";
    private static final String COL_APP_USER_ID = "userId";
    private static final String COL_APP_COMPANY = "companyName";
    private static final String COL_APP_POSITION = "position";
    private static final String COL_APP_STATUS = "status";
    private static final String COL_APP_EMAIL_ID = "emailId";
    private static final String COL_APP_SUMMARY = "summary";
    private static final String COL_APP_DATE = "sentDate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASS_HASH + " TEXT, " +
                COL_USER_IMAP_PASS + " TEXT, " +
                COL_USER_API_KEY + " TEXT)";

        String createApp = "CREATE TABLE " + TABLE_APPS + " (" +
                COL_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_APP_USER_ID + " INTEGER, " +
                COL_APP_COMPANY + " TEXT, " +
                COL_APP_POSITION + " TEXT, " +
                COL_APP_STATUS + " TEXT, " +
                COL_APP_EMAIL_ID + " TEXT, " +
                COL_APP_SUMMARY + " TEXT, " +
                COL_APP_DATE + " INTEGER, " +
                "FOREIGN KEY(" + COL_APP_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)";

        db.execSQL(createUsers);
        db.execSQL(createApp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- User Operations ---

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, user.email);
        values.put(COL_USER_PASS_HASH, user.passwordHash);
        values.put(COL_USER_IMAP_PASS, user.imapPassword);
        values.put(COL_USER_API_KEY, user.mistralApiKey);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            user.email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL));
            user.passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASS_HASH));
            user.imapPassword = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_IMAP_PASS));
            user.mistralApiKey = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_API_KEY));
            cursor.close();
        }
        db.close();
        return user;
    }

    // --- Application Operations ---

    public void insertApplication(InternshipApplication app) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APP_USER_ID, app.userId);
        values.put(COL_APP_COMPANY, app.companyName);
        values.put(COL_APP_POSITION, app.position);
        values.put(COL_APP_STATUS, app.status);
        values.put(COL_APP_EMAIL_ID, app.emailId);
        values.put(COL_APP_SUMMARY, app.summary);
        values.put(COL_APP_DATE, app.sentDate);
        db.insert(TABLE_APPS, null, values);
        db.close();
    }

    public List<InternshipApplication> getApplicationsForUser(int userId) {
        List<InternshipApplication> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPS, null, COL_APP_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COL_APP_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                InternshipApplication app = new InternshipApplication();
                app.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_ID));
                app.userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_USER_ID));
                app.companyName = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_COMPANY));
                app.position = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_POSITION));
                app.status = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_STATUS));
                app.emailId = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_EMAIL_ID));
                app.summary = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_SUMMARY));
                app.sentDate = cursor.getLong(cursor.getColumnIndexOrThrow(COL_APP_DATE));
                list.add(app);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<InternshipApplication> getCompanyTimeline(int userId, String companyName) {
        List<InternshipApplication> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPS, null, COL_APP_USER_ID + "=? AND " + COL_APP_COMPANY + "=?",
                new String[]{String.valueOf(userId), companyName}, null, null, COL_APP_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                InternshipApplication app = new InternshipApplication();
                app.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_ID));
                app.userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_USER_ID));
                app.companyName = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_COMPANY));
                app.position = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_POSITION));
                app.status = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_STATUS));
                app.emailId = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_EMAIL_ID));
                app.summary = cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_SUMMARY));
                app.sentDate = cursor.getLong(cursor.getColumnIndexOrThrow(COL_APP_DATE));
                list.add(app);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int countEmailId(String emailId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPS + " WHERE " + COL_APP_EMAIL_ID + "=?", new String[]{emailId});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}