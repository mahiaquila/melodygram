package com.melodygram.model;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

/**
 * Created by LALIT on 22-07-2016.
 */
public class MediaPlayerTag {


    RelativeLayout playPauseParent;
    ImageView audioStop;
    ImageView audioPlay;
    TextView audioTime;
    SeekBar seekBar;
    File audioFile;


    public File getFile()
    {
        return this.audioFile;
    }

    public void setFile(File audioFile)
    {
        this.audioFile = audioFile;
    }


    public ImageView getPlayImage()
    {
        return this.audioPlay;
    }

    public void setPlayImage(ImageView audioPlay)
    {
        this.audioPlay = audioPlay;
    }


    public ImageView getStopImage()
    {
        return this.audioStop;
    }

    public void setStopImage(ImageView audioStop)
    {
        this.audioStop = audioStop;
    }


    public TextView getAudioTime()
    {
        return this.audioTime;
    }

    public void setAudioTime(TextView audioTime)
    {
        this.audioTime = audioTime;
    }

    public SeekBar getSeekBar()
    {
        return this.seekBar;
    }

    public void setSeekBar(SeekBar seekBar)
    {
        this.seekBar = seekBar;
    }

    public RelativeLayout getPlayPauseParent()
    {
        return this.playPauseParent;
    }

    public void setPlayPauseParent( RelativeLayout playPauseParent)
    {
        this.playPauseParent = playPauseParent;
    }
}
