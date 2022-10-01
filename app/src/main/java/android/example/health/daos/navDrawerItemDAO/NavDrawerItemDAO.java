package android.example.health.daos.navDrawerItemDAO;

public class NavDrawerItemDAO {
    private String title;
    private int image;

    public void setImage(int image) {
        this.image = image;
    }
    public int getImage() {
        return image;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}