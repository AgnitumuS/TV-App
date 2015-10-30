package com.example.marti.tv;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by MortadhaS on 10/3/2015.
 **/

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ChannelViewHolder>{

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView channel_name;
        ImageView channel_cover;
        ImageView channel_status;

        ChannelViewHolder(View itemView)
        {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.channel_card);
            cardView.setClickable(true);
            channel_name = (TextView)itemView.findViewById(R.id.channel_name);
            channel_cover = (ImageView)itemView.findViewById(R.id.channel_cover);
            channel_status=(ImageView)itemView.findViewById(R.id.channel_status_image);
        }
    }

    List<Channel> channels;

    RecyclerViewAdapter(List<Channel> channels)
    {

        this.channels = channels;

    }

    @Override
    public int getItemCount()
    {
        return channels.size();
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ChannelViewHolder channelViewHolder = new ChannelViewHolder(view);
        return channelViewHolder;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder channelViewHolder, int i)
    {
        if(channels.get(i).channel_status)
        channelViewHolder.channel_status.setImageResource(R.drawable.online_flat);
        else
            channelViewHolder.channel_status.setImageResource(R.drawable.offline_flat);
        channelViewHolder.channel_name.setText(channels.get(i).channel_name);
        Picasso.with(MainActivity.getContext()).load(channels.get(i).channel_cover).into(channelViewHolder.channel_cover);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

}