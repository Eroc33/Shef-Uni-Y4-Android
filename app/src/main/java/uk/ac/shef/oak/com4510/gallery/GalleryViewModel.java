package uk.ac.shef.oak.com4510.gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.oak.com4510.ImageRepository;
import uk.ac.shef.oak.com4510.data.Image;
import uk.ac.shef.oak.com4510.search.Search;
import uk.ac.shef.oak.com4510.support.ProxyLiveData;

/**
 * Provide a @link{LiveData} of all images in the db, and progress of any ongoing loading of images.
 */
public class GalleryViewModel extends AndroidViewModel {
    private static String TAG = "GalleryViewModel";
    private final ImageRepository imageRepository;

    private final ProxyLiveData<List<Image>> images = new ProxyLiveData<>();

    public final MutableLiveData<Boolean> hasCamera = new MutableLiveData<>();

    public final LiveData<Boolean> scanningImages = new MediatorLiveData<>();
    public final LiveData<Long> scannedImageCount = new MutableLiveData<>();
    public final LiveData<Long> totalImagesToScan = new MutableLiveData<>();
    public final LiveData<String> scanStage = new MutableLiveData<>();

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
        images.setSource(imageRepository.getAllImages());
        ((MediatorLiveData<Boolean>)scanningImages).addSource(scannedImageCount,(count)->{
            updateScanningStatus(count,totalImagesToScan.getValue());
        });
        ((MediatorLiveData<Boolean>)scanningImages).addSource(totalImagesToScan,(total)->{
            updateScanningStatus(scannedImageCount.getValue(),total);
        });
    }

    public LiveData<List<Image>> getImages() {
        return images;
    }

    public void applySearch(Search search) {
        images.setSource(imageRepository.search(search));
    }

    public void withExactImages(long[] ids){
        images.setSource(imageRepository.findExact(ids));
    }

    public void setProgress(long count, long total,int stageResourceId){
        ((MutableLiveData<Long>) scannedImageCount).postValue(count);
        ((MutableLiveData<Long>) totalImagesToScan).postValue(total);
        ((MutableLiveData<String>)scanStage).postValue(getApplication().getResources().getString(stageResourceId));

    }

    private void updateScanningStatus(Long count, Long total){
        if(count == null || total == null){
            return;
        }
        ((MediatorLiveData<Boolean>) scanningImages).postValue(!count.equals(total));

    }
}
