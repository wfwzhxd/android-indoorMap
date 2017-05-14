package com.hunter.indoormap.overlay;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ScannerDialogFragment extends DialogFragment implements ZXingScannerView.ResultHandler {
    private static String TAG = ScannerDialogFragment.class.getSimpleName();
    private ZXingScannerView mScannerView;
    ZXingScannerView.ResultHandler resultHandler;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        mScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        mScannerView.setResultHandler(this);
        return new AlertDialog.Builder(getActivity()).setView(mScannerView).create();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            }
        }
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        Log.i(TAG, mScannerView.getWidth() + " " + mScannerView.getHeight());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PERMISSION_DENIED) {
            Toast.makeText(getActivity(), "Please give me the CAMERA permission!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setResultHandler(ZXingScannerView.ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.i(TAG, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString());
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isResumed()) {
                    mScannerView.resumeCameraPreview(ScannerDialogFragment.this);
                }
            }
        }, 2000);
        if (resultHandler != null) {
            resultHandler.handleResult(rawResult);
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        mScannerView.stopCamera();
    }

}