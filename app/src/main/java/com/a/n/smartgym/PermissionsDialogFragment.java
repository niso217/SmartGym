package com.a.n.smartgym;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.a.n.smartgym.Listener.PermissionsGrantedCallback;

/**
 * Created by tylerjroach on 8/31/16.
 */

public class PermissionsDialogFragment extends DialogFragment {
  private final int PERMISSION_REQUEST_CODE = 11;

  private Context context;
  private PermissionsGrantedCallback listener;

  private boolean shouldResolve;
  private boolean shouldRetry;
  private boolean externalGrantNeeded;
  private static final String TAG = PrimaryFragment.class.getSimpleName();


  public static PermissionsDialogFragment newInstance() {
    return new PermissionsDialogFragment();
  }

  public PermissionsDialogFragment() {}

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
    if (context instanceof PermissionsGrantedCallback) {
      listener = (PermissionsGrantedCallback) context;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.PermissionsDialogFragmentStyle);
    setCancelable(false);
    requestNecessaryPermissions();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (shouldResolve) {
      if (externalGrantNeeded) {
        showAppSettingsDialog();
      } else if(shouldRetry) {
        showRetryDialog();
      } else {
        //permissions have been accepted
        if (listener != null) {
          listener.navigateToCaptureFragment();
          dismiss();
        }
      }
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
    listener = null;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    shouldResolve = true;
    shouldRetry = false;

    for (int i=0; i<permissions.length; i++) {
      String permission = permissions[i];
      int grantResult = grantResults[i];

      if (!shouldShowRequestPermissionRationale(permission) && grantResult != PackageManager.PERMISSION_GRANTED) {
        externalGrantNeeded = true;
        return;
      } else if (grantResult != PackageManager.PERMISSION_GRANTED) {
        shouldRetry = true;
        return;
      }
    }
  }

  private void requestNecessaryPermissions() {
    requestPermissions(new String[] {
        Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
  }

  private void showAppSettingsDialog() {
    new AlertDialog.Builder(context)
        .setTitle("Permissions Required")
        .setMessage("In order to record videos, access to the camera, microphone, and storage is needed. Please enable these permissions from the app settings.")
        .setPositiveButton("App Settings", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
            listener.progressState(false);
            dismiss();
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            listener.closeActivity();
            dismiss();
          }
        }).setCancelable(false).create().show();
  }

  private void showRetryDialog() {
    new AlertDialog.Builder(context)
        .setTitle("Permissions Declined")
        .setMessage("In order to record videos, the app needs access to the camera, microphone, and storage.")
        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            requestNecessaryPermissions();
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            listener.closeActivity();
            dismiss();
          }
        }).create().show();
  }


}
