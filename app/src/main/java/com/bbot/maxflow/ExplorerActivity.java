package com.bbot.maxflow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ExplorerActivity extends AppCompatActivity implements FGEViewAdapter.ItemClickListener {

    FGEViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        new dbLoader().execute(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvFlowGraphEntities);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new FGEViewAdapter(this, new ArrayList<FlowGraphEntity>());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        FlowGraphEntity chosen = adapter.getItem(position);
        Intent intent = new Intent(this, SolverActivity.class);
        intent.putExtra("serialized", chosen.getSerialized());
        startActivity(intent);

    }

    private class dbLoader extends AsyncTask<Context, Void, Void> {
        List<FlowGraphEntity> data;

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Context... context) {
            this.data = AppDatabase.getInstance(context[0]).flowGraphEntityDao().getAll();
            for (FlowGraphEntity d: data)
                System.out.println(d.getName());
            return null;
        }
    }
}