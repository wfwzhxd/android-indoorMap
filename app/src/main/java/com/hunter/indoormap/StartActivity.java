package com.hunter.indoormap;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = StartActivity.class.getSimpleName();

    private static final String MAPFILE_DIR = "mapfile";
    private static final String MAPFILE_SUFFIX = ".dxf";
    private static final String ASSETS_URI_PRIFIX = "//android_asset/";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        listView = (ListView) findViewById(R.id.list);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 0);
            }
        });
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                return getMapList();
            }

            @Override
            protected void onPostExecute(final List<String> list) {
                listView.setAdapter(new ArrayAdapter<>(StartActivity.this, R.layout.simple_list_item_1, list));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        viewMap(Uri.fromFile(new File(ASSETS_URI_PRIFIX + MAPFILE_DIR, list.get(position))));
                    }
                });
            }
        }.execute();
    }

    private List<String> getMapList() {
        List<String> mapList = new ArrayList<>();
        try {
            String[] mapFiles = getAssets().list(MAPFILE_DIR);
            if (mapFiles != null) {
                for (String fileName : mapFiles) {
                    if (fileName.toLowerCase().endsWith(MAPFILE_SUFFIX)) {
                        mapList.add(fileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private void viewMap(Uri mapUri) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.setData(mapUri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            Log.d(TAG, data.getData().toString());
            if (data.getData() != null) {
                viewMap(data.getData());
            }
        }
    }
}
