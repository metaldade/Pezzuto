package com.pezzuto.pezzuto.items;

import java.util.Date;

/**
 * Created by dade on 28/05/17.
 */

public class Event {
    private String name, description, briefDescription, place, image, month;
    Date startDate, endDate;
    private int id,participants;
    public Event(int id, String name, String description, String briefDescription, Date startDate, Date endDate, String place,
                 int participants, String image) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.briefDescription = briefDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.participants = participants;
        this.image = image;
    }
    public String getName() { return name; }
    public String getDescription() { return description; };
    public String getBriefDescription() { return briefDescription; };
    public String getPlace() { return place; }
    public String getImage() { return image;}
    public int getParticipants() { return participants; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getMonth() { return month; }
    public int getId() { return id; }
    public void setMonth(String month) { this.month = month; }
}
