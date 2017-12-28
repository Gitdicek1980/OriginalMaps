package jp.techacademy.yamamoto.daisuke.originalmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CourseDetailListAdapter extends BaseAdapter {
    private final static int TYPE_COURSE = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Course mCourse;

    public CourseDetailListAdapter(Context context, Course course) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCourse = course;
    }

    @Override
    public int getCount() {
        return 1 + mCourse.getAnswers().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_COURSE;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return mCourse;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == TYPE_COURSE) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_course_detail, parent, false);
            }
            String body = mCourse.getBody();
            String name = mCourse.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            byte[] bytes = mCourse.getImageBytes();
            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mCourse.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }

        return convertView;
    }
}
