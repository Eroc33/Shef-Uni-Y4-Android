package uk.ac.shef.oak.com4510;

import android.arch.persistence.room.Room;

import uk.ac.shef.oak.com4510.data.ImageDatabase;

public class Application extends android.app.Application {
    private static ImageDatabase imageDbInstance;

    public ImageDatabase getImageDb() {
        return imageDbInstance;
    }

    @Override public void onCreate() {
        super.onCreate();
        imageDbInstance = Room.databaseBuilder(getApplicationContext(),
                ImageDatabase.class, "com4510-image-database").build();
    }
}
