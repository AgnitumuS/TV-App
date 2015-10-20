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

    public String getChannel_stream() {
        return channel_stream;
    }

    public String getChannel_cover() {
        return channel_cover;
    }

    public Boolean getChannel_status() {
        return channel_status;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public void setChannel_stream(String channel_stream) {
        this.channel_stream = channel_stream;
    }

    public void setChannel_cover(String channel_cover) {
        this.channel_cover = channel_cover;
    }

    public void setChannel_status(Boolean channel_status) {
        this.channel_status = channel_status;
    }
}
