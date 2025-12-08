package eirb.mobile.internshiptracker.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.User;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etImapPass, etMistralKey;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etImapPass = findViewById(R.id.etImapPass);
        etMistralKey = findViewById(R.id.etMistralKey);
        Button btnRegister = findViewById(R.id.btnRegister);

        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        tvLoginLink.setOnClickListener(v -> finish());


        TextView tvImapHelp = findViewById(R.id.tvImapHelp);
        tvImapHelp.setOnClickListener(v -> {
            String helpMessage = "Here’s how to do to redeem IMAP Password code:\n\n" +
                    "1. Open your Google Account Security Settings: Visit https://account.google.com/security\n\n" +
                    "2. Enable 2-Step Verification (if it’s not already enabled): Scroll down to Signing in to Google → click 2-Step Verification → follow the setup steps. You’ll need this active before you can create app passwords.\n\n" +
                    "3. Generate an App Password:\n" +
                    "- Once 2-Step Verification is on, go back to the Security page.\n" +
                    "- Click App Passwords (it appears below 2-Step Verification).\n" +
                    "- Log in again if prompted.\n" +
                    "- Under Select App, choose Mail.\n" +
                    "- Under Select Device, choose Other (Custom name) → type for example InternshipTracker.\n" +
                    "- Click Generate.\n" +
                    "- Google will display a 16-character password — copy it.\n\n" +
                    "4. Use this password in your .env file\n\n" +
                    "5. Enable IMAP in Gmail (if not already):\n" +
                    "- Go to Gmail → Settings → See all settings → Forwarding and POP/IMAP tab.\n" +
                    "- Under “IMAP access,” choose Enable IMAP → click Save Changes.";

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Gmail Configuration Help")
                    .setMessage(helpMessage)
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String imapPass = etImapPass.getText().toString();
            String mistralKey = etMistralKey.getText().toString();

            if (email.isEmpty() || password.isEmpty() || imapPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                User newUser = new User(0, email, hash, imapPass, mistralKey);
                long id = dbHelper.insertUser(newUser);

                runOnUiThread(() -> {
                    if (id > 0) {
                        Toast.makeText(this, "Registered! Please login.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Registration failed (Email exists?)", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}