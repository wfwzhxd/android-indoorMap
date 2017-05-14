package com.hunter.indoormap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.hunter.indoormap.data.DataSource;
import com.hunter.indoormap.data.DxfDataSource;
import com.hunter.indoormap.overlay.Overlay;
import com.hunter.indoormap.overlay.RouterOverlay;
import com.hunter.indoormap.route.RouterDataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri mapUri = getIntent().getData();
        if (mapUri == null) {
            Log.w(TAG, "No mapUri found in " + getIntent());
            finish();
            return;
        }
        mapView = (MapView) findViewById(R.id.map_view);
        new DataLoader().executeOnExecutor(Executors.newFixedThreadPool(1), mapUri);
    }

    Boolean readPermGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PERMISSION_DENIED) {
            readPermGranted = false;
            Toast.makeText(this, "Please give me the READ SD permission!", Toast.LENGTH_SHORT).show();
        } else {
            readPermGranted = true;
        }
    }

    class DataLoader extends AsyncTask<Uri, Void, DataSource> {
//        final String File

        final String ASSETS_URI_PRIFIX = "/android_asset/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this) {
                @Override
                public void onBackPressed() {
                }

                @Override
                public boolean onTouchEvent(@NonNull MotionEvent event) {
                    return true;
                }
            };
            progressDialog.setMessage(getString(R.string.loading_map));
            progressDialog.show();
        }

        @Override
        protected DataSource doInBackground(Uri... params) {
            Uri mapUri = params[0];
            DxfDataSource dataSource = null;
            if (mapUri.getScheme() != null && mapUri.getScheme().toLowerCase().equals("file")) {
                String path = mapUri.getPath();
                if (path != null) {
                    if (path.toLowerCase().startsWith(ASSETS_URI_PRIFIX) && path.length() > ASSETS_URI_PRIFIX.length()) {
                        InputStream inputStream = null;
                        try {
                            inputStream = getAssets().open(path.substring(ASSETS_URI_PRIFIX.length()));
                            dataSource = new DxfDataSource(inputStream);
                            dataSource.loadData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                                readPermGranted = null;
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                                while (readPermGranted == null) {
                                    try {
                                        Thread.sleep(250l);
                                    } catch (InterruptedException e) {}
                                }
                            }
                        }
                        dataSource = new DxfDataSource(new File(path));
                        dataSource.loadData();
                    }
                    if (dataSource != null && !dataSource.isDataLoaded()) {
                        dataSource = null;
                    }
                }
            }
            return dataSource;
        }

        @Override
        protected void onPostExecute(DataSource dataSource) {
            progressDialog.dismiss();
            if (dataSource == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setMessage(getString(R.string.loadmap_error)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.finish();
                    }
                });
                alertDialog.show();
            } else {
                mapView.setDataSource(dataSource);
                if (dataSource instanceof RouterDataSource) {
                    for (Overlay overlay : mapView.getOverlayManager()) {
                        if (overlay instanceof RouterOverlay) {
                            ((RouterOverlay)overlay).setRouterDataSource((RouterDataSource) dataSource);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mapView.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long lastBackPressed;

    @Override
    public void onBackPressed() {
        Long curTime = System.currentTimeMillis();
        if (curTime-lastBackPressed < 1800) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = curTime;
    }
}
