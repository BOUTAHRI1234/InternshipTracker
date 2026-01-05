package eirb.mobile.internshiptracker.model;

public class User {
    public int id;

    public String email;
    public String passwordHash;
    public String imapPassword;
    public String groqApiKey;

    public User() {}

    public User(int id, String email, String passwordHash, String imapPassword, String groqApiKey) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.imapPassword = imapPassword;
        this.groqApiKey = groqApiKey;
    }
}
