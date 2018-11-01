package uk.ac.shef.com4510;

import android.net.Uri;

public class DiskImage {
    private int id;
    private String path;
    private String title;
    private Uri storageUri;

    DiskImage(int id, String path, String title, Uri storageUri) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.storageUri = storageUri;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Uri getStorageUri() {
        return storageUri;
    }
}
