package io.madcamp.jh.madcamp_assignment2;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload extends AsyncTask<String, Integer, String> {
    private String path = Environment.getExternalStorageDirectory() + File.separator + "Download/";

    private String fileName;

    public ImageDownload(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected String doInBackground(String... params) {
        File dir = new File(path);
        if(!dir.exists()) {
            Log.d("Test@ImageDownload", "mkdir");
            dir.mkdirs();
        }

        String fileUrl = params[0];
        String localPath = path + "/" + fileName + ".jpg";

        HttpURLConnection conn = null;
        String result = null;
        try {
            URL imgUrl = new URL(fileUrl);
            conn = (HttpURLConnection) imgUrl.openConnection();
            int response = conn.getResponseCode();
            if(response != HttpURLConnection.HTTP_OK) return null;
            Log.d("Test@ImageDownload", "conn created");

            File file = new File(localPath);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            Log.d("Test@ImageDownload", "streams are opened");

            byte[] buf = new byte[1024];
            int len = 0;
            while((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            Log.d("Test@ImageDownload", "written");
            os.close();
            is.close();
            Log.d("Test@ImageDownload", "closed");
            result = localPath;
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }
        return result;
    }
}
