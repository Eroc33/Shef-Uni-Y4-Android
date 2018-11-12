package uk.ac.shef.com4510.gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;

public class GalleryViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;
    //FIXME: this indirection seems dumb. should we really have this on the viewmodel?
    //       or should we have the activity implement an interface for these actions
    private Runnable onOpenCamera;
    private Runnable onOpenMap;

    public LiveData<List<Image>> getImages() {
        return imageRepository.getAllImages();
    }
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public void openCamera() {
        if (this.onOpenCamera != null) {
            this.onOpenCamera.run();
        }
    }

    public void openMap() {
        if (this.onOpenMap != null) {
            this.onOpenMap.run();
        }
    }

    public void setOnOpenCamera(Runnable onOpenCamera) {
        this.onOpenCamera = onOpenCamera;
    }

    public void setOnOpenMap(Runnable onOpenMap) {
        this.onOpenMap = onOpenMap;
    }
}
