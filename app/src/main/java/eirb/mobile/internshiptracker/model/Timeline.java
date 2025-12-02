package eirb.mobile.internshiptracker.model;

import java.util.List;

public class Timeline {
    public Company company;
    public List<InternshipInteraction> interactions;

    public Timeline(Company company, List<InternshipInteraction> interactions) {
        this.company = company;
        this.interactions = interactions;
    }
}
