package uk.ac.shef.com4510;

public class DiskImage {
    private String path;
    private String title;

    public DiskImage(String path, String title) {
        this.path = path;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }
}
