package eirb.mobile.internshiptracker.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.activity_register); // Assurez-vous d'avoir ce layout

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etImapPass = findViewById(R.id.etRegImapPass);
        etMistralKey = findViewById(R.id.etRegMistralKey);
        Button btnRegister = findViewById(R.id.btnRegister);

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