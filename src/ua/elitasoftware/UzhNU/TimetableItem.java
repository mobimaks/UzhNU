package ua.elitasoftware.UzhNU;

import java.util.ArrayList;

public class TimetableItem {

    private int id;
    private int itemType;
    private String caption;
    private String description;
    private String postDate;
    private Integer hits;
    private ArrayList<TimetableItem> items;

    public TimetableItem(int id, int itemType, String caption, String description, String postDate, Integer hits, ArrayList<TimetableItem> items) {
        this.id = id;
        this.itemType = itemType;
        this.caption = caption;
        this.description = description;
        this.postDate = postDate;
        this.hits = hits;
        this.items = items;
    }

    public TimetableItem(int id, int itemType, String caption, String description, String postDate, Integer hits) {
        this(id, itemType, caption, description, postDate, hits, null);
    }

    public int getId() {
        return id;
    }

    public int getItemType() {
        return itemType;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getPostDate() {
        return postDate;
    }

    public int getHits() {
        return hits;
    }

    public ArrayList<TimetableItem> getItems() {
        return items;
    }
}
