package eirb.mobile.internshiptracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.User;
import eirb.mobile.internshiptracker.util.SessionManager;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        new Thread(() -> {
            User user = dbHelper.getUserByEmail(email);

            if (user != null) {
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash);
                if (result.verified) {
                    SessionManager.saveUser(this, user.id, user.email, user.imapPassword, user.groqApiKey);
                    runOnUiThread(() -> {
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
