package eirb.mobile.internshiptracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import eirb.mobile.internshiptracker.model.Company;
import eirb.mobile.internshiptracker.model.InternshipInteraction;
import eirb.mobile.internshiptracker.model.Timeline;
import eirb.mobile.internshiptracker.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "internship_tracker.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASS_HASH = "passwordHash";
    private static final String COL_USER_IMAP_PASS = "imapPassword";
    private static final String COL_USER_API_KEY = "mistralApiKey";

    // Table Company
    private static final String TABLE_COMPANY = "company";
    private static final String COL_COMPANY_ID = "id";
    private static final String COL_COMPANY_NAME = "name";

    // Table InternshipInteraction
    private static final String TABLE_INTERNSHIP_INTERACTION = "internship_interaction";
    private static final String COL_INTERACTION_ID = "id";
    private static final String COL_INTERACTION_COMPANY_ID = "company_id";
    private static final String COL_INTERACTION_OFFER_NAME = "offer_name";
    private static final String COL_INTERACTION_DESCRIPTION = "description";
    private static final String COL_INTERACTION_DATE = "interaction_date";
    private static final String COL_INTERACTION_USER_EMAIL = "user_email";


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

        String createCompany = "CREATE TABLE " + TABLE_COMPANY + " (" +
                COL_COMPANY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMPANY_NAME + " TEXT UNIQUE)";

        String createInternshipInteraction = "CREATE TABLE " + TABLE_INTERNSHIP_INTERACTION + " (" +
                COL_INTERACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_INTERACTION_COMPANY_ID + " INTEGER, " +
                COL_INTERACTION_OFFER_NAME + " TEXT, " +
                COL_INTERACTION_DESCRIPTION + " TEXT, " +
                COL_INTERACTION_DATE + " INTEGER, " +
                COL_INTERACTION_USER_EMAIL + " TEXT, " +
                "FOREIGN KEY(" + COL_INTERACTION_COMPANY_ID + ") REFERENCES " + TABLE_COMPANY + "(" + COL_COMPANY_ID + ") ON DELETE CASCADE)";

        db.execSQL(createUsers);
        db.execSQL(createCompany);
        db.execSQL(createInternshipInteraction);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERNSHIP_INTERACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
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

    // --- Company Operations ---
    public long insertCompany(Company company) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPANY_NAME, company.name);
        long id = db.insertWithOnConflict(TABLE_COMPANY, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        if (id == -1) {
            return getCompanyByName(company.name).id;
        }
        return id;
    }

    public Company getCompanyByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMPANY, null, COL_COMPANY_NAME + "=?", new String[]{name}, null, null, null);
        Company company = null;
        if (cursor != null && cursor.moveToFirst()) {
            company = new Company();
            company.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID));
            company.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY_NAME));
            cursor.close();
        }
        db.close();
        return company;
    }

    // --- InternshipInteraction Operations ---
    public long insertInternshipInteraction(InternshipInteraction interaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INTERACTION_COMPANY_ID, interaction.companyId);
        values.put(COL_INTERACTION_OFFER_NAME, interaction.offerName);
        values.put(COL_INTERACTION_DESCRIPTION, interaction.description);
        values.put(COL_INTERACTION_DATE, interaction.interactionDate);
        values.put(COL_INTERACTION_USER_EMAIL, interaction.userEmail);
        long id = db.insert(TABLE_INTERNSHIP_INTERACTION, null, values);
        db.close();
        return id;
    }
    
    public List<Timeline> getTimelinesForUser(String userEmail) {
        List<Timeline> timelines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COL_INTERACTION_COMPANY_ID + " FROM " + TABLE_INTERNSHIP_INTERACTION + " WHERE " + COL_INTERACTION_USER_EMAIL + "=?";
        Cursor companyCursor = db.rawQuery(query, new String[]{userEmail});

        if (companyCursor.moveToFirst()) {
            do {
                int companyId = companyCursor.getInt(companyCursor.getColumnIndexOrThrow(COL_INTERACTION_COMPANY_ID));
                timelines.add(getTimelineForCompany(userEmail, companyId));
            } while (companyCursor.moveToNext());
        }
        companyCursor.close();
        db.close();

        return timelines;
    }
    
    public Timeline getTimelineForCompany(String userEmail, int companyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Company company = null;
        List<InternshipInteraction> interactions = new ArrayList<>();

        Cursor companyDetailsCursor = db.query(TABLE_COMPANY, null, COL_COMPANY_ID + "=?", new String[]{String.valueOf(companyId)}, null, null, null);
        if(companyDetailsCursor.moveToFirst()){
            company = new Company();
            company.id = companyDetailsCursor.getInt(companyDetailsCursor.getColumnIndexOrThrow(COL_COMPANY_ID));
            company.name = companyDetailsCursor.getString(companyDetailsCursor.getColumnIndexOrThrow(COL_COMPANY_NAME));
        }
        companyDetailsCursor.close();

        Cursor interactionCursor = db.query(TABLE_INTERNSHIP_INTERACTION, null, COL_INTERACTION_COMPANY_ID + "=? AND " + COL_INTERACTION_USER_EMAIL + "=?",
                new String[]{String.valueOf(companyId), userEmail}, null, null, COL_INTERACTION_DATE + " DESC");

        if (interactionCursor.moveToFirst()) {
            do {
                InternshipInteraction interaction = new InternshipInteraction();
                interaction.id = interactionCursor.getInt(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_ID));
                interaction.companyId = interactionCursor.getInt(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_COMPANY_ID));
                interaction.offerName = interactionCursor.getString(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_OFFER_NAME));
                interaction.description = interactionCursor.getString(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_DESCRIPTION));
                interaction.interactionDate = interactionCursor.getLong(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_DATE));
                interaction.userEmail = interactionCursor.getString(interactionCursor.getColumnIndexOrThrow(COL_INTERACTION_USER_EMAIL));
                interactions.add(interaction);
            } while (interactionCursor.moveToNext());
        }
        interactionCursor.close();

        if(company != null){
            return new Timeline(company, interactions);
        }
        return null;
    }
}
