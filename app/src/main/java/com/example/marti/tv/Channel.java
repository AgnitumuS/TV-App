package com.example.marti.tv;

/**
 * Created by MortadhaS on 10/3/2015.
 **/
public class Channel
{

    String channel_name;
    String channel_stream;
    String channel_cover;
    Boolean channel_status;

    Channel(String channel_name, String channel_stream, String channel_cover,Boolean channel_status)
    {
        this.channel_name=channel_name;
        this.channel_cover=channel_cover;
        this.channel_stream=channel_stream;
        this.channel_status=channel_status;
    }
}
