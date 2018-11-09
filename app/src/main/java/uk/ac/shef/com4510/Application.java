package uk.ac.shef.com4510;

import android.arch.persistence.room.Room;

import uk.ac.shef.com4510.data.ImageDatabase;

public class Application extends android.app.Application {
    private static ImageDatabase imageDbInstance;

    public synchronized ImageDatabase getImageDb() {
        if (imageDbInstance == null) {
            imageDbInstance = Room.databaseBuilder(getApplicationContext(),
                    ImageDatabase.class, "com4510-image-database").build();
        }
        return imageDbInstance;
    }
}
