package com.androidbuts.parser;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * Created by Pratik Butani
 */
public class JSONParser {

    public static String get(String url)throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Upload URL of your folder with php file name...
     * You will find this file in php_upload folder in this project
     * You can copy that folder and paste in your htdocs folder...
     */
    public static final String URL_UPLOAD_IMAGE = "http://g5-api.azurewebsites.net/api/file/upload/";
    public static final String URL_DOWNLOAD_IMAGE = "http://g5-api.azurewebsites.net/api/file/download/";
    /**
     * Upload Image
     *
     * @param sourceImageFile
     * @return
     */
    public static String uploadImage(String sourceImageFile) {
        try {
            return uploadImage(sourceImageFile, URL_UPLOAD_IMAGE);
        }  catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static String uploadImage(String sourceFileName, String url){
        File sourceFile = new File(sourceFileName);
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image",  sourceFile.getName(), RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                .build();
        return post(requestBody, url);
    }

    public static String post(RequestBody requestBody, String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            return response.body().string();

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}
