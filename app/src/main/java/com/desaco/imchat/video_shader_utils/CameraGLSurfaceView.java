package com.desaco.imchat.video_shader_utils;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.ViewConfiguration;


import com.desaco.imchat.utils.DisplayUtil;
import com.desaco.imchat.utils.GlUtil;
import com.desaco.imchat.utils.LogUtils;
import com.desaco.imchat.video_shader_utils.gles.DirectDrawerPreviewShader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 * <p>
 * 可以看成视频的推流
 */
public class CameraGLSurfaceView extends GLSurfaceView implements Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private SurfaceTexture mSurface;
    private SurfaceTexture mSamllSurface;

    private int mTextureID = -1;
    private int mBitmapTextureID = -1;

    private DirectDrawerPreviewShader mDirectDrawer;
    private DirectDrawerPreviewShader mBitmapDirectDrawer;

    private TextureResources mTextureResources;

    // 小视频的高度
    private float mThumbnailHeight;
    // 小视频的宽度
    private float mThumbnailWidth;

    // 记录小视频的坐标
    private RectF mThumbnailRect;
    // 屏幕的宽度
    private float mScreenWidth;
    // 屏幕的高度
    private float mScreenHeight;
    //距离屏幕的最小距离
    private int mMargin;
    //最小的滑动距离
    private int mTouchSlop;

    // 标识符，判断手指按下的范围是否在小视频的坐标内
    private boolean mTouchThumbnail = false;

    // 标识符，判断手指是移动小视频而不是点击小视频
    private boolean isMoveThumbnail = false;
    // 按下时手指的x坐标值
    private float mDownX = 0;
    // 按下时手指的y坐标值
    private float mDownY = 0;

    private float mLastYLength = 0;
    private float mLastXLength = 0;

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        // 设置OpenGl ES的版本为2.0
        setEGLContextClientVersion(2);
        // 设置与当前GLSurfaceView绑定的Renderer
        setRenderer(this);
        // 设置渲染的模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        mDirectDrawersList = new ArrayList<>();

        mScreenWidth = DisplayUtil.getScreenWidthPixels(mContext);
        mScreenHeight = DisplayUtil.getScreenHeightPixels(mContext);

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();//最小的滑动距离
        mThumbnailWidth = mScreenWidth / 4f;//4f
        mThumbnailHeight = mScreenHeight / 4f;//4f

        mMargin = DisplayUtil.dip2px(mContext, 2);
        mThumbnailRect = new RectF(mMargin,
                (mScreenHeight - mMargin), (mMargin + mThumbnailWidth), (mScreenHeight - mMargin - mThumbnailHeight));

        mTextureResources = TextureResources.getInstance();
    }

    private List<DirectDrawerPreviewShader> mDirectDrawersList;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.logI("onSurfaceCreated...");

        mTextureID = GlUtil.createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);


        //初始化时，视屏大窗口，播放视频
        mDirectDrawer = new DirectDrawerPreviewShader(mTextureID);
        mDirectDrawer.setFromCamera(true);

        //TODO 开启手机后摄像头
        CameraCapture.get().openBackCamera();
        //TODO 开启手机前置摄像头
//        CameraCapture.get().openFrontCamera();

        //初始化时，小窗口获取的图片，预览视频 推流
        mBitmapTextureID = GlUtil.loadTexture(mTextureResources.getPicBitmap());
        //----------- TODO
//        mBitmapTextureID = GlUtil.createTextureID();
//        mSamllSurface = new SurfaceTexture(mBitmapTextureID);
//        mSamllSurface.setOnFrameAvailableListener(this);
        //-------------
        mBitmapDirectDrawer = new DirectDrawerPreviewShader(mBitmapTextureID);
        mBitmapDirectDrawer.setFromCamera(false);

        mDirectDrawersList.add(mDirectDrawer);
        mDirectDrawersList.add(mBitmapDirectDrawer);

        LogUtils.logI("mTextureID: " + mBitmapTextureID);
        LogUtils.logI("mTextureID: " + mTextureID);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.logI("onSurfaceChanged...");
        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
        GLES20.glViewport(0, 0, width, height);
        if (!CameraCapture.get().isPreviewing()) {
            //使用TextureView预览Camera
            CameraCapture.get().doStartPreview(mSurface);
        }

