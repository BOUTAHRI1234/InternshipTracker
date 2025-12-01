package eirb.mobile.internshiptracker.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.data.DatabaseHelper;
import eirb.mobile.internshiptracker.model.InternshipApplication;
import eirb.mobile.internshiptracker.util.SessionManager;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        String companyName = getIntent().getStringExtra("COMPANY_NAME");
        TextView tvTitle = findViewById(R.id.tvCompanyTitle);
        tvTitle.setText(companyName + " Timeline");

        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));

        int userId = SessionManager.getUserId(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Instanciation

        new Thread(() -> {
            List<InternshipApplication> timeline = dbHelper.getCompanyTimeline(userId, companyName);

            // Adapter non fourni dans le prompt initial, mais la logique d'appel est ici
            runOnUiThread(() -> {
                // TimelineAdapter adapter = new TimelineAdapter(timeline);
                // rvTimeline.setAdapter(adapter);
            });
        }).start();
    }
}