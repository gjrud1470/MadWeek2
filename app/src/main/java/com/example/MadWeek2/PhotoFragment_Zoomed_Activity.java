package com.example.MadWeek2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.MadWeek2.Retrofit.ImageService;
import com.example.MadWeek2.Retrofit.RetrofitClient2;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoFragment_Zoomed_Activity extends Activity{
    private Context mContext = null;
    private FloatingActionButton fab_menu;
    private FloatingActionButton path;
    private FloatingActionButton delete;
    private Boolean isMenuOpen = false;

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

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        path = (FloatingActionButton) findViewById(R.id.path);
        delete = (FloatingActionButton) findViewById(R.id.delete);

        fab_menu.bringToFront();

        adapter = new SliderAdapter(this, DATA);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpen();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewPager.getCurrentItem();
                adapter.thumbsDataList.remove(pos);
                adapter.notifyDataSetChanged();

                String salt = find_login_info();
                String file_path = DATA.get(viewPager.getCurrentItem());
                String file_name = DATA.get(viewPager.getCurrentItem()).substring(DATA.get(viewPager.getCurrentItem()).lastIndexOf('/') + 1);
                ImageService mTestService;

                RetrofitClient2<ImageService> mowaDefaultRestClient = new RetrofitClient2<>();
                mTestService = mowaDefaultRestClient.getClient(ImageService.class);

                Call<Image_Delete_Info> call = mTestService.deleteImages(salt, file_name);
                call.enqueue(new Callback<Image_Delete_Info>() {
                    @Override
                    public void onResponse(Call<Image_Delete_Info> call, Response<Image_Delete_Info> response) {
                        if (response.isSuccessful()){
                            if (response.body().getResult().equals("Successfully removed")){
                                File fdelete = new File(DATA.get(viewPager.getCurrentItem()));
                                fdelete.delete();
                                callBroadCast();
                                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(DATA.get(viewPager.getCurrentItem())))));
                                DATA.remove(viewPager.getCurrentItem());
                                Toast.makeText(getApplicationContext(), "Server & Local image is removed!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                File fdelete = new File(DATA.get(viewPager.getCurrentItem()));
                                fdelete.delete();
                                callBroadCast();
                                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(DATA.get(viewPager.getCurrentItem())))));
                                DATA.remove(viewPager.getCurrentItem());
                                Toast.makeText(getApplicationContext(), "Only local image is removed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Image_Delete_Info> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Fail!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        path.setOnClickListener(new View.OnClickListener() {
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

        Intent data = new Intent();
        data.putExtra("Result", DATA);
        setResult(0, data);
    }

    private void menuOpen() {
        if (!isMenuOpen) {
            path.animate().translationX(-getResources().getDimension(R.dimen.upload));
            delete.animate().translationX(-getResources().getDimension(R.dimen.download));
            isMenuOpen = true;
        }
        else {
            path.animate().translationX(0);
            delete.animate().translationX(0);
            isMenuOpen = false;
        }
    }

    private String find_login_info () {
        SharedPreferences pref = this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return pref.getString("salt", "");
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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