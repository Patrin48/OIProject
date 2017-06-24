package com.lstu.oiproject;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.transition.Visibility;
import android.text.format.Formatter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import junit.framework.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class AuthorizationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText EditTextLogin,EditTextPassword;
    ProgressBar pbbar;
    ImageView imageView, imageView1;
    TextView textView;
    Boolean check = false, check2=false, check3=false;
    NfcAdapter mAdapter;
    DrawerLayout drawer;
    Button ButtonEnter;
    Intent intent_audit;
    String queryData;
    String NameEmployee, URL, VideoURL, ID, Line, PlaceN, IndexEmployee, Powers, NameAudit, AuditDate, TestID, PlaceName, AuditResults;
    PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            textView = (TextView) findViewById(R.id.textView3);
            EditTextLogin = (EditText) findViewById(R.id.editTextLogin);
            EditTextPassword = (EditText) findViewById(R.id.editTextPassword);
            imageView = (ImageView) findViewById(R.id.imageView);
            ButtonEnter = (Button) findViewById(R.id.buttonEnter);
            pbbar = (ProgressBar) findViewById(R.id.progressBar);
            imageView1 = (ImageView) findViewById(R.id.imageView4);
            pbbar.setVisibility(View.GONE);
            imageView1.setVisibility(View.GONE);
            Glide.with(this).load("http://vietagz.com/wp-content/uploads/2014/07/connectAnimation.gif").into(imageView);
            mAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mAdapter == null) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "NFC-адептер отсутствует!\nЗавершение работы(3)...", Toast.LENGTH_SHORT);
                toast.show();
                Toast toast1 = Toast.makeText(getApplicationContext(),
                        "NFC-адептер отсутствует!\nЗавершение работы(2)...", Toast.LENGTH_SHORT);
                toast1.show();
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "NFC-адептер отсутствует!\nЗавершение работы(1)...", Toast.LENGTH_SHORT);
                toast2.show();
                finish();
            }
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                   getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            setSupportActionBar(toolbar);
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            ButtonEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DoLogin doLogin = new DoLogin(); // this is the Asynctask
                    doLogin.execute("");
                }
            });


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка главного окна. "+e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_send) {
            startActivity(intent_audit);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onPause() {

        try {
            super.onPause();
            if (mAdapter != null) {
                mAdapter.disableForegroundDispatch(this);
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка NFC.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight mifare = MifareUltralight.get(tagFromIntent);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(6);
            byte[] payload2 = mifare.readPages(7);
            String data =  new String(payload, Charset.defaultCharset().forName("UTF-8")) + new String(payload2, Charset.defaultCharset().forName("UTF-8"));
            String[] parts = data.split("#");
            EditTextLogin.setText(parts[1]);
            EditTextPassword.setText(parts[2]);
            DoLogin doLogin = new DoLogin();
            doLogin.execute("");

        } catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка NFC. Введите данные вручную.", Toast.LENGTH_SHORT);
            toast.show();
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                }
                catch (IOException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите данные вручную.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }

    }
    private class DoLogin extends AsyncTask<String, String, String> {
        String signature = "";
        Boolean isSuccess = false;


        String TextLogin = EditTextLogin.getText().toString();
        String TextPassword = EditTextPassword.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            try {
                pbbar.setVisibility(View.GONE);
                if (!check2) {
                    {
                        if (isSuccess & !Powers.contains("Аудитор")) {
                            Toast.makeText(AuthorizationActivity.this, r+ ".\n"+"Пройдите тест!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AuthorizationActivity.this, TestActivity.class);
                            intent.putExtra("EmployeeName", NameEmployee);
                            intent.putExtra("URL", URL);
                            intent.putExtra("VideoURL", VideoURL);
                            intent.putExtra("ID", ID);
                            intent.putExtra("IndexEmployee", IndexEmployee);
                            intent.putExtra("TestURL", TestID);
                            FirebaseMessaging.getInstance().subscribeToTopic(PlaceN);
                            startActivity(intent);
                            finish();
                        } else {
                            if (check) {
                                Toast.makeText(AuthorizationActivity.this, "Аудитор авторизован!", Toast.LENGTH_SHORT).show();
                                EditTextPassword.setVisibility(View.GONE);
                                ButtonEnter.setVisibility(View.GONE);
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                textView.setText(NameEmployee + " , Вы готовы к проведению аудита! Авторизуйте NFC-карту работника!");
                                check2 = true;
                            }

                        }
                    }
                }
                else
                {
                    textView.setText("Проверяемый работник: "+ NameEmployee);
                    intent_audit = new Intent(AuthorizationActivity.this, AuditActivity.class);
                    intent_audit.putExtra("EmployeeName", NameEmployee);
                    intent_audit.putExtra("URL", URL);
                    intent_audit.putExtra("VideoURL", VideoURL);
                    intent_audit.putExtra("ID", ID);
                    intent_audit.putExtra("IndexEmployee", IndexEmployee);
                    intent_audit.putExtra("Line",Line);
                    intent_audit.putExtra("PlaceN", PlaceN);
                    intent_audit.putExtra("AuditName",NameAudit);
                    intent_audit.putExtra("AuditDate",AuditDate);
                    intent_audit.putExtra("AuditResults",AuditResults);
                    imageView1.setVisibility(View.VISIBLE);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка "+ e.getMessage()+". Обратитесь к администратору!", Toast.LENGTH_SHORT);
                toast.show();
            }

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
                conn=DriverManager.getConnection(ConnURL);
            }catch(SQLException se){
                Toast toast = Toast.makeText(getApplicationContext(),
                        se.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }catch(ClassNotFoundException e){
                Toast toast = Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }catch(Exception e){
                Toast toast = Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
            return conn;
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                if (TextLogin.trim().equals("") || TextPassword.trim().equals(""))
                    signature = "Пожалуйста, введите логин и/или пароль";
                else {
                    try {
                        Connection con = CONN();
                        if (con == null) {
                            signature = "Ошибка при подключении к SQL серверу";
                        } else {
                            String query = "SELECT Auth.Powers from Auth where Id='" + TextLogin + "' and Password='" + TextPassword + "'";
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(query);
                            if (rs.next())
                            {
                                signature = "Вы успешно вошли";
                                isSuccess = true;
                                if (rs.getString("Powers").contains("Работник"))
                                {
                                    queryData = "SELECT E.ID, E.Name, I.InstURL, E.VideoURL, E.Line, E.PlaceN, E.IndexEmployee, A.Powers, E.AuditDate, T.TestURL, E.AuditClaims FROM Employee E, Instructions I, Tests T, Auth A, WorkPlaces W WHERE A.ID=E.ID AND E.PlaceN=I.PlaceN AND E.ID="+TextLogin+"";
                                }
                                if (rs.getString("Powers").contains("Аудитор"))
                                {
                                    queryData = "SELECT E.ID, E.Name, InstURL='', E.VideoURL, E.Line, E.PlaceN, E.IndexEmployee, A.Powers, E.AuditDate FROM Employee E, Auth A WHERE A.ID=E.ID AND E.ID="+TextLogin+"";

                                }
                            }
                            else
                            {
                                signature = "Неверные учетные данные";
                                isSuccess = false;
                            }

                                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                WifiInfo wifiInfo = wifi.getConnectionInfo();
                                int ip = wifiInfo.getIpAddress();
                                String ipAddress = Formatter.formatIpAddress(ip);
                                Statement stmtData = con.createStatement();
                                ResultSet rsData = stmtData.executeQuery(queryData);
                                if (rsData.next()) {
                                    ID = rsData.getString("ID");
                                    NameEmployee = rsData.getString("Name");
                                    URL = rsData.getString("InstURL");
                                    VideoURL = rsData.getString("VideoURL");
                                    Line = rsData.getString("Line");
                                    PlaceN = rsData.getString("PlaceN");
                                    IndexEmployee = rsData.getString("IndexEmployee");
                                    if (!check) {
                                        Powers = rsData.getString("Powers");
                                        NameAudit = NameEmployee;
                                        check = true;
                                    }
                                    AuditDate = rsData.getString("AuditDate");
                                    AuditResults = rsData.getString("AuditClaims");
                                }
                                String queryTest = "SELECT TOP 1 TestURL FROM Tests WHERE TestNum="+ ID +" ORDER BY NEWID()";
                                stmtData = con.createStatement();
                                rsData = stmtData.executeQuery(queryTest);
                                if (rsData.next() & rsData!=null) {
                                    TestID = rsData.getString("TestURL");
                                }
                                String queryUpdateIP = "UPDATE Tablets SET Tablets_IP='"+ ipAddress +"', Tablets_Status='В сети' WHERE Tablets_Line='"+Line+"' AND Tablets_WorkPlaceN='"+PlaceN+"'";
                                Statement stmtIp = con.createStatement();
                                stmtIp.executeUpdate(queryUpdateIP);
                                String queryUpdateStatus = "UPDATE Employee SET Status='ONLINE' WHERE ID='"+ID+"'";
                                stmt = con.createStatement();
                                stmt.executeUpdate(queryUpdateStatus);
                        }
                    } catch (Exception ex) {
                        isSuccess = false;
                        signature = "Ошибка!" + ex.getMessage();
                    }
                }
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка запроса к БД. Обратитесь к администратору!", Toast.LENGTH_SHORT);
                toast.show();
            }
            return signature;
        }
    }

}




