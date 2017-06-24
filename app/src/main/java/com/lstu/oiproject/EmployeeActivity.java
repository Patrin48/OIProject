package com.lstu.oiproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.YouTubeIntents;
import org.apache.commons.io.FileUtils;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


public class EmployeeActivity extends AppCompatActivity {

    PhotoView imageView;
    private int currentPage = 0;
    String download_file_path = "";
    String pathToVideo, ID,  IndexEmployee;
    VideoView videoView;
    Boolean success;
    Button buttonInst;
    File dest = new File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/instruction.jpg");
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Intent intent = getIntent();
            IndexEmployee = intent.getStringExtra("IndexEmployee");
            mPieChart.addPieSlice(new PieModel("", Integer.parseInt(IndexEmployee), Color.parseColor("#56B7F1")));
            mPieChart.addPieSlice(new PieModel("", 100 - Integer.parseInt(IndexEmployee), Color.parseColor("#FE6DA8")));
            mPieChart.startAnimation();
            TextView textViewEmployee = (TextView) findViewById(R.id.textView1);
            TextView textView = (TextView) findViewById(R.id.textView2);
            buttonInst = (Button) findViewById(R.id.button1);
            imageView = (PhotoView) findViewById(R.id.photo_view);
            Button buttonTest = (Button) findViewById(R.id.button2);
            Button buttonDef = (Button) findViewById(R.id.button_def);
            TextView textView1 = (TextView) findViewById(R.id.textView8);
            videoView = (VideoView) findViewById(R.id.videoView);
            String EmployeeName = intent.getStringExtra("EmployeeName");
            verifyStoragePermissions(this);
            download_file_path = intent.getStringExtra("URL");
            pathToVideo = intent.getStringExtra("VideoURL");
            ID = intent.getStringExtra("ID");
            textView1.setText("Your QScore: " + Integer.parseInt(IndexEmployee) + "%");
            textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewEmployee.setText("Добро пожаловать, " + EmployeeName + ".");
            buttonInst.setText("Загрузить производственную инструкцию");
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка загрузки формы Работника!", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        try {
            int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void TestonClick(View view) throws IOException {

        try {
            imageView.setVisibility(view.GONE);
            Uri videoUri = Uri.parse(pathToVideo);
            videoView.setVideoURI(videoUri);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не удалось показать видеофайл", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();

        }

    }

    public void InstructiononClick(View view) throws IOException {
        try {
            imageView.setVisibility(view.VISIBLE);
            videoView.setVisibility(View.GONE);
            Button b = (Button) buttonInst;
            String buttonText = b.getText().toString();
            if (buttonText == "Загрузить производственную инструкцию") {
                new DownLoadImageTask(imageView).execute(download_file_path);
                render();
            }
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не удалось показать инструкцию", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void onDownloadComplete(boolean success) {
        if (!success) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Загрузка не удалась!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Загрузка удалась! Вы можете просмотреть инструкцию!", Toast.LENGTH_SHORT);
                    toast.show();
                    buttonInst.setText("Просмотреть производственную инструкцию");
                }
            });
        }
    }

    private void render() {

        imageView = (PhotoView) findViewById(R.id.photo_view);
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/instruction.jpg");

    }

    public void DefonClick(View view) {

        try {
            Intent intent = new Intent(EmployeeActivity.this, DefectRegActivity.class);
            intent.putExtra("ID", ID);
            startActivity(intent);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка переадресации на форму Дефект!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка загрузки медиа-файла!", Toast.LENGTH_LONG);
                toast.show();
            }
            return logo;
        }
        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main2 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class LoadFile extends Thread {
        private final String src;
        private final File dest;

        LoadFile(String src, File dest) {
            this.src = src;
            this.dest = dest;
        }

        @Override
        public void run() {
            try {
                FileUtils.copyURLToFile(new URL(download_file_path), dest);
                onDownloadComplete(true);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                onDownloadComplete(false);
                success = false;
            }
        }
    }
}





