package com.bbot.maxflow;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExplorerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        GridView gv = (GridView) findViewById(R.id.gv);
        File[] files = getFilesDir().listFiles();
        final List<String> fnames = new ArrayList<>();
        System.out.println(getFilesDir().getPath());
        for(File f : files) {
            fnames.add(f.getName());
            System.out.println(f.getName());
        }
        fnames.add("hello");
        fnames.add("hello");
        fnames.add("hello");
        fnames.add("hello");
        fnames.add("hello");
        fnames.add("hello");

        System.out.println( getResources().getIdentifier("sample_graph.txt","Graphs",getPackageName())
        );
        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, fnames);
        gv.setAdapter(gridViewArrayAdapter);
        gridViewArrayAdapter.notifyDataSetChanged();
//        Button btn = (Button) findViewById(R.id.open_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


    }
}
