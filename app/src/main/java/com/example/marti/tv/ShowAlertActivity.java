package com.example.marti.tv;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
/**
 * Created by MortadhaS on 10/3/2015.
 **/
public class ShowAlertActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        int type=intent.getIntExtra("Type",0);
        if(type==0)
        {
            setContentView(R.layout.activity_show_alert);

            actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar)));
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Channel " + intent.getStringExtra("channel_name"));

            textView =(TextView) (findViewById(R.id.channel_name));
            textView.setText(R.string.alert_message);
        }
        else if (type==1)
        {
//for any ohter type of alerts
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

        return super.onOptionsItemSelected(item);
    }
}
