package com.bbot.maxflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class FGEViewAdapter extends RecyclerView.Adapter<FGEViewAdapter.ViewHolder> {

//    private static Bitmap mImg;
    private List<FlowGraphEntity> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public void setData(List<FlowGraphEntity> data) {
        this.mData = data;
    }

    // data is passed into the constructor
    FGEViewAdapter(Context context, List<FlowGraphEntity> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
//        this.mImg = img;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mData.get(position).getName());
//        holder.myImageView.setImageBitmap(mImg);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
//        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
//            myImageView = (ImageView) itemView.findViewById(R.id.info_img);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    FlowGraphEntity getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}