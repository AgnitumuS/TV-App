package com.example.marti.tv;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.support.v4.app.Fragment
{

    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;


    public  JSONArray jsonArray;
    public ArrayList<Channel> fields;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ActionBar actionBar;
    private FloatingActionButton fab;


    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static MainFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        gridLayoutManager = new GridLayoutManager(getContext(),2) ;
        recyclerView.setLayoutManager(gridLayoutManager);
        fab=(FloatingActionButton)view.findViewById(R.id.fab);






        final MainActivity mainActivity=(MainActivity)getActivity();
        jsonArray=mainActivity.getJsonArray(mPage);
        actionBar=mainActivity.getSupportActionBar();
        actionBar.setShowHideAnimationEnabled(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.finish();
                startActivity(mainActivity.getIntent());
            }
        });



        fields = new ArrayList<Channel>();


            for (int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject jsonObject = null;
                try
                {
                    jsonObject = jsonArray.getJSONObject(index);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    fields.add(new Channel(jsonObject.getString("channel_name"), jsonObject.getString("channel_stream"), jsonObject.getString("channel_cover"),jsonObject.getString("channel_type") ,jsonObject.getString("channel_source"),jsonObject.getBoolean("channel_status")));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                adapter = new RecyclerViewAdapter(fields);
            }
            ScaleInAnimationAdapter scaleadapter =new ScaleInAnimationAdapter(adapter);
            recyclerView.setAdapter(scaleadapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                    int Distance=10;
                    if(dy>Distance){fab.hide();}
                    if(dy<-Distance){fab.show();}
                }
            });

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            String channel_stream;
                            String channel_name;
                            String channel_source;

                            boolean channel_status;
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(position);
                                Intent intent;
                                channel_stream = jsonObject.getString("channel_stream");
                                channel_status = jsonObject.getBoolean("channel_status");
                                channel_name = jsonObject.getString("channel_name");
                                channel_source=jsonObject.getString("channel_source");
                                if (channel_status)
                                {
                                    intent = new Intent(getContext(), com.google.android.exoplayer.demo.PlayerActivity.class);
                                    intent.setData(Uri.parse(channel_stream));
                                    intent.putExtra("content_type", TYPE_HLS);
                                    intent.putExtra("content_source",channel_source);
                                    startActivity(intent);
                                }
                                else
                                {
                                    intent = new Intent(getContext(), ShowAlertActivity.class);
                                    intent.putExtra("Type", 0);//if == 0 show channel is down message
                                    intent.putExtra("channel_name", channel_name);
                                    startActivity(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
            );

        return view;
    }

}