package com.aikucun.akapp.activity.video;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ak123 on 2017/11/17.
 * 视频实例
 */

public class VideoEntity implements Parcelable {

    private String id;
    private String videoPath;
    private String videoName;
    private String videoSize;
    private String videoDuration;
    private boolean isSelected = false;
    private Bitmap bitmap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public VideoEntity(){}

    protected VideoEntity(Parcel in) {
        id = in.readString();
        videoPath = in.readString();
        videoName = in.readString();
        videoSize = in.readString();
        videoDuration = in.readString();
        isSelected = in.readByte() != 0;
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<VideoEntity> CREATOR = new Creator<VideoEntity>() {
        @Override
        public VideoEntity createFromParcel(Parcel in) {
            return new VideoEntity(in);
        }

        @Override
        public VideoEntity[] newArray(int size) {
            return new VideoEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(videoPath);
        dest.writeString(videoName);
        dest.writeString(videoSize);
        dest.writeString(videoDuration);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeParcelable(bitmap, flags);
    }
}
