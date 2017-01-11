package com.androidbuts.parser;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
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

    /**
     * Upload URL of your folder with php file name...
     * You will find this file in php_upload folder in this project
     * You can copy that folder and paste in your htdocs folder...
     */
    private static final String URL_UPLOAD_IMAGE = "http://g5-api.azurewebsites.net/api/file/upload";

    /**
     * Upload Image
     *
     * @param sourceImageFile
     * @return
     */
    public static JSONObject uploadImage(String sourceImageFile) {

        try {

            String res = uploadImage(sourceImageFile, URL_UPLOAD_IMAGE);
            /*File sourceFile = new File(sourceImageFile);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image*//*");

            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("result", "my_image")
                    .build();

            Request request = new Request.Builder()
                    .url(URL_UPLOAD_IMAGE)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();*/
            Log.e("TAG", "Error: " + res);
            return new JSONObject(res);

        } /*catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        }*/ catch (Exception e) {
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
