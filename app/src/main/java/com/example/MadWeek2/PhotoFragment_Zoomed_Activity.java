package com.example.MadWeek2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoFragment_Zoomed_Activity extends Activity{

    private Context mContext = null;

    SliderAdapter adapter;
    ViewPager viewPager;
    int position;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_slide);
        mContext = this;

        Intent i = getIntent();
        final ArrayList<String> DATA = (ArrayList<String>) i.getSerializableExtra("thumbsDataList");
        position = i.getIntExtra("index", 1);

        button = (ImageButton) findViewById(R.id.btn_setting);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        button.bringToFront();

        adapter = new SliderAdapter(this, DATA);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Dialog = new AlertDialog.Builder(PhotoFragment_Zoomed_Activity.this);
                Dialog.setTitle("Path");
                Dialog.setMessage(DATA.get(viewPager.getCurrentItem()));

                Dialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog.show();
            }
        });
    }

    public class SliderAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<String> thumbsDataList;

        public SliderAdapter(Context context, ArrayList<String> thumbsDataList){
            this.context = context;
            this.thumbsDataList = thumbsDataList;
        }

        @Override
        public int getCount(){
            return thumbsDataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object){
            return view == ((ConstraintLayout) object);
        }

        public int getOrientationOfImage(String filepath) {
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(filepath);
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
            return 0;
        }

        public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees){
            if (bitmap == null) return null;
            if (degrees == 0) return bitmap;

            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.activity_zoomed, container, false);

            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 2;
            PhotoView iv = (PhotoView) v.findViewById(R.id.imageZoomedView);
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = getRotatedBitmap(bmp, getOrientationOfImage(thumbsDataList.get(position)));
            iv.setImageBitmap(resized);
            container.addView(v);
            return v;
        }

        public void destroyItem(ViewGroup container, int position, Object object){
            container.invalidate();
        }
    }
}