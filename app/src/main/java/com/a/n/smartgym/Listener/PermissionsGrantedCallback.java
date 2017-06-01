package com.a.n.smartgym.Listener;

/**
 * Created by nirb on 01/06/2017.
 */

public interface PermissionsGrantedCallback {
    void navigateToCaptureFragment();
    void closeActivity();
    void progressState(boolean progress);
}
