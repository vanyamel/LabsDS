package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Event implements Serializable {
    private String title;
    private LocalDateTime dateTime;
    private String category;
    private boolean isPeriodic;
    private LocalDateTime reminderTime;

    public Event(String title, LocalDateTime dateTime, String category, boolean isPeriodic, LocalDateTime reminderTime) {
        this.title = title;
        this.dateTime = dateTime;
        this.category = category;
        this.isPeriodic = isPeriodic;
        this.reminderTime = reminderTime;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCategory() {
        return category;
    }

    public boolean isPeriodic() {
        return isPeriodic;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPeriodic(boolean periodic) {
        isPeriodic = periodic;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", dateTime=" + dateTime +
                ", category='" + category + '\'' +
                ", isPeriodic=" + isPeriodic +
                ", reminderTime=" + reminderTime +
                '}';
    }
}

