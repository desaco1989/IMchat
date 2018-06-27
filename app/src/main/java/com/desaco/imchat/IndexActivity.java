package com.desaco.imchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.desaco.imchat.activity.VideoChatActivity;
import com.desaco.imchat.video_chat.VideoChatActivity1;

/**
 * Created by desaco on 2018/6/26.
 * 利用FFmpeg视频录制微信小视频与其压缩处理- https://github.com/mabeijianxi/small-video-record
 * <p>
 * Android OpenGL渲染双视频（类微信视频聊天）- https://github.com/296777513/AndroidOpenGL
 */

public class IndexActivity extends Activity implements View.OnClickListener {
    //
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        //
        Button previewVideo = (Button) findViewById(R.id.preview_video);
        previewVideo.setOnClickListener(this);

        Button chatVideo = (Button) findViewById(R.id.video_chat);
        chatVideo.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_video:
                //VideoChatActivity
                jump(VideoChatActivity.class);
                break;
            case R.id.video_chat:
                //
                jump(VideoChatActivity1.class);
                break;
            default:
                break;
        }
    }

    private void jump(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(mContext, clazz);
        startActivity(intent);
    }
}
