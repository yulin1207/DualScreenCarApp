package com.example.dualscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dualscreen.R;
import com.example.dualscreen.util.DisplayManagerHelper;

/**
 * 媒体播放Activity
 * 模拟音乐播放功能
 */
public class MediaActivity extends AppCompatActivity {
    private static final String TAG = "MediaActivity";

    private ImageView mIvAlbumArt;
    private TextView mTvSongTitle;
    private TextView mTvArtist;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private SeekBar mSeekBar;
    private ImageView mBtnPlayPause;
    private ImageView mBtnPrev;
    private ImageView mBtnNext;

    private Handler mHandler;
    private boolean mIsPlaying = false;
    private int mCurrentProgress = 0;
    private int mTotalDuration = 245; // 4:05 in seconds

    private final String[][] SONGS = {
            {"晴天", "周杰伦"},
            {"稻香", "周杰伦"},
            {"夜曲", "周杰伦"},
            {"告白气球", "周杰伦"},
            {"七里香", "周杰伦"}
    };
    private int mCurrentSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mHandler = new Handler(Looper.getMainLooper());

        initViews();
        initListeners();
        updateSongInfo();
    }

    private void initViews() {
        mIvAlbumArt = findViewById(R.id.iv_album_art);
        mTvSongTitle = findViewById(R.id.tv_song_title);
        mTvArtist = findViewById(R.id.tv_artist);
        mTvCurrentTime = findViewById(R.id.tv_current_time);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        mSeekBar = findViewById(R.id.seek_bar);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnPrev = findViewById(R.id.btn_prev);
        mBtnNext = findViewById(R.id.btn_next);

        mSeekBar.setMax(mTotalDuration);
        mTvTotalTime.setText(formatTime(mTotalDuration));
    }

    private void initListeners() {
        findViewById(R.id.btn_media_back).setOnClickListener(v -> finish());

        mBtnPlayPause.setOnClickListener(v -> togglePlayPause());

        mBtnPrev.setOnClickListener(v -> playPrevious());

        mBtnNext.setOnClickListener(v -> playNext());

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                    mTvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void togglePlayPause() {
        mIsPlaying = !mIsPlaying;
        if (mIsPlaying) {
            mBtnPlayPause.setImageResource(R.drawable.ic_pause);
            startPlayback();
        } else {
            mBtnPlayPause.setImageResource(R.drawable.ic_play);
            stopPlayback();
        }
        sendMediaUpdate();
    }

    private void startPlayback() {
        Runnable playbackRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsPlaying) {
                    mCurrentProgress++;
                    if (mCurrentProgress >= mTotalDuration) {
                        mCurrentProgress = 0;
                        playNext();
                    }
                    mSeekBar.setProgress(mCurrentProgress);
                    mTvCurrentTime.setText(formatTime(mCurrentProgress));
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        mHandler.post(playbackRunnable);
    }

    private void stopPlayback() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void playPrevious() {
        mCurrentSongIndex--;
        if (mCurrentSongIndex < 0) {
            mCurrentSongIndex = SONGS.length - 1;
        }
        mCurrentProgress = 0;
        updateSongInfo();
        sendMediaUpdate();
    }

    private void playNext() {
        mCurrentSongIndex++;
        if (mCurrentSongIndex >= SONGS.length) {
            mCurrentSongIndex = 0;
        }
        mCurrentProgress = 0;
        updateSongInfo();
        sendMediaUpdate();
    }

    private void updateSongInfo() {
        String[] song = SONGS[mCurrentSongIndex];
        mTvSongTitle.setText(song[0]);
        mTvArtist.setText(song[1]);
        mTvCurrentTime.setText(formatTime(mCurrentProgress));
    }

    private void sendMediaUpdate() {
        Intent intent = new Intent(DisplayManagerHelper.ACTION_MEDIA_UPDATE);
        intent.putExtra(DisplayManagerHelper.EXTRA_MEDIA_TITLE, SONGS[mCurrentSongIndex][0]);
        intent.putExtra(DisplayManagerHelper.EXTRA_MEDIA_ARTIST, SONGS[mCurrentSongIndex][1]);
        intent.putExtra(DisplayManagerHelper.EXTRA_MEDIA_PROGRESS, mCurrentProgress);
        sendBroadcast(intent);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayback();
    }
}
