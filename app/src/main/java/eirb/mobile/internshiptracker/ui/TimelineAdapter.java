package eirb.mobile.internshiptracker.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.model.InternshipInteraction;
import eirb.mobile.internshiptracker.model.Timeline;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<Timeline> timelines;
    private final OnTimelineClickListener listener;
    private Context context;

    public interface OnTimelineClickListener {
        void onTimelineClick(Timeline timeline);
    }

    public TimelineAdapter(List<Timeline> timelines, OnTimelineClickListener listener) {
        this.timelines = timelines;
        this.listener = listener;
    }

    public void setData(List<Timeline> newData) {
        this.timelines = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // On récupère le contexte ici
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        holder.bind(timelines.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return timelines.size();
    }

    class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvLastInteraction, tvDate;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvLastInteraction = itemView.findViewById(R.id.tvLastInteraction);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(final Timeline timeline, final OnTimelineClickListener listener) {
            tvCompanyName.setText(timeline.company.name);

            if (timeline.interactions != null && !timeline.interactions.isEmpty()) {
                InternshipInteraction last = timeline.interactions.get(0);

                String status = last.status != null ? last.status : "Applied";
                String displayText = last.offerName + " (" + status + ")";
                tvLastInteraction.setText(displayText);
                int iconResId;
                int colorHex;

                switch (status) {
                    case "Accepted":
                        iconResId = R.drawable.ic_status_accepted;
                        colorHex = Color.parseColor("#4CAF50");
                        break;
                    case "Rejected":
                        iconResId = R.drawable.ic_status_rejected;
                        colorHex = Color.parseColor("#F44336");
                        break;
                    case "Awaiting Reply":
                        iconResId = R.drawable.ic_status_awaiting;
                        colorHex = Color.parseColor("#FFC107");
                        break;
                    case "Applied":
                    default:
                        iconResId = R.drawable.ic_status_applied;
                        colorHex = Color.parseColor("#2196F3");
                        break;
                }

                Drawable statusDrawable = ContextCompat.getDrawable(context, iconResId != 0 ? iconResId : android.R.drawable.ic_menu_info_details);

                if (statusDrawable != null) {
                    statusDrawable = DrawableCompat.wrap(statusDrawable.mutate());
                    DrawableCompat.setTint(statusDrawable, colorHex);
                    tvLastInteraction.setCompoundDrawablesRelativeWithIntrinsicBounds(statusDrawable, null, null, null);
                    tvLastInteraction.setCompoundDrawablePadding(16);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvDate.setText(sdf.format(new Date(last.interactionDate)));
            } else {
                tvLastInteraction.setText("No interactions yet");
                tvDate.setText("");
                tvLastInteraction.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            }

            itemView.setOnClickListener(v -> listener.onTimelineClick(timeline));
        }
    }
}