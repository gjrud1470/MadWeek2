package com.example.MadWeek2;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.MadWeek2.Retrofit.ImageService;
import static android.app.Activity.RESULT_OK;

import com.example.MadWeek2.Retrofit.RetrofitClient2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoFragment extends Fragment {
    private ArrayList<String> mThumbsDataList = null;
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

        mThumbsDataList = imageGridAdapter.thumbsDataList;

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                imageGridAdapter.callImageViewer(position);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_photos);
        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh () {
                    ImageGridAdapter newimageGridAdapter = new ImageGridAdapter(getContext());
                    gridView.invalidate();
                    gridView.setAdapter(newimageGridAdapter);
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

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Toast.makeText(getContext(), "Download Start!", Toast.LENGTH_LONG).show();
                String salt = find_login_info();
                ImageService mTestService;

                /*
                    Connection Codes
                 */

                RetrofitClient2<ImageService> mowaDefaultRestClient = new RetrofitClient2<>();
                mTestService = mowaDefaultRestClient.getClient(ImageService.class);

                Call<ArrayList<Image_Download_Info>> call = mTestService.downloadImages(salt);
                call.enqueue(new Callback<ArrayList<Image_Download_Info>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Image_Download_Info>> call, Response<ArrayList<Image_Download_Info>> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().size(); i++) {
                                    int counter = 0;
                                    int pos = i;
                                    for (int j = 0; j < mThumbsDataList.size(); j++) {
                                        if (!mThumbsDataList.get(j).substring(mThumbsDataList.get(j).lastIndexOf('/') + 1).equals(response.body().get(i).getOriginalName())) {
                                            counter++;
                                        }
                                    }
                                    if(counter == mThumbsDataList.size()){
                                        Bitmap temp = getBitmapFromString(response.body().get(pos).getBase64());
                                        mThumbsDataList.add("/storage/emulated/0/DCIM/Camera/" + response.body().get(pos).getOriginalName());
                                        Bitmap resized = getRotatedBitmap(temp, getOrientationOfImage("/storage/emulated/0/DCIM/Camera/" + response.body().get(pos).getOriginalName()));
                                        saveBitmaptoJpeg(resized, "/storage/emulated/0/DCIM/Camera/", response.body().get(pos).getOriginalName());

                                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        File f = new File("/storage/emulated/0/DCIM/Camera/" + response.body().get(pos).getOriginalName());
                                        Uri contentUri = Uri.fromFile(f);
                                        mediaScanIntent.setData(contentUri);
                                        getActivity().sendBroadcast(mediaScanIntent);
                                    }
                                }
                        }
                        else {
                            Toast.makeText(getContext(), "Download Fail!", Toast.LENGTH_LONG).show();
                        }

                        if (mThumbsDataList.size() == response.body().size()){
                            Toast.makeText(getContext(), "Download Finish!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Download Not!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Image_Download_Info>> call, Throwable t) {
                        Log.d("onFailure", t.toString());
                    }
                });
            }
        });
        return view;
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
        String foler_name = folder;
        String file_name = name;
        String string_path = foler_name;
        File file_path;

        try{ file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs(); }

            FileOutputStream out = new FileOutputStream(string_path+file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close(); }

        catch(FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        }

        catch(IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees){
        if(bitmap == null) return null;
        if (degrees == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public int getOrientationOfImage(String filepath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e) {
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

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data){

        switch (requestCode) {
            case 0:
            {
                mThumbsDataList = data.getStringArrayListExtra("Result");
            }

            default: {
                super.onActivityResult(requestCode, resultCode, data);

                String salt = find_login_info();
                ImageService mTestService;

        /*
            Connection Code
         */

                RetrofitClient2<ImageService> mowaDefaultRestClient = new RetrofitClient2<>();
                mTestService = mowaDefaultRestClient.getClient(ImageService.class);

                if (requestCode == 2 && resultCode == RESULT_OK) {
                    File file = new File(getPath(getContext(), data.getData()));
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                    Call<Image_Upload_Info> call = mTestService.uploadImages(salt, body);
                    call.enqueue(new Callback<Image_Upload_Info>() {
                        @Override
                        public void onResponse(Call<Image_Upload_Info> call, Response<Image_Upload_Info> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getResult().equals("Image upload success!"))
                                    Toast.makeText(getContext(), "Image Upload Success!", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getContext(), "Already Existed File!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Image_Upload_Info> call, Throwable t) {
                            Toast.makeText(getContext(), "Image Upload Fail!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
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

    private String find_login_info () {
        SharedPreferences pref = this.getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return pref.getString("salt", "");
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /*
        Start ImageGridAdapter
                                */

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
            startActivityForResult(i, 0);
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