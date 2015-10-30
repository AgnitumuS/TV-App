package com.example.marti.tv;

/**
 * Created by MortadhaS on 10/3/2015.
 **/
public class Channel
{

    String channel_name;
    String channel_stream;
    String channel_cover;
    String channel_type;
    Boolean channel_status;
    String channel_source;

    Channel(String channel_name, String channel_stream, String channel_cover,String channel_type,String channel_source,Boolean channel_status)
    {
        this.channel_name=channel_name;
        this.channel_cover=channel_cover;
        this.channel_stream=channel_stream;
        this.channel_status=channel_status;
        this.channel_type=channel_type;
        this.channel_source=channel_source;
    }


}
