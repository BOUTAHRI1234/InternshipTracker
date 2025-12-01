package eirb.mobile.internshiptracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class User {
    public int id;

    public String email;
    public String passwordHash;
    public String imapPassword;
    public String mistralApiKey;

    public User() {}

    public User(int id, String email, String passwordHash, String imapPassword, String mistralApiKey) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.imapPassword = imapPassword;
        this.mistralApiKey = mistralApiKey;
    }
}
