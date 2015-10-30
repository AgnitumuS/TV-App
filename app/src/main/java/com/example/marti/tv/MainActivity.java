package com.example.marti.tv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by MortadhaS on 10/3/2015.
 **/
public class MainActivity extends AppCompatActivity
{
    public JSONArray jsonArray;

    private static Context context;
    private StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private BufferedReader jsonReader;
    private StringBuilder jsonBuilder;
    private ActionBar actionBar;
    private FloatingActionButton fab;//can be used here instead of inside the fragment...
    private ViewPager viewPager;
    private TabLayout tabLayout;

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
        actionBar.setElevation(0);



        try {
            //URL urlJson = new URL("http://tv.jt.iq/json.php");
            URL urlJson = new URL("http://steammania.allalla.com/json2.php");
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
            jsonArray = new JSONArray(tokener);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#de156f"));
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "By MurtadhaS 2015", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static Context getContext()
    {
        return context;
    }



    public class MainFragmentPagerAdapter extends FragmentPagerAdapter
    {

        //final int PAGE_COUNT =5;
        //private String tabTitles[] = new String[] { "All", "Sports", "Movies" ,"News & Culture","Other"};
        ArrayList<String> genres;

        {
            try
            {
                genres = getDistinctJsonArray(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        final int PAGE_COUNT=genres.size();
        private ArrayList<String> tabTitles=genres;
        private Context context;

        public MainFragmentPagerAdapter(FragmentManager fm, Context context)
        {
          //  jsonArray.
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position)
        {
            return MainFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            // Generate title based on item position
            return tabTitles.get(position);
        }




    }

//get the tabs genres from json file
    public ArrayList<String> getDistinctJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> genres = new ArrayList<String>();
        genres.add("All");
        JSONObject tempJsonObject;
        for(int position=0;position<jsonArray.length();position++)
        {
            tempJsonObject=jsonArray.getJSONObject(position);
            if(!genres.contains(tempJsonObject.getString("channel_type")))
                genres.add(tempJsonObject.getString("channel_type"));
        }

return genres;
    }


    public JSONArray getJsonArray(int mPage)
    {
        JSONArray tempJsonArray=new JSONArray();
        JSONObject jsonObject;
        String pageTitle=(String)viewPager.getAdapter().getPageTitle(mPage-1);


        if(pageTitle.equals("All"))return jsonArray;
        for(int number=0;number<jsonArray.length();number++)
        {
            try
            {
                jsonObject=jsonArray.getJSONObject(number);
                if (jsonObject.getString("channel_type").equals(pageTitle))
                    tempJsonArray.put(jsonObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        return tempJsonArray;
    }
}
