package com.melodygram.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.melodygram.R;
import com.melodygram.adapter.MusicListAdapter;
import com.melodygram.chatinterface.MusicMergeInterface;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.Music;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by LALIT on 21-07-2016.
 */
public class MusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ListView listView;
    private ArrayList<Music> musicList;
    private TextView titleTextView, musicTitleTextView, musicTotalTime, musicRunningTime;
    private MusicListAdapter musicListAdapter;
    private SeekBar musicSeekbar;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private MediaRecorder recorder;
    private String recoredfileUrl, musicFileUrl;
    private String extension = ".wav";
    private int currrnetPosition = -1;
    private MusicMergeInterface musicMergeInterface;
    private ImageView nextButton, previousButton, recordButton;
    private Chronometer chronometer;
    private boolean recording;
    private LinearLayout reviewSend;
    private Dialog progressDialog;
    Handler handler;
    Runnable myRunnable;
    String selectedMusicName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.music_list_layout, null);
        initView(view);
        loadFFMpegBinary();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            musicMergeInterface = (MusicMergeInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CallbackInterface ");
        }
    }

    @Override
    public void onDetach() {
        musicMergeInterface = null;
        super.onDetach();
    }

    private void initView(View view) {
        mediaPlayer = new MediaPlayer();
        musicList = new ArrayList<>();

        handler = new Handler();
        reviewSend = (LinearLayout) view.findViewById(R.id.review_send);
        chronometer = (Chronometer) view.findViewById(R.id.chronometer_text);
        listView = (ListView) view.findViewById(R.id.music_list);
        titleTextView = (TextView) view.findViewById(R.id.title);
        musicTitleTextView = (TextView) view.findViewById(R.id.music_title);
        musicTotalTime = (TextView) view.findViewById(R.id.music_running_time);
        musicRunningTime = (TextView) view.findViewById(R.id.music_total_time);
        recordButton = (ImageView) view.findViewById(R.id.record_button);
        view.findViewById(R.id.preview_music).setOnClickListener(this);
        view.findViewById(R.id.send_music).setOnClickListener(this);


        musicTitleTextView.setText("Add a Background Music");
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (currrnetPosition != -1) {
                            musicTitleTextView.setText(selectedMusicName);

                            if (!recording) {
                                recording = true;
                                stopMusic();
                                recordButton.setImageResource(R.drawable.sender_stop);
                                reviewSend.setVisibility(View.INVISIBLE);
                                startRecording();
                            } else {

                                stopRecording();
                                if (currrnetPosition != -1 && recordedAudio != null) {
                                    mergeAudio(musicList.get(currrnetPosition).getLocalPath(), recordedAudio.getAbsolutePath());
                                }

                                recording = false;
                            }
                        } else {
                            Toast.makeText(getActivity(), "Select sound", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
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
        nextButton = (ImageView) view.findViewById(R.id.next_button);
        previousButton = (ImageView) view.findViewById(R.id.previous_button);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        musicSeekbar = (SeekBar) view.findViewById(R.id.music_seekbar);
        musicSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        musicSeekbar.setOnSeekBarChangeListener(this);
        listView.setOnItemClickListener(this);
        getAllMusicCatList();
    }


    private void getAllMusicCatList() {
        musicList.clear();
        MusicDataSource musicDataSource = new MusicDataSource(getActivity());
        musicDataSource.open();
        //   ArrayList<Music> list = musicDataSource.getCatMusic(getArguments().getString("CatId"));
        ArrayList<Music> list = musicDataSource.getCatMusic("1");
        musicList.addAll(list);
        musicDataSource.close();
        titleTextView.setText(getArguments().getString("CatName") + "(" + list.size() + " Tracks)");
        musicListAdapter = new MusicListAdapter(musicList, getActivity());
        listView.setAdapter(musicListAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        musicTitleTextView.setText("Record your voice");
        reviewSend.setVisibility(View.INVISIBLE);
        recordButton.setImageResource(R.drawable.microphone_music);
        musicListAdapter.setCurrentMusic(position);
        chronometer.setBase(SystemClock.elapsedRealtime());
        Music music = musicList.get(position);
        selectedMusicName =music.getName();
        recording = false;
        stopRecording();
        final File file = new File(music.getLocalPath());
        previewMusic(file, position);
        musicListAdapter.notifyDataSetChanged();
    }
    private Runnable updateSongTime = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            musicTotalTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                    TimeUnit.MILLISECONDS.toSeconds(totalDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(totalDuration))));
            musicRunningTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(currentDuration),
                    TimeUnit.MILLISECONDS.toSeconds(currentDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(currentDuration))));
            int progress = (CommonUtil.getProgressPercentage(currentDuration, totalDuration));
            musicSeekbar.setProgress(progress);
            myHandler.postDelayed(this, 100);
        }
    };


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
        recorder.setMaxDuration(1200000);
        try {
            recorder.prepare();
            recorder.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            chronometer.start();
            chronometer.setVisibility(View.VISIBLE);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        myRunnable = new Runnable() {
            public void run() {
                // do something
                reviewSend.setVisibility(View.VISIBLE);
                stopRecording();
                if (currrnetPosition != -1 && recordedAudio != null)
                    mergeAudio(musicList.get(currrnetPosition).getLocalPath(), recordedAudio.getAbsolutePath());
                recording = false;


            }
        };
        handler.postDelayed(myRunnable, 31000);

    }

    File recordedAudio;

    public void stopRecording() {
        recordedAudio = null;
        if(myRunnable!=null)
        {
            handler.removeCallbacks(myRunnable);
        }

        if (null != recorder) {
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recordedAudio = new File(musicFileUrl);

                recorder = null;
                chronometer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getFilename() {
        File mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data/" + getActivity().getPackageName());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale(
                "en")).format(new Date());
        musicFileUrl = mediaStorageDir.getPath() + File.separator + timeStamp + "." + extension;
        return musicFileUrl;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_button:
                if (currrnetPosition != 0) {
                    --currrnetPosition;
                    listView.performItemClick(listView.getAdapter().getView(currrnetPosition, null, null), currrnetPosition, listView.getItemIdAtPosition(currrnetPosition));
                } else {
                    currrnetPosition = musicList.size() - 1;
                    listView.performItemClick(listView.getAdapter().getView(currrnetPosition, null, null), currrnetPosition, listView.getItemIdAtPosition(currrnetPosition));
                }
                break;
            case R.id.next_button:
                if (currrnetPosition != musicList.size() - 1) {
                    ++currrnetPosition;
                    listView.performItemClick(listView.getAdapter().getView(currrnetPosition, null, null), currrnetPosition, listView.getItemIdAtPosition(currrnetPosition));
                } else {
                    currrnetPosition = 0;
                    listView.performItemClick(listView.getAdapter().getView(currrnetPosition, null, null), currrnetPosition, listView.getItemIdAtPosition(currrnetPosition));
                }
                break;

            case R.id.preview_music:
                if(recoredfileUrl!=null) {
                    File outputFile = new File(recoredfileUrl);
                    previewMusic(outputFile, -1);
                }
                break;
            case R.id.send_music:
                stopMusic();
                File outputFileSend = new File(recoredfileUrl);
                musicMergeInterface.sendMusic(outputFileSend.getAbsolutePath());
                break;
        }
    }

    private String getMusicFilename() {
        File mediaStorageDir = new File(GlobalState.ONE_TO_ONE_AUDIO,"/Android/data/" + getActivity().getPackageName());
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", new Locale(
                "en")).format(new Date());
        recoredfileUrl = mediaStorageDir + File.separator
                + "mergedaudio_%" + timeStamp + ".wav";
        return recoredfileUrl;
    }


    private void loadFFMpegBinary() {
        FFmpeg ffmpeg = FFmpeg.getInstance(getActivity());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.v("onFailure", "onFailure");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Log.v("onFailure", "onFailure");
        }
    }


    private void previewMusic(File musicFile, int position) {
        try {
            stopMusic();
            final File file = new File(musicFile.getAbsolutePath());
            if (file.isFile()) {
                if (position != -1)
                    currrnetPosition = position;
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getActivity(),
                        Uri.fromFile(file));
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        long finalTime = mediaPlayer.getDuration();
                        musicSeekbar.setMax(100);
                        musicTotalTime.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                                TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes(finalTime))));
                        myHandler.postDelayed(updateSongTime, 100);
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        musicTotalTime.setText(CommonUtil
                                .mediaDurationFormat(CommonUtil
                                        .mediaToDurationPath(file
                                                .getAbsolutePath())));
                        musicRunningTime.setText("00:00");
                        myHandler.removeCallbacks(updateSongTime);
                        musicSeekbar.setProgress(0);
                    }
                });
            } else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_missing), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopMusic() {
        try {
            if (mediaPlayer != null ) {
                myHandler.removeCallbacks(updateSongTime);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            if (mediaPlayer != null) {
                myHandler.removeCallbacks(updateSongTime);
                mediaPlayer.release();
                e.printStackTrace();
            }
        }
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(getActivity());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.merging_popup_layout);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissDialogAct() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void mergeAudio(String beatFile, String voiceFile) {
        String files = "-i " + voiceFile + " -i " + beatFile;
        String filter = " -filter_complex [0:a]volume=1[a1];[1:a]volume=0.5[a2];[a1][a2]amix=inputs=2:duration=first:dropout_transition=3";
        String output = " -c:a pcm_s16le -vn -dn -sn -strict -2 " + getMusicFilename();
        String cmd = files + filter + output;
        // String filter = " -filter_complex amix=inputs=2:duration=first:dropout_transition=3"; well
        //String filter = " -filter_complex [0:a]volume=0.99[a1];[1:a]volume=0.3[a2];[a1][a2]amix=inputs=2,volume=1.3,pan=stereo|c0<c0+c2|c1<c1+c3[out]";
        Log.v("FFMPEG CMD", cmd);
        String[] command = cmd.split(" ");
        FFmpeg ffmpeg = FFmpeg.getInstance(getActivity());
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    showProgressDialog();
                }

                @Override
                public void onProgress(String message) {
                    Log.v("onProgress", message);
                }

                @Override
                public void onFailure(String message) {
                    dismissDialogAct();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_melody), Toast.LENGTH_SHORT).show();
                    Log.v("onFailure", message);
                }

                @Override
                public void onSuccess(String message) {
                    dismissDialogAct();
                    if (!chronometer.getText().equals("00:00")) {
                        recordButton.setImageResource(R.drawable.microphone_music);
                        reviewSend.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.melody_create), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFinish() {
                    dismissDialogAct();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            dismissDialogAct();
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(updateSongTime);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            myHandler.removeCallbacks(updateSongTime);
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = CommonUtil.progressToTimer(seekBar.getProgress(), totalDuration);
            // forward or backward to certain seconds
            mediaPlayer.seekTo(currentPosition);
            myHandler.postDelayed(updateSongTime, 100);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
