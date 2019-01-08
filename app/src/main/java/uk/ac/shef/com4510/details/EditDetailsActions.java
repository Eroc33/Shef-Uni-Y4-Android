package uk.ac.shef.com4510.details;

/**
 * Actions in the edit details activity which cannot be on the viewmodel (i.e. navigation)
 */
public interface EditDetailsActions {
    void commitEdit();

    void getLocation();
}
