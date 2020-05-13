package lexfy.hdstudios.thoughtsapp.model;

import com.google.firebase.Timestamp;

public class QuoteModel {

    private String title;
    private String description;
    private String image_url;
    private String userId;
    private Timestamp timeAdded;
    private String userName;

    public QuoteModel() {
    }

    public QuoteModel(String title, String description, String image_url, String userId, Timestamp timeAdded, String userName) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
