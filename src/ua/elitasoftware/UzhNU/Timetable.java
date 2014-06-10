package ua.elitasoftware.UzhNU;

import java.util.ArrayList;

public class Timetable {
    private String id;
    private String caption;
    private String data;
    private String parent_id;
    private ArrayList<TimetableItem> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public ArrayList<TimetableItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TimetableItem> items) {
        this.items = items;
    }
}
