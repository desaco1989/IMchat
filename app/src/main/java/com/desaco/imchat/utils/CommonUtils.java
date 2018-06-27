package com.desaco.imchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;


/**
 * @version 1.0.0
 * @description:
 * @date: 2015-7-3 上午10:08:34
 * @author: wangqing
 */
public class CommonUtils {

    private static final String tag = CommonUtils.class.getSimpleName();

    /**
     * 网络类型
     **/
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    /**
     * 根据key获取config.properties里面的boolean值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBooleProperty(Context context, String key) {
        try {
            Properties props = new Properties();
            InputStream input = context.getAssets().open("config.properties");
            if (input != null) {
                props.load(input);
                if (props.getProperty(key).equals("true")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 根据key获取config.properties里面的值
     *
     * @param context
     * @param key
     * @return
     */
    public static String getProperty(Context context, String key) {
        try {
            Properties props = new Properties();
            InputStream input = context.getAssets().open("config.properties");
            if (input != null) {
                props.load(input);
                return props.getProperty(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static boolean isLiveStreamingAvailable() {
        // Todo: Please ask your app server, is the live streaming still available
        return true;
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType(Context context) {
        int netType = 1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


    /**
     * 判断SDCard是否存在,并可写
     *
     * @return
     */
    public static boolean checkSDCard() {
        String flag = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(flag)) {
            return true;
        }
        return false;
    }

    /**
     * 创建文件
     * 判断是否有SD卡，有的话创建并返回File
     *
     * @param filePath
     * @return
     */
//    public static File createFile(String filePath) {
//        if (!checkSDCard()) {
//            LogUtil.e("CommonUtils", "fail checkSDCard");
//            return null;
//        }
//        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filePath);
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        File dir = new File(appDir, formatCurrentDate("yyyy_MM_dd_HH_mm_ss") + ".jpg");
//        if (!dir.exists()) {
//            try {
//                if (!dir.getParentFile().exists()) {
//                    dir.getParentFile().mkdirs();
//                }
//                if (!dir.exists()) {
//                    dir.createNewFile();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            LogUtil.e("CommonUtils", "success createFile dir=" + dir.toString());
//            return dir;
//        } else {
//            LogUtil.e("CommonUtils", "success createFile dir=" + dir.toString());
//            return dir;
//        }
//
//    }

    /**
     * 获取当前时间 并且格式化
     */
    public static String formatCurrentDate(String pattern) {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        //"yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date curDate = new Date(time);
        return format.format(curDate);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 根据宽度设置图片高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidthForHeight(Context context, int width) {
        return width * 9/16;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕显示信息对象
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * dp转pixel
     */
    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * pixel转dp
     */
    public static float pixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }


    /**
     * 短信分享
     *
     * @param mContext
     * @param smstext  短信分享内容
     * @return
     */
    public static Boolean sendSms(Context mContext, String smstext) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        mIntent.putExtra("sms_body", smstext);
        mContext.startActivity(mIntent);
        return null;
    }

    /**
     * 邮件分享
     *
     * @param mContext
     * @param title    邮件的标题
     * @param text     邮件的内容
     * @return
     */
    public static void sendMail(Context mContext, String title, String text) {
        // 调用系统发邮件
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // 设置文本格式
        emailIntent.setType("text/plain");
        // 设置对方邮件地址
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        // 设置标题内容
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        // 设置邮件文本内容
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        mContext.startActivity(Intent.createChooser(emailIntent, "Choose Email Client"));
    }

    /**
     * 如果输入法在窗口上已经显示，则隐藏系统键盘，反之则显示
     *
     * @param context
     */
    public static void hideSysKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = inputMethodManager.isActive();//isOpen若返回true，则表示输入法打开
        if (isOpen) {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * 强制隐藏软键盘
     *
     * @param mView
     */
//    public static void hideKeyboardView(View mView) {
//        if (mView != null) {
//            InputMethodManager imm = (InputMethodManager) NyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(mView, InputMethodManager.SHOW_FORCED);
//            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0); //强制隐藏键盘
//        }
//    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * 是否横屏
     *
     * @param context
     * @return true为横屏，false为竖屏
     */
    public static boolean isLandscape(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是平板
     * 这个方法是从 Google I/O App for Android 的源码里找来的，非常准确。
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 是否需要改变布局
     * 说明：针对我们的项目，如果是平板横屏才显示最右边fragment区域
     * 平板竖屏以及手持设备都不显示最右边fragment区域
     *
     * @param context
     * @return
     */
    public static boolean isChangeLayout(Context context) {
        if (isTablet(context) && isLandscape(context)) {
            return true;
        }
        return false;
    }

    /**
     * 安装APK
     *
     * @param context
     * @param storePath
     * @param fileName
     */
    public static void archiveAPK(Context context, String storePath, String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 加这个标志，安装完毕后才会提示已安装成功，否则不会提示。chenwenhan 20141101
        intent.setDataAndType(Uri.fromFile(new File(storePath, fileName)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装APK
     *
     * @param context
     * @param filePath
     */
    public static void archiveAPK(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // 加这个标志，安装完毕后才会提示已安装成功，否则不会提示。chenwenhan 20141101
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 静默卸载
     *
     * @param packageName
     * @return result
     */
    public static int uninstallAPKSilently(String packageName) {
        int result = -1;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec("per-up");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " +
                    packageName);
            dataOutputStream.flush();
            dataOutputStream.close();
            BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                if (s.contains("Success")) {
                    result = 0;
                }
            }
            while ((s = errorResult.readLine()) != null) {
                if (s.contains("Failure")) {
                    result = -1;
                }
            }
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String formatData(long publishTime) {
        long delta = new Date().getTime() - publishTime;
        if (delta < 1000) {
            return "刚刚";
        }
        String sb = "";
        long[] itimes = new long[]{365 * 24 * 60 * 60 * 1000L, 30 * 24 * 60 * 60 * 1000L, 24 * 60 * 60 * 1000L, 60 * 60 * 1000L, 60 * 1000, 1000L};
        String[] sunits = new String[]{"年前", "月前", "天前", "小时前", "分钟前", "秒前"};

        for (int i = 0, len = itimes.length; i < len; i++) {
            long itemp = itimes[i];
            if (delta < itemp) {
                continue;
            }
            long temp2 = delta / itemp;
            if (temp2 > 0) {
                sb = temp2 + sunits[i];
                break;
            }
        }
        return sb;
    }

    /**
     * 显示发布时间（发布时间是指文章第一次创建的时间，不是最近更新时间）的规则：
     * 发布时间距离当前时间1小时内：20分钟前
     * 发布时间距离当前时间超过1小时且在当天之内：5小时前
     * 发布时间在当前日期的前1天： 昨天 12:30
     * 其他情况显示日期：12-08
     *
     * @param publishTime
     * @return
     */
    public static String dateFormat(long publishTime) {
        if (publishTime <= 0) {
            return "";
        }
        if (publishTime - yesterdayEarlyMorningTimeStamp() < 0) {//新闻是在昨天之前发的
            return formatDate(publishTime, "MM-dd");
        } else {
            if (publishTime - todayEarlyMorningTimeStamp() < 0) {//新闻是在昨天发的
                return "昨天 " + formatDate(publishTime, "HH:mm");
            } else {//新闻是在今天发的
                long currentTime = System.currentTimeMillis();//当前时间戳
                long oneHourTimeStamp = 60 * 60 * 1000;//一个小时的时间戳
                if ((currentTime - publishTime) >= oneHourTimeStamp) {//今天之内，一小时之外
                    return (currentTime-publishTime) / oneHourTimeStamp + "小时前";
                } else {//今天之内，一小时之内
                    return (currentTime - publishTime) / (1000 * 60) + "分钟前";
                }
            }
        }
    }

    public static String formatDate(long timeStamp, String regularRule) {
        SimpleDateFormat formatter = new SimpleDateFormat(regularRule);//yyyy-MM-dd HH:mm:ss
        String dateString = formatter.format(timeStamp);
        return dateString;
    }

    public static long todayEarlyMorningTimeStamp() {//今天凌晨
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);//今天开始是从凌晨0点开始
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date date = todayStart.getTime();
        return date.getTime();
    }

    public static long yesterdayEarlyMorningTimeStamp() {//昨天凌晨
        long onedayTimeStamp = 24 * 60 * 60 * 1000;//一天的时间戳
        return todayEarlyMorningTimeStamp() - onedayTimeStamp;
    }

    /**
     * 时间格式化
     * param: position 时间的毫秒数（long类型）
     */
    public static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    /**
     * 时间格式化
     * param: s 时间的毫秒数（String类型）
     */
    public static String dateTimeFormat(String s) {
        try {
            long e = Long.parseLong(s);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(e);
            String t1 = format.format(d1);
            return t1;
        } catch (Exception var6) {
            return "";
        }
    }

    /**
     * 压缩图片
     *
     * @param srcPath
     * @return
     */
//    public static Bitmap getBitmapFromUri(String srcPath) {
//        Matrix matrix = null;
//        try {
//            ExifInterface exifInterface = new ExifInterface(srcPath);
//            int orientation = exifInterface.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int degree = 0;
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    degree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    degree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    degree = 270;
//                    break;
//            }
//            matrix = new Matrix();
//            matrix.setRotate(degree);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
//        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;//be=1表示不缩放
//        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
//        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
//        }
//        if (be <= 0)
//            be = 1;
//        newOpts.inSampleSize = be;//设置缩放比例
//        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
//        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//
//        if (matrix != null && bitmap != null) {
//            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            if (bitmap != newBitmap) {
//                bitmap.recycle();
//                bitmap = newBitmap;
//            }
//        }
//
//        return compressImage(srcPath, bitmap);//压缩好比例大小后再进行质量压缩
//    }

    /**
     * 质量压缩 返回的是文件路径
     *
     * @param image
     * @return
     */
//    public static Bitmap compressImage(String filePath, Bitmap image) {
//        String mFilePath = copyImagePath(filePath);
//        File myCaptureFile = new File(mFilePath);
//        FileOutputStream fos = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            fos = new FileOutputStream(myCaptureFile);
//            baos = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//            int options = 100;
//            while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
//                baos.reset();// 重置baos即清空baos
//                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
//                options -= 10;// 每次都减少10
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            baos.writeTo(fos);
//            baos.flush();
//            baos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return image;
////		return myCaptureFile.toString();
//    }

//    public static String copyImagePath(String headImagePath) {
//        String destPath = ConstantAttr.SDCARD_IMGS_PATH
//                + System.currentTimeMillis() + ".jpg";
//        copyFile(headImagePath, destPath);
//        return destPath;
//    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean bResult = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    // System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                bResult = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bResult;
    }

    /**
     * 产生0~1000的随机数，对产生400-600的随机数的用户进行统计
     *
     * @return
     */
    public static boolean isBeyondStatRange() {
        int randomNum = (int) (Math.random() * 1000);
        if (randomNum >= 400 && randomNum <= 600) {
            return true;
        }
        return false;
    }
}