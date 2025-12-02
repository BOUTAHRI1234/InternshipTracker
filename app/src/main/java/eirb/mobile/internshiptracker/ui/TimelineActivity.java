package eirb.mobile.internshiptracker.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.Timeline;
import eirb.mobile.internshiptracker.util.SessionManager;

public class TimelineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InternshipInteractionAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        dbHelper = new DatabaseHelper(this);

        String companyName = getIntent().getStringExtra("COMPANY_NAME");
        int companyId = getIntent().getIntExtra("COMPANY_ID", -1);

        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        tvCompanyName.setText(companyName);

        recyclerView = findViewById(R.id.recyclerViewInteractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InternshipInteractionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (companyId != -1) {
            loadInteractions(companyId);
        }
    }

    private void loadInteractions(int companyId) {
        String userEmail = SessionManager.getEmail(this);
        new Thread(() -> {
            Timeline timeline = dbHelper.getTimelineForCompany(userEmail, companyId);
            if (timeline != null) {
                runOnUiThread(() -> adapter.setData(timeline.interactions));
            }
        }).start();
    }
}
