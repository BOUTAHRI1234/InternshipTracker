package eirb.mobile.internshiptracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.InternshipApplication;
import eirb.mobile.internshiptracker.service.AiAnalyzer;
import eirb.mobile.internshiptracker.service.ImapService;
import eirb.mobile.internshiptracker.util.SessionManager;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private DatabaseHelper dbHelper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApplicationAdapter(new ArrayList<>(), app -> {
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.putExtra("COMPANY_NAME", app.companyName);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabSync).setOnClickListener(v -> syncEmails());

        loadData();
    }

    private void loadData() {
        int userId = SessionManager.getUserId(this);
        new Thread(() -> {
            List<InternshipApplication> list = dbHelper.getApplicationsForUser(userId);
            runOnUiThread(() -> adapter.setData(list));
        }).start();
    }

    private void syncEmails() {
        progressBar.setVisibility(View.VISIBLE);
        String email = SessionManager.getEmail(this);
        String imapPass = SessionManager.getImapPassword(this);
        String mistralKey = SessionManager.getMistralKey(this);
        int userId = SessionManager.getUserId(this);

        new Thread(() -> {
            ImapService imapService = new ImapService();
            AiAnalyzer aiAnalyzer = new AiAnalyzer(mistralKey);

            imapService.fetchEmails(email, imapPass, (msg, folder) -> {
                try {
                    String msgId = msg.getHeader("Message-ID")[0];
                    if (dbHelper.countEmailId(msgId) > 0) return; // Check SQLite

                    String subject = msg.getSubject();
                    String sender = msg.getFrom()[0].toString();
                    String body = imapService.getTextFromMessage(msg);
                    String date = msg.getSentDate().toString();

                    JsonObject analysis = aiAnalyzer.analyzeEmail(subject, body, sender, date);

                    if (analysis != null) {
                        InternshipApplication app = new InternshipApplication();
                        app.userId = userId;
                        app.emailId = msgId;
                        app.companyName = analysis.has("company") ? analysis.get("company").getAsString() : "Unknown";
                        app.position = analysis.has("position") ? analysis.get("position").getAsString() : "Unknown";
                        app.status = analysis.has("status") ? analysis.get("status").getAsString() : "Awaiting Reply";
                        app.summary = analysis.has("summary") ? analysis.get("summary").getAsString() : "";
                        app.sentDate = msg.getSentDate().getTime();

                        dbHelper.insertApplication(app); // Insert via SQLite
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Sync Complete", Toast.LENGTH_SHORT).show();
                loadData();
            });
        }).start();
    }
}