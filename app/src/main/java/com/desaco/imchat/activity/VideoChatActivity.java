package com.desaco.imchat.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.desaco.imchat.R;
import com.desaco.imchat.video.CameraGLSurfaceView;

import java.io.IOException;


/**
 * <p>
 * Android OpenGL渲染双视频（类微信视频聊天）- https://github.com/296777513/AndroidOpenGL
 * 视频聊天的推流和拉流，两人视频和多人视频聊天实现
 * 一个窗口播放视频，一个视频窗口推流(预览视频，推流)
 */
public class VideoChatActivity extends Activity implements View.OnClickListener {

    CameraGLSurfaceView mCameraGLSurfaceView;
    Button mSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.camera_gl_surface_view);
        mSwitchBtn = (Button) findViewById(R.id.switch_camera);
        mSwitchBtn.setOnClickListener(this);
    }

    private MediaPlayer mediaPlayer;

    public void play() {
        mediaPlayer = new MediaPlayer();
//        mVideoPathUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.tusdk_sample_splice_video);
        String url = "http://www.w3school.com.cn/example/html5/mov_bbb.mp4";

        try {
//            mediaPlayer.setDataSource(this, mVideoPathUri);
            mediaPlayer.setDataSource(url);
//            mediaPlayer.setSurface(mCameraGLSurfaceView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mCameraGLSurfaceView.bringToFront();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mCameraGLSurfaceView.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_camera:
                mCameraGLSurfaceView.switchCamera();
                break;
            default:
                break;
        }

    }
}
