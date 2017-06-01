package com.a.n.smartgym;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.a.n.smartgym.Listener.PermissionsGrantedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by tylerjroach on 8/31/16.
 */

public class SettingsDialogFragment extends DialogFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private int REQUEST_ENABLE_BT = 1;
    private int REQUEST_NFC = 2;
    final public static int REQUEST_CODE = 3;
    final public static int REQUEST_CHECK_SETTINGS = 4;

    private Context context;
    private PermissionsGrantedCallback listener;
    private GoogleApiClient mGoogleApiClient;

    private boolean shouldResolve;
    private boolean shouldRetry;
    private boolean mBluetoothInProgress;
    private static final String TAG = PrimaryFragment.class.getSimpleName();


    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    public SettingsDialogFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof PermissionsGrantedCallback) {
            listener = (PermissionsGrantedCallback) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.PermissionsDialogFragmentStyle);
        setCancelable(false);
        InitializeGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        listener = null;
    }

    private void InitializeGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            mBluetoothInProgress = false;
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                Log.d(TAG, "Bluetooth not enabled");
                dismiss();
                close();
                return;
            }
            if (resultCode == Activity.RESULT_OK) {
                CheckNFC();
                return;
            }
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "REQUEST_CHECK_SETTINGS: RESULT_OK");
                    CheckBluetooth();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "REQUEST_CHECK_SETTINGS: RESULT_CANCELED");
                    // The user was asked to change settings, but chose not to

                    break;
                default:
                    break;
            }

        }

        if (requestCode == REQUEST_NFC) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(TAG, " REQUEST_NFC OK");
                    break;
                default:
                    Log.d(TAG, " REQUEST_NFC default");
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void promptWIFISettings() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.nfc_denied));
        builder.setMessage(getResources().getString(R.string.nfc_fix));
        builder.setPositiveButton(getResources().getString(R.string.go_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goWIFISettings();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                close();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "finish");
                dismiss();
                close();
            }
        });
        builder.show();
    }

    private void goWIFISettings() {
        Intent WIFISettings = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        WIFISettings.addCategory(Intent.CATEGORY_DEFAULT);
        WIFISettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(WIFISettings);
    }

    private void promptLocationSettings() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.gps_denied));
        builder.setMessage(getResources().getString(R.string.gps_fix));
        builder.setPositiveButton(getResources().getString(R.string.go_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goLocationSettings();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                close();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "finish");
                dismiss();
                close();
            }
        });
        builder.show();
    }

    private void goLocationSettings() {
       // startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);

        Intent mIntent= new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(mIntent, REQUEST_CHECK_SETTINGS);
    }

    private void setLocation() {
        LocationRequest mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setNeedBle(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates mLocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        CheckBluetooth();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            Log.d(TAG, "startResolutionForResult");
//                            MainActivity main = (MainActivity)context;
//                            status.startResolutionForResult(
//                                    getActivity(),
//                                    REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
                        promptLocationSettings();
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }

    private void CheckBluetooth() {

        if (BluetoothAdapter.getDefaultAdapter() == null || !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "finish");
            dismiss();
            close();
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                getActivity().setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Intent enableBtIntent = new Intent(BluetoothAdapter.getDefaultAdapter().ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else
                CheckNFC();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"onConfigurationChanged");
        //dismiss();
    }

    public void CheckNFC() {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            Toast.makeText(context, "NFC is not available", Toast.LENGTH_LONG)
                    .show();
            dismiss();
            close();
        }
        if (!mNfcAdapter.isEnabled()) {
            promptWIFISettings();
        } else
            dismiss();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void close() {
        if (listener != null)
            listener.closeActivity();
    }

}
