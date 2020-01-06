package com.example.MadWeek2;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.MadWeek2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class PhotoFragment extends Fragment {

    private FloatingActionButton upload;
    private FloatingActionButton download;
    private FloatingActionButton fab_menu;
    private Boolean isMenuOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        final GridView gridView = (GridView) view.findViewById(R.id.gridViewImages);
        final ImageGridAdapter imageGridAdapter = new ImageGridAdapter(getActivity());
        gridView.setAdapter(imageGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                imageGridAdapter.callImageViewer(position);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_photos);
        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh () {
                    //imageGridAdapter.notifyDataSetChanged();
                    gridView.invalidate();
                    swipeRefreshLayout.setRefreshing(false);
        }
        });

        fab_menu = view.findViewById(R.id.fab_menu);
        upload = view.findViewById(R.id.fab_upload);
        download = view.findViewById(R.id.fab_download);

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                menuOpen();
            }
        });

        return view;
    }

    private void menuOpen() {
        if (!isMenuOpen) {
            upload.animate().translationY(-getResources().getDimension(R.dimen.upload));
            download.animate().translationY(-getResources().getDimension(R.dimen.download));
            isMenuOpen = true;
        }
        else {
            upload.animate().translationY(0);
            download.animate().translationY(0);
            isMenuOpen = false;
        }
    }

    public class ImageGridAdapter extends BaseAdapter {

        Context mContext;
        private String imgData;
        private String geoData;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;


        public ImageGridAdapter(Context context){
            this.mContext = context;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            getThumbInfo(thumbsIDList, thumbsDataList);
        }

        public void callImageViewer(int selectedIndex){
            Intent i = new Intent(mContext, PhotoFragment_Zoomed_Activity.class);
            i.putExtra("thumbsDataList", thumbsDataList).putExtra("index", selectedIndex);
            startActivity(i);
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
            if(bitmap == null) return null;
            if (degrees == 0) return bitmap;

            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }

        public int getCount() {
            return thumbsIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            }else{
                imageView = (ImageView) convertView;
            }

            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);

            int imageWidth = bmp.getWidth();
            int imageHeight = bmp.getHeight();
            imageView.measure(View.MeasureSpec.makeMeasureSpec(imageWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(imageHeight, View.MeasureSpec.EXACTLY));
            int siz = imageView.getMeasuredWidth();

            Bitmap resized_bitmap = ThumbnailUtils.extractThumbnail(bmp, siz, siz);

            Bitmap resized = getRotatedBitmap(resized_bitmap, getOrientationOfImage(thumbsDataList.get(position)));
            imageView.setImageBitmap(resized);

            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;
                String data;
                String imgSize;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                } while (imageCursor.moveToNext());
            }
            imageCursor.close();
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID){
            String imageDataPath = null;
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='"+ thumbID +"'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                if (imageCursor.getCount() > 0){
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                }
            }
            imageCursor.close();
            return imageDataPath;
        }
    }
}