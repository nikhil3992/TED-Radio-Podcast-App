package com.example.nikhil.group22_hw07;
/**
 *
 * File name - PodcastAdapter.java
 * Full Name - Nikhil Jonnalagadda
 *
 * **/
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;


public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.PodcastHolder> {

    private List<Podcast> list;
    private LayoutInflater inflater;
    private ItemClickInterface itemClickInterface;

    public PodcastAdapter(Context context,List<Podcast> list,ItemClickInterface itemClickInterface) {

        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.itemClickInterface = itemClickInterface;
    }

    @Override
    public PodcastHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(MainActivity.viewIsLinear ? R.layout.list_item : R.layout.grid_layout,parent,false);
        return new PodcastHolder(view);
    }

    @Override
    public void onBindViewHolder(PodcastHolder holder, int position) {

        Podcast podcast = list.get(position);
        holder.titleTextView.setText(podcast.getTitle());
        holder.itemImageView.setImageBitmap(podcast.getImageBitmap());
        if (MainActivity.viewIsLinear) {
            holder.postedDateTextView.setText(podcast.getPubDate().substring(0,16));
        }
        if(podcast.isPlaying()) {
            holder.playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
        } else {
            holder.playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_18dp);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PodcastHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView itemImageView;
        private TextView titleTextView,postedDateTextView;
        private Button playButton;
        private View itemContainer;

        public PodcastHolder(View itemView) {

            super(itemView);
            itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            if (MainActivity.viewIsLinear) {
                postedDateTextView = (TextView) itemView.findViewById(R.id.postedDateTextView);
            }
            playButton = (Button) itemView.findViewById(R.id.playButton);
            playButton.setOnClickListener(this);
            itemContainer = itemView.findViewById(R.id.item_container);
            itemContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_container:
                    itemClickInterface.onItemClick(getAdapterPosition());
                    break;
                case R.id.playButton:
                    Podcast podcast = list.get(getAdapterPosition());
                    if(podcast.isPlaying()) {
                        podcast.setPlaying(false);
                        playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_18dp);
                    } else {
                        podcast.setPlaying(true);
                        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
                    }
                    itemClickInterface.onPlayItemClick(getAdapterPosition());

                    break;
                default:
                    break;
            }

        }
    }

    public interface ItemClickInterface extends Serializable {
        void onPlayItemClick(int position);
        void onItemClick(int position);
    }
}
