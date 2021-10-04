package com.cactusuk.cactus_dance_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity  {

    private final int MY_PERMISSION_REQUEST_RECORD_AUDIO = 1;
    private final int MY_PERMISSION_REQUEST_WRITE_STORAGE = 1;
    private MediaPlayer media;
    Context context;
    private InterstitialAd mInterstitialAd;


    private int[] soundIndex = {
            R.raw.cactusong, R.raw.cactusong1, R.raw.cactusong2};
    int mCompleted = 0;


    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;

    private static final String TAG = "Something";

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;


    private MediaRecorder recorder = null;


    private MediaPlayer   player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private String audioFileName = null;

    MediaRecorder mediaRecorder;
    String pathSave = "";

    SoundPool sp;
    int explosion = 0;
    JcPlayerView jcplayerView;

    Uri uri;

    private AdView mAdView;

    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView mvideo = (VideoView) findViewById(R.id.launcherVideo);
        Button button=findViewById(R.id.btn);
        Button button1=findViewById(R.id.btn1);
        Button button2=findViewById(R.id.btn2);

         imageView=findViewById(R.id.image);

        Button next=findViewById(R.id.next);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest1);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-1871204129280578/5617886788");


        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-1871204129280578/4802550448", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.show(MainActivity.this);
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });



        final MediaPlayer mediaPlayer = MediaPlayer.create(this, soundIndex[0]);


        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO);
        int permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck2==PERMISSION_GRANTED){

        }else {

            // you don't have permission, try requesting for it

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    (MY_PERMISSION_REQUEST_WRITE_STORAGE));
        }

        if (permissionCheck == PERMISSION_GRANTED) {
            // you have the permission, proceed to record audio



        }
        else {

            // you don't have permission, try requesting for it

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    (MY_PERMISSION_REQUEST_RECORD_AUDIO));
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mvideo.pause();
                    Toast.makeText(MainActivity.this, "Video PLayer is Paused !", Toast.LENGTH_SHORT).show();
                    startRecording();
                }else{
                    startRecording();
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                stopRecording();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mCompleted++;
                        mp.reset();
                        if (mCompleted < soundIndex.length) {
                            try {
                                AssetFileDescriptor afd = getResources().openRawResourceFd(soundIndex[mCompleted+1]);
                                if (afd != null) {
                                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    afd.close();
                                    mp.prepare();
                                    mp.start();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else if (mCompleted >= soundIndex.length) {
                            mCompleted = 0;
                            try {
                                AssetFileDescriptor afd = getResources().openRawResourceFd(soundIndex[mCompleted+1]);
                                if (afd != null) {
                                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    afd.close();
                                    mp.prepare();
                                    mp.start();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            mCompleted = 0;
                            mp.release();
                            mp = null;
                        }

                    }
                });

                mediaPlayer.start();




            }
        });






//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//                this.getPackageName());




//        SpeechRecognitionListener listener = new SpeechRecognitionListener();
//        mSpeechRecognizer.setRecognitionListener(listener);
//
//        if (!mIslistening)
//        {
//            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//        }


        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

       // ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);








        String fileNamee = "android.resource://"+ getPackageName()+"/raw/cactus";
        Uri uri = Uri.parse(fileNamee);
        mvideo.setVideoURI(uri);
        mvideo.seekTo(5);







        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    Glide.with(getApplicationContext()).load(R.drawable.catusdancee).into(imageView);

                    mvideo.pause();
                    button.setText("Play");
                }else {
                    mediaPlayer.start();
                    mvideo.start();
                    Glide.with(getApplicationContext()).load(R.drawable.catusdance).into(imageView);

                    button.setText("Stop");
                }

            }
        });

        mvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0, 0);
                mp.setLooping(true);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCompleted++;
                mp.reset();
                if (mCompleted < soundIndex.length) {
                    try {
                        AssetFileDescriptor afd = getResources().openRawResourceFd(soundIndex[mCompleted]);
                        if (afd != null) {
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                            mp.prepare();
                            mp.start();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else if (mCompleted >= soundIndex.length) {
                    mCompleted = 0;
                    try {
                        AssetFileDescriptor afd = getResources().openRawResourceFd(soundIndex[mCompleted]);
                        if (afd != null) {
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                            mp.prepare();
                            mp.start();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    mCompleted = 0;
                    mp.release();
                    mp = null;
                }

            }
        });


    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onRecord(boolean start) {
        if (start) {
            startRecording();

        } else {
            stopRecording();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPlaying() {
        player = new MediaPlayer();

        Glide.with(getApplicationContext()).load(R.drawable.catusdance).into(imageView);

        MediaPlayer audioPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(audioFileName));
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        PlaybackParams params = new PlaybackParams();
        params.setPitch(1.5f);
        audioPlayer.setPlaybackParams(params);
        audioPlayer.start();
//        try {
//            //player.setDataSource(audioFileName);
//            player.prepare();
//            player.start();
//
//
//
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }

//        if (explosion!=0){
//
//            sp.play(explosion, 1,1,0,0,2.3f);
//        }
    }

    private void stopPlaying() {

        Glide.with(getApplicationContext()).load(R.drawable.catusdancee).into(imageView);
        player.release();
        player = null;
    }


    @SuppressLint("WrongConstant")
    private void startRecording() {
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setOutputFile(fileName);


        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/"
                + UUID.randomUUID().toString() + "_audio_record.mp3";
        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioFileName += "/recorded_audio.mp3";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioFileName);

        Log.d("RecordView", "onStart");

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();

        }




    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stopRecording() {
        try {


            mediaRecorder.stop();
            mediaRecorder = null;

//            sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
//            //explosion = sp.load(this, R.raw.hh,0);
//            explosion = sp.load(audioFileName,0);

            startPlaying();
        } catch (Exception e) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }



}