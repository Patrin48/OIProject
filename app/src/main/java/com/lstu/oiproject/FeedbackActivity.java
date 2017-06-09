package com.lstu.oiproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedbackActivity extends AppCompatActivity {

    String IndexEmployee,EmployeeName, NameAudit, Line, PlaceN, ID;
    String Feed="", additional;
    EditText editText;
    TextView textView, textViewdop;
    CheckBox checkBox, checkBox1,checkBox2,checkBox3,checkBox4,checkBox5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Intent intent = getIntent();
        IndexEmployee=intent.getStringExtra("IndexEmployee");
        EmployeeName = intent.getStringExtra("EmployeeName");
        NameAudit = intent.getStringExtra("AuditName");
        Line = intent.getStringExtra("Line");
        PlaceN = intent.getStringExtra("PlaceN");
        ID = intent.getStringExtra("ID");
        textView = (TextView) findViewById(R.id.textView);
        textViewdop = (TextView) findViewById(R.id.textView5);
        editText = (EditText) findViewById(R.id.editText);
        textView.setText("Отклик "+ NameAudit.split(" ")[0]+ " на " + EmployeeName.split(" ")[0]);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox6);
        textViewdop.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);

    }
    protected void sendEmail() {
        String[] TO = {"qualita@whirlpool.com"};
        String[] CC = {"audit@whirlpool.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        additional = editText.getText().toString();
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Несоответствие работника");
        String Feedback = "Я, "+NameAudit+ ", произвел проверку работника: "+ EmployeeName + ".\n" +"№ пропуска: "+ ID+ "."+"\n" +"В ходе проверки были найдены следующие несоответствия: "+"\n"+ Feed+"•"+ "Доп. информация: "+ additional+"";

        emailIntent.putExtra(Intent.EXTRA_TEXT,Feedback);

        try {
            startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FeedbackActivity.this,
                    "Клиент почты не установлен! Отправка письма невозможна!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        Feed+= "•"+ checkBox1.getText()+ ", "+ "\n";
    }

    public void onClick1(View view) {

        Feed+="•"+checkBox.getText()+ "; "+ "\n";
    }

    public void onClick2(View view) {
        Feed+="•"+checkBox2.getText()+"; "+ "\n";
    }

    public void onClick3(View view) {
        Feed+= "•"+checkBox3.getText()+"; "+ "\n";
    }

    public void onClick4(View view) {
        Feed+="•"+checkBox4.getText()+ "; "+ "\n";
    }

    public void onClick5(View view) {
        if (textViewdop.getVisibility()==View.VISIBLE & editText.getVisibility()==View.VISIBLE)
        {
            textViewdop.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
        }
        else {
            textViewdop.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
        }
    }
    public void sendOnClick(View view) {
        UpdateAudit updateAudit = new  UpdateAudit(); // this is the Asynctask
        updateAudit.execute(Feed);
        sendEmail();
    }

    public class UpdateAudit extends AsyncTask<String, String, String> {
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
                    Connection con = CONN();
                    if (con == null) {
                        signature = "Ошибка при подключении к SQL серверу";
                    } else {
                        String queryUpdateIP = "UPDATE Employee SET AuditDate=SYSDATETIME(),AuditClaims='"+Feed+"' WHERE Employee.ID="+ID+"";
                        Statement stmtIp = con.createStatement();
                        stmtIp.executeUpdate(queryUpdateIP);
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    signature = "Ошибка!" + ex.getMessage();
                }
            return signature;
        }
    }
}
