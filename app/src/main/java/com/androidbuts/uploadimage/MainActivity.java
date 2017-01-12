package com.androidbuts.uploadimage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.parser.JSONParser;
import com.androidbuts.permission.PermissionsActivity;
import com.androidbuts.permission.PermissionsChecker;
import com.androidbuts.utils.InternetConnection;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    /**
     * Permission List
     */
    private static final String[] PERMISSIONS_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * Context Variables
     */
    Context mContext;

    /**
     * Views
     */
    ImageView imageView;
    TextView textView;

    /**
     * Image path to send
     */
    String imagePath;

    /**
     *
     */
    PermissionsChecker checker;

    /**
     *
     */
    Toolbar toolbar;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        /**
         * Permission Checker Initialized
         */
        checker = new PermissionsChecker(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checker.lacksPermissions(PERMISSIONS_READ_STORAGE)) {
                    startPermissionsActivity(PERMISSIONS_READ_STORAGE);
                } else {
                    showImagePopup();
                }
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void downloadImage(View view) {
       /* new AsyncTask<Void, Integer, Boolean>() {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(getString(R.string.string_title_upload_progressbar_));
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                String jsonObject = null;
                try {
                    jsonObject = JSONParser.get(JSONParser.URL_DOWNLOAD_IMAGE+"a7196168-c09f-4648-9a96-d716dcfea3c4");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return jsonObject != null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (progressDialog != null)
                    progressDialog.dismiss();

                if (aBoolean)
                    Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.string_upload_fail, Toast.LENGTH_LONG).show();

                imagePath = "";
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        }.execute();*/

        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        Picasso.with(mContext).load(JSONParser.URL_DOWNLOAD_IMAGE+"a7196168-c09f-4648-9a96-d716dcfea3c4")
                .error(R.drawable.ic_cloud_upload_white_36dp)
                .into(imageView);
    }

    public void showImagePicker(View view){
        if (!TextUtils.isEmpty(imagePath)) {

            /**
             * Uploading AsyncTask
             */
            if (InternetConnection.checkConnection(mContext)) {
                new AsyncTask<Void, Integer, Boolean>() {

                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage(getString(R.string.string_title_upload_progressbar_));
                        progressDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        String jsonObject = JSONParser.uploadImage(imagePath);
                        return jsonObject != null;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if (aBoolean)
                            Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), R.string.string_upload_fail, Toast.LENGTH_LONG).show();

                        imagePath = "";
                        textView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }.execute();
            } else {
                Snackbar.make(findViewById(R.id.parentView), R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {
            Snackbar.make(findViewById(R.id.parentView), R.string.string_message_to_attach_file, Snackbar.LENGTH_INDEFINITE).show();
        }
    }


    /**
     * Showing Image Picker
     */
    private void showImagePopup() {
        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of file system options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.string_choose_image));
        startActivityForResult(chooserIntent, 1010);
    }

    /***
     * OnResult of Image Picked
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1010) {
            if (data == null) {
                Snackbar.make(findViewById(R.id.parentView), R.string.string_unable_to_pick_image, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if(selectedImageUri != null)
                try {
                    imagePath = getFilePath(this,selectedImageUri);
                    Picasso.with(mContext).load(new File(imagePath))
                            .into(imageView);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            else {
                Snackbar.make(findViewById(R.id.parentView), R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImagePopup();
                    }
                }).show();
            }

            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
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

    private void startPermissionsActivity(String[] permission) {
        PermissionsActivity.startActivityForResult(this, 0, permission);
    }
}
