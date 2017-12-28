package jp.techacademy.yamamoto.daisuke.originalmaps;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mCourseUid;
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getCourseUid() {
        return mCourseUid;
    }
    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    public Course(String title, String body, String name, String uid, String courseuid, int genre, byte[] bytes, ArrayList<Answer> answers) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mCourseUid = courseuid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
    }
}
