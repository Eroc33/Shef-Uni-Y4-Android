package uk.ac.shef.com4510;

import android.arch.persistence.room.Room;

import uk.ac.shef.com4510.data.ImageDatabase;

public class Application extends android.app.Application {
    public ImageDatabase imageDb() {
        return Room.databaseBuilder(getApplicationContext(),
                ImageDatabase.class, "com4510-image-database").build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImageScannerService.start(getApplicationContext());
    }
}
