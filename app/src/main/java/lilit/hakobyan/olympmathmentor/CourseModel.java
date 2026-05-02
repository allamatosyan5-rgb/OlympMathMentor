package lilit.hakobyan.olympmathmentor;

public class CourseModel {
    private int id;
    private String title;
    private boolean isLocked;
    private int colorResId;

    public CourseModel(int id, String title, boolean isLocked, int colorResId) {
        this.id = id;
        this.title = title;
        this.isLocked = isLocked;
        this.colorResId = colorResId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isLocked() { return isLocked; }
    public int getColorResId() { return colorResId; }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
}