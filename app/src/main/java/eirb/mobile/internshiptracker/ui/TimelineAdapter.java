package eirb.mobile.internshiptracker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import eirb.mobile.internshiptracker.R;
import eirb.mobile.internshiptracker.model.Timeline;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<Timeline> timelines;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Timeline timeline);
    }

    public TimelineAdapter(List<Timeline> timelines, OnItemClickListener listener) {
        this.timelines = timelines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        Timeline timeline = timelines.get(position);
        holder.bind(timeline, listener);
    }

    @Override
    public int getItemCount() {
        return timelines.size();
    }

    public void setData(List<Timeline> newTimelines) {
        this.timelines = newTimelines;
        notifyDataSetChanged();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCompanyName;
        private final TextView tvLastInteraction;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvLastInteraction = itemView.findViewById(R.id.tvLastInteraction);
        }

        public void bind(final Timeline timeline, final OnItemClickListener listener) {
            tvCompanyName.setText(timeline.company.name);

            if (timeline.interactions != null && !timeline.interactions.isEmpty()) {
                String lastInteractionText = "Last interaction: " + timeline.interactions.get(0).offerName;
                tvLastInteraction.setText(lastInteractionText);
            } else {
                tvLastInteraction.setText("No interactions yet.");
            }

            itemView.setOnClickListener(v -> listener.onItemClick(timeline));
        }
    }
}
