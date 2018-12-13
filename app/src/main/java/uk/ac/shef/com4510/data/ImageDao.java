package uk.ac.shef.com4510.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.Calendar;
import java.util.List;

@Dao
@TypeConverters(Converters.class)
public abstract class ImageDao {
    @Query("SELECT EXISTS(SELECT * FROM image WHERE path = :path)")
    public abstract LiveData<Boolean> contains(String path);

    @Query("SELECT path FROM image WHERE path IN (:paths)")
    public abstract LiveData<List<String>> existing(String... paths);

    @Query("SELECT EXISTS(SELECT * FROM image WHERE path = :path)")
    public abstract boolean containsSync(String path);

    @Insert
    public abstract long[] insertSync(Image... image);

    @Query("SELECT * FROM image")
    public abstract LiveData<List<Image>> allImages();

    @Query("SELECT * FROM image where path = :path")
    public abstract LiveData<Image> getImage(String path);

    @Query("SELECT * FROM image where (title LIKE :title OR :title IS NULL) AND (description LIKE :description OR :description IS NULL) AND ((date BETWEEN :startDate AND :endDate) OR (:startDate IS NULL OR :endDate IS NULL))")
    public abstract LiveData<List<Image>> search(String title, String description, Calendar startDate, Calendar endDate);

    @Update
    public abstract void updateSync(Image image);

    @Query("SELECT * FROM image where path in (:paths)")
    public abstract LiveData<List<Image>> findAll(List<String> paths);
}
