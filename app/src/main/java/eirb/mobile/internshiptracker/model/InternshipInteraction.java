package eirb.mobile.internshiptracker.model;

public class InternshipInteraction {
    public int id;
    public int companyId;
    public String offerName;

    public String status;
    public String description;
    public long interactionDate;
    public String userEmail;

    public InternshipInteraction() {}

    public InternshipInteraction(int id, int companyId, String offerName, String description, long interactionDate, String userEmail, String status) {
        this.id = id;
        this.companyId = companyId;
        this.offerName = offerName;
        this.description = description;
        this.interactionDate = interactionDate;
        this.userEmail = userEmail;
        this.status = status;
    }
}
