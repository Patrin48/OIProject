package com.lstu.oiproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.transition.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AuditActivity extends AppCompatActivity {

    String IndexEmployee,EmployeeName, URL, VideoURL, NameAudit, Line, PlaceN, ID, AuditDate, AuditResults;
    PhotoView imageView;
    VideoView videoView;
    Button button;
    Boolean success;
    File dest = new File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/instruction.jpg");
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_audit);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Intent intent = getIntent();
            IndexEmployee=intent.getStringExtra("IndexEmployee");
            EmployeeName = intent.getStringExtra("EmployeeName");
            URL = intent.getStringExtra("URL");
            AuditResults = intent.getStringExtra("AuditResults");
            VideoURL = intent.getStringExtra("VideoURL");
            NameAudit = intent.getStringExtra("AuditName");
            Line = intent.getStringExtra("Line");
            PlaceN = intent.getStringExtra("PlaceN");
            ID = intent.getStringExtra("ID");
            AuditDate = intent.getStringExtra("AuditDate");
            imageView = (PhotoView) findViewById(R.id.photo_view);
            videoView = (VideoView) findViewById(R.id.videoView);
            TextView textView1=(TextView) findViewById(R.id.textView1);
            button = (Button) findViewById(R.id.button2);
            textView1.setText("Аудит проводит: "+ NameAudit);
            TextView textView2 = (TextView) findViewById(R.id.textView2);
            textView2.setText("Работник: "+ EmployeeName+" "+ " - Линия: "+ Line+ " "+" - Рабочее место №:"+PlaceN+" "+ " - КЛВ: "+IndexEmployee + ". Дата последнего аудита: "+ AuditDate);
            textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            videoView.setVisibility(View.GONE);
            if (Integer.parseInt(IndexEmployee)<=10)
            {
                ratingBar.setRating(1);
            }
            else if (Integer.parseInt(IndexEmployee)<=40)
            {
                ratingBar.setRating(2);
            }
            else if (Integer.parseInt(IndexEmployee)<=60)
            {
                ratingBar.setRating(3);
            }
            else if (Integer.parseInt(IndexEmployee)<=80)
            {
                ratingBar.setRating(4);
            }
            else if (Integer.parseInt(IndexEmployee)<=100)
            {
                ratingBar.setRating(5);
            }
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            AlertDialog.Builder builder = new AlertDialog.Builder(AuditActivity.this);
            if (AuditResults=="") {
                builder.setTitle("Замечания к прошлому аудиту")
                        .setMessage("Замечания отсутствуют :)")
                        .setIcon(R.drawable.logo)
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else
            {
                builder.setTitle("Замечания к прошлому аудиту")
                        .setMessage(AuditResults)
                        .setIcon(R.drawable.logo)
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка загрузки интерфейса Аудитора!", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void render() {

        try {
            imageView = (PhotoView) findViewById(R.id.photo_view);
            File file = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/instruction.jpg");
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка загрузки производственной инструкции работника!", Toast.LENGTH_LONG);
            toast.show();
        }

    }
    public void InstructionOnClick(View view) {
        try {
            imageView.setVisibility(view.VISIBLE);
            videoView.setVisibility(View.GONE);

                new AuditActivity.DownLoadImageTask(imageView).execute(URL);
                render();
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
                    button.setText("Просмотреть производственную инструкцию");
                }
            });
        }
    }
    public void VideoOnClick(View view) {
        try {
            imageView.setVisibility(view.GONE);
            Uri videoUri = Uri.parse(VideoURL);
            videoView.setVideoURI(videoUri);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не удалось показать видеофайл", Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    public void FeedbackOnClick(View view) {

        try {
            Intent intent_feedback =  new Intent(AuditActivity.this, FeedbackActivity.class);
            intent_feedback.putExtra("EmployeeName", EmployeeName);
            intent_feedback.putExtra("URL", URL);
            intent_feedback.putExtra("VideoURL", VideoURL);
            intent_feedback.putExtra("ID", ID);
            intent_feedback.putExtra("IndexEmployee", IndexEmployee);
            intent_feedback.putExtra("Line",Line);
            intent_feedback.putExtra("PlaceN", PlaceN);
            intent_feedback.putExtra("AuditName",NameAudit);
            intent_feedback.putExtra("ID", ID);
            startActivity(intent_feedback);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка переадресации на фидбэк!", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
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
                        "Загрузка медиафайла неудачна!", Toast.LENGTH_LONG);
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
                FileUtils.copyURLToFile(new URL(URL), dest);
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

