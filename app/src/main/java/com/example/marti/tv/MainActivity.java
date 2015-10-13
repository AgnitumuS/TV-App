package com.example.marti.tv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
/**
 * Created by MortadhaS on 10/3/2015.
 **/
public class MainActivity extends AppCompatActivity
{
//to be used later
    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

    private static Context context;

    private StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private BufferedReader jsonReader;
    private StringBuilder jsonBuilder;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context =this.getApplicationContext();

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar)));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        try
        {
          URL urlJson = new URL("http://tv.jt.iq/json.php");
          //URL urlJson=new URL("http://steammania.allalla.com/json.php");
            URLConnection connectionJson = null;
            try
            {
                connectionJson = urlJson.openConnection();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                jsonReader = new BufferedReader(new InputStreamReader(connectionJson.getInputStream()));
                jsonBuilder = new StringBuilder();
                for (String line = null; (line = jsonReader.readLine()) != null; )
                {
                    jsonBuilder.append(line).append("\n");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
            final JSONArray jsonArray = new JSONArray(tokener);
            ArrayList<Channel> fields = new ArrayList<Channel>();

            for (int index = 0; index < jsonArray.length(); index++)
            {
                final JSONObject jsonObject = jsonArray.getJSONObject(index);
                fields.add(new Channel(jsonObject.getString("channel_name"), jsonObject.getString("channel_stream"), jsonObject.getString("channel_cover"),jsonObject.getBoolean("channel_status")));
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(fields);
                ScaleInAnimationAdapter scaleadapter =new ScaleInAnimationAdapter(adapter);
                recyclerView.setAdapter(scaleadapter);
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(this.getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String channel_stream;
                                String channel_name;
                                boolean channel_status;
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(position);
                                    Intent intent;
                                    channel_stream = jsonObject.getString("channel_stream");
                                    channel_status = jsonObject.getBoolean("channel_status");
                                    channel_name = jsonObject.getString("channel_name");
                                    if (channel_status)
                                    {
                                        intent = new Intent(MainActivity.this, com.google.android.exoplayer.demo.PlayerActivity.class);
                                        intent.setData(Uri.parse(channel_stream));
                                        intent.putExtra("content_type", TYPE_HLS);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        intent = new Intent(MainActivity.this, ShowAlertActivity.class);
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
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            setContentView(R.layout.activity_show_alert);
            TextView alert_text=(TextView)(findViewById(R.id.channel_name));
            alert_text.setText(R.string.alert_message_main);
            ImageView alert_image=(ImageView)(findViewById(R.id.channel_cover));
            alert_image.setImageResource(R.drawable.refreshsecond);
            alert_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "By MortadhaS 2015",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static Context getContext()
    {
        return context;
    }
}
