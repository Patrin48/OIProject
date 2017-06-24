package com.lstu.oiproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.loopj.android.http.*;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Дмитрий on 10.05.2017.
 */

public class DefectRegActivity extends AppCompatActivity
{

    MyView mv;
    String name="", ID, imagePath, nameimg;
    AlertDialog dialog;
    String coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mv= new MyView(this);
        mv.setDrawingCacheEnabled(true);
        mv.setBackgroundResource(R.drawable.washing_machine_v3);

        setContentView(mv);
        ID = intent.getStringExtra("ID");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    private Paint       mPaint;
    private MaskFilter mEmboss;
    private MaskFilter  mBlur;

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    public class MyView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        Context context;

        public MyView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            //showDialog();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            //mPaint.setMaskFilter(null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    private static final int Save = Menu.FIRST ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, Save, 0, "Зарегистрировать дефект").setShortcut('5', 'z');
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    public Bitmap drawTextToBitmap(Context gContext, Bitmap gResId) {
        Resources resources = gContext.getResources();
        String gText;
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =gResId;
        Random random = new Random();
        gText = "Бар-код: "+(String.valueOf(random.nextInt(1000)));
        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(206, 2, 2));
        // text size in pixels
        paint.setTextSize((int) (25 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 625;
        int y = 300;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            mPaint.setXfermode(null);
            mPaint.setAlpha(0xFF);
            switch (item.getItemId()) {
                case Save:
                    AlertDialog.Builder editalert = new AlertDialog.Builder(DefectRegActivity.this);
                    editalert.setTitle("Выберите описание дефекта");
                    //  final EditText input = new EditText(DefectRegActivity.this);
                    // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    //         LinearLayout.LayoutParams.FILL_PARENT,
                    //         LinearLayout.LayoutParams.FILL_PARENT);
                    // input.setLayoutParams(lp);
                    //  editalert.setView(input);
                    final String[] defects = {"Дефект клипсы ←", "Неверная клипса ←", "Дефект навески клипсы ←", "Вмятина на корпусе ← ←", "Царапина на корпусе ← ←", "Неверный корпус ← ←","Общие дефекты корпуса ← ←", "Другое"};
                    final String[] defects1 = {"Вмятина на корпусе ←", "Царапина на корпусе ←", "Скол на корпусе ←" , "Дефект покраски ←", "Неверный корпус ← ←", "Дефект тампографии ← ←", "Иные дефекты корпуса ← ←"};

                    final boolean[] array = new boolean[100];
                    if (ID=="5") {
                        editalert.setMultiChoiceItems(defects, array, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                name += defects[i] + "; ";
                            }
                        });
                    }
                    else
                    {
                        editalert.setMultiChoiceItems(defects1, array, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                name += defects1[i] + "; ";
                            }
                        });
                    }
                    editalert.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });
                    editalert.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //  name= input.getText().toString();
                            try
                            {
                                nameimg = UUID.randomUUID().toString();
                                Bitmap bitmap = mv.getDrawingCache();
                                bitmap=drawTextToBitmap(DefectRegActivity.this,bitmap);
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                File file = new File("/sdcard/"+nameimg+".png");
                                if(!file.exists())
                                {
                                    file.createNewFile();
                                }
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                                ostream.close();
                                mv.invalidate();
                                DoLogin doLogin = new DoLogin(); // this is the Asynctask
                                doLogin.execute("");

                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Дефект с описанием(-ами) "+ name.replace("←","").replace(";","") +"успешно добавлен!", Toast.LENGTH_SHORT);
                                toast.show();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }finally
                            {

                                mv.setDrawingCacheEnabled(false);
                            }
                        }
                    });

                    editalert.show();
                    return true;

            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка регистрации дефекта!", Toast.LENGTH_LONG);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }
    public class DoLogin extends AsyncTask<String, String, String> {
        String signature = "";
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

        }


        @SuppressLint("NewApi")
        public Connection CONN(){
            String ip = "patrin.ddns.net:1433";
            String classs = "net.sourceforge.jtds.jdbc.Driver";
            String db = "OIProject";
            String un ="sa";
            String password = "18swlgnm";
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn=null;
            String ConnURL=null;
            try{

                Class.forName(classs);
                ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                        + "databaseName=" + db + ";user=" + un + ";password="
                        + password + ";";
                conn= DriverManager.getConnection(ConnURL);
            }catch(SQLException se){
                Log.e("ERRO",se.getMessage());
            }catch(ClassNotFoundException e){
                Log.e("ERRO",e.getMessage());
            }catch(Exception e){
                Log.e("ERRO",e.getMessage());
            }
            return conn;
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                String Line="", PlaceN="",Turno="" ;
                Connection con = CONN();
                if (con == null) {
                    signature = "Ошибка при подключении к SQL серверу";
                } else {
                    String queryData = "SELECT E.Line,E.PlaceN,E.Turno FROM Employee E WHERE E.ID='"+ID+"'";
                    Statement stmtData = con.createStatement();
                    ResultSet rsData = stmtData.executeQuery(queryData);
                    if (rsData.next()) {
                        Line = rsData.getString("Line");
                        PlaceN= rsData.getString("PlaceN");
                        Turno = rsData.getString("Turno");

                    }
                    long curTime = System.currentTimeMillis();
                    String curStringDate = new SimpleDateFormat("dd.MM.yyyy  hh:mm:ss").format(curTime);
                    queryData = "INSERT INTO Defects VALUES ('"+name.substring(0, name.length()-1)+"','"+Line+"','"+PlaceN+"','"+ Turno +"','','"+curStringDate +"','На линии', 'http://patrin.ddns.net:90/DefectImages/"+ nameimg+".png')";
                    stmtData = con.createStatement();
                    boolean check = stmtData.execute(queryData);
                    if (!check) {
                        signature = "Дефект зарегистрирован успешно!";
                        isSuccess = true;
                        SyncHttpClient client = new SyncHttpClient();
                        RequestParams paramz = new RequestParams();
                        paramz.put("image", new File("/sdcard/"+nameimg+".png"));
                        client.post("http://patrin.ddns.net:90/DefectImages/", paramz, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                // error handling
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                // success
                            }
                        });
                    }
                    else
                    {
                        signature = "Ошибка при регистрации дефекта!";
                        isSuccess = false;
                    }


                }
            } catch (Exception ex) {
                isSuccess = false;
                signature = ex.getMessage();
            }

            return signature;
        }

    }

}
