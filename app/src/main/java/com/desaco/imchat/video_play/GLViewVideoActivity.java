package com.desaco.imchat.video_play;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.desaco.imchat.R;
import com.desaco.imchat.utils.CommonUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * https://github.com/ChouRay/PlayVideo-OpenGL
 *
 * 分离了OpenGL Shader着色器代码
 */
public class GLViewVideoActivity extends Activity implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, View.OnClickListener {
        //
    public static final String videoPath = Environment.getExternalStorageDirectory().getPath() + "/Movies/不将就.mp4";

    private boolean frameAvailable = false;

    private static short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private Context context;

    // Texture to be shown in backgrund
    private FloatBuffer textureBuffer;
    private static float squareSize = 1.0f;
    private static float squareCoords[] = {
            -squareSize, squareSize,   // top left
            -squareSize, -squareSize,   // bottom left
            squareSize, -squareSize,    // bottom right
            squareSize, squareSize}; // top right
    private float textureCoords[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};

    private int width, height;

    private SurfaceTexture videoTexture;
    private GLSurfaceView glView;
    private MediaPlayer mediaPlayer;

    private FrameLayout mRootFLayout;
    private ImageView mFullHalfIv;
    private LinearLayout mRootLayoutGL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glview_video);
        context = this;

        initView();

        playVideo();
        Toast.makeText(this, "马上开始播放！", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mRootFLayout = (FrameLayout) findViewById(R.id.root_layout);
        glView = (GLSurfaceView) findViewById(R.id.surface_view);
        mFullHalfIv = (ImageView) findViewById(R.id.full_half_iv);

        mRootLayoutGL = (LinearLayout) findViewById(R.id.gl_root_llayout);
        mFullHalfIv.setOnClickListener(this);
//        glView = new GLSurfaceView(this);
//        setContentView(glView);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_half_iv://横竖屏
                if (isHorizontalScreen) {//横屏切竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isHorizontalScreen = false;
                } else {//竖屏切横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isHorizontalScreen = true;
                }
                break;
        }
    }

    private boolean isHorizontalScreen;//true为横屏；false为竖屏

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {//横屏切竖屏
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.width = CommonUtils.getScreenWidth(context);
            params.height = CommonUtils.getScreenWidth(context) * 9 / 16;
            mRootLayoutGL.setLayoutParams(params);
            mFullHalfIv.setImageResource(R.mipmap.icon_full);
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {//竖屏切横屏
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.width = CommonUtils.getScreenWidth(context);
            params.height = CommonUtils.getScreenHeight(context);
            mRootLayoutGL.setLayoutParams(params);
            mFullHalfIv.setImageResource(R.mipmap.icon_video_in);
        }
    }

    private void playVideo() {//TODO
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            try {
                //http://video.netwin.cn/9e0e1e46a4d3493d9d6111a4ac0b8d12/193234ee930947478049edab17ac91ac-a5b7d8911cc7d347a9c9dd7e9b1d521b.mp4
                // http://www.w3school.com.cn/example/html5/mov_bbb.mp4
//                String videoPath = "http://video.netwin.cn/9e0e1e46a4d3493d9d6111a4ac0b8d12/193234ee930947478049edab17ac91ac-a5b7d8911cc7d347a9c9dd7e9b1d521b.mp4";
                String videoPath = "http://video.netwin.cn/9e0e1e46a4d3493d9d6111a4ac0b8d12/193234ee930947478049edab17ac91ac-a5b7d8911cc7d347a9c9dd7e9b1d521b.mp4";
                mediaPlayer.setDataSource(videoPath);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private GLShader mGLShader;
    private int[] textures = new int[1];
    private float[] videoTextureTransform = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e("desaco", "1.onSurfaceCreated 屏幕创建！");

        mGLShader = new GLShader(context,textures);
        videoTexture = new SurfaceTexture(textures[0]);
        videoTexture.setOnFrameAvailableListener(this);

        Surface surface = new Surface(videoTexture);
        mediaPlayer.setSurface(surface);
        surface.release();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("desaco", "onSurfaceChanged 屏幕改变！");
        if (isHorizontalScreen) {
            this.width = CommonUtils.getScreenWidth(context);
            this.height = CommonUtils.getScreenHeight(context);

            glView.getLayoutParams().width = this.width;
            glView.getLayoutParams().height = this.height;
        } else {
            this.width = CommonUtils.getScreenWidth(context);
            this.height = CommonUtils.getScreenWidth(context) * 9 / 16;

            glView.getLayoutParams().width = this.width;
            glView.getLayoutParams().height = this.height;
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //com.asha.md360player4android
        Log.e("desaco", "onDrawFrame 渲染视频！");
        synchronized (this) {
            if (frameAvailable) {
                videoTexture.updateTexImage();
                videoTexture.getTransformMatrix(videoTextureTransform);
                frameAvailable = false;
            }
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glViewport(0, 0, width, height);//TODO

        mGLShader.drawTexture(videoTextureTransform);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        Log.e("desaco", "onFrameAvailable");
        synchronized (this) {
            frameAvailable = true;
        }
    }
}