//        playVideo();
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.logI("onDrawFrame...");
        // 设置白色为清屏
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 更新纹理
        mSurface.updateTexImage();

        //
//        mSamllSurface.updateTexImage();

        // mDirectDrawers中有两个对象，一个是绘制Camera传递过来的数据，一个是绘制由bitmap转换成的纹理
        for (int i = 0; i < mDirectDrawersList.size(); i++) {
            DirectDrawerPreviewShader directDrawer = mDirectDrawersList.get(i);
            if (i == 0) {
                directDrawer.resetMatrix();
            } else {
                directDrawer.calculateMatrix(mThumbnailRect, mScreenWidth, mScreenHeight);
            }
            directDrawer.draw();
        }

        //TODO 更新窗口的视频纹理
//        float[] mtx = new float[16];
//        mSamllSurface.getTransformMatrix(mtx);
//        mSamllSurface.updateTexImage();
//        mBitmapDirectDrawer.draw();

    }
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        LogUtils.logI("onFrameAvailable...");
        this.requestRender();
    }

    private MediaPlayer mediaPlayer;

    public void pauseVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void startVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void stopAndReleaseVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.setSurface(null);
            mediaPlayer.release();
            mediaPlayer = null;
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
            Surface surface = new Surface(mSamllSurface);
            mediaPlayer.setSurface(surface);
            surface.release();
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




    /**
     * 移动小视频
     *
     * @param rectF   小视频的坐标
     * @param lengthY 在Y轴移动的距离
     * @param lengthX 在X轴移动的距离
     */
    public void moveView(RectF rectF, float lengthY, float lengthX) {//TODO 绘制小窗口
        rectF.top = rectF.top - (lengthY - mLastYLength);
        rectF.bottom = rectF.bottom - (lengthY - mLastYLength);
        rectF.left = rectF.left + (lengthX - mLastXLength);
        rectF.right = rectF.right + (lengthX - mLastXLength);

        if (rectF.top > (mScreenHeight - mMargin)) {
            rectF.top = mScreenHeight - mMargin;
            rectF.bottom = rectF.top - mThumbnailHeight;
        }

        if (rectF.bottom < mMargin) {
            rectF.bottom = mMargin * 1f;
            rectF.top = rectF.bottom + mThumbnailHeight;
        }

        if (rectF.right > (mScreenWidth - mMargin)) {
            rectF.right = mScreenWidth - mMargin;
            rectF.left = rectF.right - mThumbnailWidth;
        }

        if (rectF.left < mMargin) {
            rectF.left = mMargin;
            rectF.right = rectF.left + mThumbnailWidth;
        }

        mLastYLength = lengthY;
        mLastXLength = lengthX;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                if (mDownX > mThumbnailRect.left && mDownX < mThumbnailRect.right
                        && mDownY > mThumbnailRect.bottom && mDownY < mThumbnailRect.top) {
                    mTouchThumbnail = true;
                    mLastYLength = 0;
                    mLastXLength = 0;
                    return true;
                } else {
                    mTouchThumbnail = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if (mTouchThumbnail) {
                    float lengthX = Math.abs(mDownX - moveX);
                    float lengthY = Math.abs(mDownY - moveY);
                    float length = (float) Math.sqrt(Math.pow(lengthX, 2) + Math.pow(lengthY, 2));
                    if (length > mTouchSlop) {
                        moveView(mThumbnailRect, mDownY - moveY, moveX - mDownX);
                        isMoveThumbnail = true;
                    } else {
                        isMoveThumbnail = false;
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchThumbnail) {
                    mLastYLength = 0;
                    mLastXLength = 0;
                    //抬起手指时，如果不是移动小视频，那么就是点击小视频
                    if (!isMoveThumbnail) {
                        changeThumbnailPosition();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void changeThumbnailPosition() {
        DirectDrawerPreviewShader directDrawer = mDirectDrawersList.remove(mDirectDrawersList.size() - 1);
        mDirectDrawersList.add(0, directDrawer);
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraCapture.get().doStopCamera();
    }




    public void switchCamera() {
        CameraCapture.get().switchCamera(1);
        mDirectDrawer.setBackCamera(CameraCapture.get().isOpenBackCamera());
    }

}