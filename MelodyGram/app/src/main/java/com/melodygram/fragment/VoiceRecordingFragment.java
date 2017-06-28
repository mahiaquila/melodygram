package com.melodygram.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.melodygram.R;
import com.melodygram.activity.ChatActivity;
import com.melodygram.activity.MusicFragmentActivity;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.model.Sticker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;




public class VoiceRecordingFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private MediaRecorder recorder;
    private Chronometer chronometer;
    private ImageView recordButton;
    private String fileUrl;
    private AudioInterface audioInterface;
    private LinearLayout buttonParent;
    private String extension = "opus";
    boolean isCheckDown,isCheckUp;
    Handler handler;
    Runnable   myRunnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.voice_recording_layout, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        handler = new Handler();
        recordButton = (ImageView) view.findViewById(R.id.record_button_active);
        chronometer = (Chronometer) view.findViewById(R.id.chronometer_text);
        buttonParent = (LinearLayout) view.findViewById(R.id.button_parent);
        view.findViewById(R.id.send_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_button).setOnClickListener(this);
        view.findViewById(R.id.add_music).setOnClickListener(this);
        handler = new Handler();

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        isCheckDown = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isCheckDown) {
                                    recordButton.setImageResource(R.drawable.recording_bottom);
                                    buttonParent.setVisibility(View.GONE);
                                    startRecording();
                                    audioInterface.stopStartPinger(0);
                                    handler.postDelayed(myRunnable, 91000);
                                }
                            }
                        }, 200);

                        break;
                    case MotionEvent.ACTION_UP:
                        isCheckDown = true;
                        stopRecording();
                        audioInterface.stopStartPinger(1);
                        if (!chronometer.getText().equals("00:00")) {
                            recordButton.setImageResource(R.drawable.recorder_button_inactive);
                            buttonParent.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;

                }

                return true;
            }
        });


        myRunnable = new Runnable() {
            public void run() {
                // do something
                try {
                    handler.removeCallbacks(myRunnable);
                    stopRecording();
                    audioInterface.stopStartPinger(1);
                    if (!chronometer.getText().equals("00:00")) {
                        recordButton.setImageResource(R.drawable.recorder_button_inactive);
                        buttonParent.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {

            }
        });
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {

            }
        });
        recorder.setMaxDuration(12000000);
        try {
            recorder.prepare();
            recorder.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            chronometer.start();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File recordedAudio;

    private void stopRecording() {
        recordedAudio = null;
        if (null != recorder) {
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recordedAudio = new File(fileUrl);
                recorder = null;
                chronometer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getFilename() {
        try {
            File mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data/" + getActivity().getPackageName());
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale(
                    "en")).format(new Date());
            fileUrl = mediaStorageDir.getPath() + File.separator
                    + "record_aud" + timeStamp + "." + extension;

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return fileUrl;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getActivity() instanceof AudioInterface) {
            audioInterface = (AudioInterface) getActivity();
        } else if (getParentFragment() instanceof AudioInterface) {
            audioInterface = (AudioInterface) getParentFragment();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + AudioInterface.class.getSimpleName());
        }


    }

    @Override
    public void onDetach() {
        audioInterface = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                audioInterface.sendAudioFile(recordedAudio, ChatGlobalStates.AUDIO_TYPE, extension, fileUrl);
                resetUI();
                break;
            case R.id.cancel_button:
                stopRecording();
                resetUI();
                break;
            case R.id.add_music:
                Intent intent = new Intent((ChatActivity)getActivity(), MusicFragmentActivity.class);
                intent.putExtra("userName", getArguments().getString("userName"));
                startActivityForResult(intent,GlobalState.REQUEST_MUSIC_FILE);
                break;
        }
    }

    private void resetUI() {
        recordButton.setImageResource(R.drawable.recorder_button_inactive);
        buttonParent.setVisibility(View.GONE);
        chronometer.setText("00:00");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case GlobalState.REQUEST_MUSIC_FILE:
                    String musicFilePath = data.getStringExtra("filePath");
                    if(musicFilePath !=null)
                    {
                        File audioFile = new File(musicFilePath);


                        if(audioFile.isFile())
                        {
                            audioInterface.sendAudioFile(audioFile, ChatGlobalStates.MUSIC_TYPE, extension, musicFilePath);
                        }

                    }
            }
        }
    }


    public interface AudioInterface {
        void stopStartPinger(int value);

        void sendAudioFile(File file, String type, String extension, String path);
    }
}
