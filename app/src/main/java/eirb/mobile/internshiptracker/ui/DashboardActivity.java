package eirb.mobile.internshiptracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.Company;
import eirb.mobile.internshiptracker.model.InternshipInteraction;
import eirb.mobile.internshiptracker.model.Timeline;
import eirb.mobile.internshiptracker.service.AiAnalyzer;
import eirb.mobile.internshiptracker.service.ImapService;
import eirb.mobile.internshiptracker.util.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimelineAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView = findViewById(R.id.recyclerViewTimelines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TimelineAdapter(new ArrayList<>(), timeline -> {
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.putExtra("COMPANY_ID", timeline.company.id);
            intent.putExtra("COMPANY_NAME", timeline.company.name);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabSync).setOnClickListener(v -> syncEmails());

        loadData();
    }

    private void loadData() {
        String userEmail = SessionManager.getEmail(this);
        new Thread(() -> {
            List<Timeline> timelines = dbHelper.getTimelinesForUser(userEmail);
            runOnUiThread(() -> {
                if (timelines.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setData(timelines);
                }
            });
        }).start();
    }

    private void syncEmails() {
        Toast.makeText(this, "Sync starting...", Toast.LENGTH_SHORT).show();
        String email = SessionManager.getEmail(this);
        String imapPass = SessionManager.getImapPassword(this);
        String mistralKey = SessionManager.getMistralKey(this);

        new Thread(() -> {
            ImapService imapService = new ImapService();
            AiAnalyzer aiAnalyzer = new AiAnalyzer(mistralKey);

            imapService.fetchEmails(email, imapPass, (msg, folder) -> {
                try {
                    String subject = msg.getSubject();
                    String sender = msg.getFrom()[0].toString();
                    String body = imapService.getTextFromMessage(msg);
                    String date = msg.getSentDate().toString();

                    JsonObject analysis = aiAnalyzer.analyzeEmail(subject, body, sender, date);

                    if (analysis != null && analysis.has("company")) {
                        String companyName = analysis.get("company").getAsString();
                        Company company = new Company(0, companyName);
                        long companyId = dbHelper.insertCompany(company);

                        InternshipInteraction interaction = new InternshipInteraction();
                        interaction.companyId = (int) companyId;
                        interaction.offerName = analysis.has("position") ? analysis.get("position").getAsString() : "Unknown";
                        interaction.description = analysis.has("summary") ? analysis.get("summary").getAsString() : "";
                        interaction.interactionDate = msg.getSentDate().getTime();
                        interaction.userEmail = email;

                        dbHelper.insertInternshipInteraction(interaction);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            runOnUiThread(() -> {
                Toast.makeText(this, "Sync Complete", Toast.LENGTH_SHORT).show();
                loadData();
            });
        }).start();
    }
}
