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
import eirb.mobile.internshiptracker.model.InternshipInteraction;

public class InternshipInteractionAdapter extends RecyclerView.Adapter<InternshipInteractionAdapter.InteractionViewHolder> {

    private List<InternshipInteraction> interactions;

    public InternshipInteractionAdapter(List<InternshipInteraction> interactions) {
        this.interactions = interactions;
    }

    @NonNull
    @Override
    public InteractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internship_interaction, parent, false);
        return new InteractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InteractionViewHolder holder, int position) {
        InternshipInteraction interaction = interactions.get(position);
        holder.bind(interaction);
    }

    @Override
    public int getItemCount() {
        return interactions.size();
    }

    public void setData(List<InternshipInteraction> newInteractions) {
        this.interactions = newInteractions;
        notifyDataSetChanged();
    }

    static class InteractionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOfferName;
        private final TextView tvInteractionDate;
        private final TextView tvDescription;

        public InteractionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOfferName = itemView.findViewById(R.id.tvOfferName);
            tvInteractionDate = itemView.findViewById(R.id.tvInteractionDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(InternshipInteraction interaction) {
            tvOfferName.setText(interaction.offerName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            tvInteractionDate.setText(sdf.format(interaction.interactionDate));
            tvDescription.setText(interaction.description);
        }
    }
}
