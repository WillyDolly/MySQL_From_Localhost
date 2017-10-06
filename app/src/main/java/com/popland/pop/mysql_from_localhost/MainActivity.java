package com.popland.pop.mysql_from_localhost;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
ListView lv;
    ArrayList<Sanpham> arrl;
    EditText edtTensp, edtGiasp;
    Button btnThem, btnUpdate;
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edtTensp = (EditText)findViewById(R.id.EDTtensp);
        edtGiasp = (EditText)findViewById(R.id.EDTgiasp);
        btnThem = (Button)findViewById(R.id.BTNthem);
        btnUpdate = (Button)findViewById(R.id.BTNupdate);
        lv = (ListView)findViewById(R.id.LV);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getData().execute();
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new insert().execute();
                    }
                });
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ID = arrl.get(position).id;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new delete().execute();
                    }
                });
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ID = arrl.get(position).id;
                edtTensp.setText(arrl.get(position).tensp);
                edtGiasp.setText(arrl.get(position).giasp+"");
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new update().execute();
                    }
                });
            }
        });
    }

    class getData extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_TuURL("http://mywebhosting.esy.es/getData.php");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                arrl = new ArrayList<Sanpham>();
                JSONArray array = new JSONArray(s);
                for(int i=0;i<array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    arrl.add(new Sanpham(object.getInt("id"),object.getString("tensp"),object.getInt("giasp")));
                }
                CustomListAdapter adapter = new CustomListAdapter(MainActivity.this,R.layout.custom_row,arrl);
                lv.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class insert extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            return makePostRequest_insert();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                Toast.makeText(MainActivity.this,"thanhcong"+s,Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this,"thatbai"+s,Toast.LENGTH_LONG).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getData().execute();
                }
            });
        }
    }

    class delete extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            return makePostRequest_delete();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Toast.makeText(MainActivity.this,"thanh cong "+s,Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this,"that bai "+s,Toast.LENGTH_LONG).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getData().execute();
                }
            });
        }
    }

    class update extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            return makePostRequest_update();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                Toast.makeText(MainActivity.this,"thành công "+s,Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this,"thất bại "+s,Toast.LENGTH_LONG).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getData().execute();
                }
            });
        }
    }

    private String makePostRequest_update() {
        HttpClient httpClient = new DefaultHttpClient();

        // URL của trang web nhận request
        HttpPost httpPost = new HttpPost("http://mywebhosting.esy.es/update.php");

        // Các tham số truyền
        List nameValuePair = new ArrayList(3);
        nameValuePair.add(new BasicNameValuePair("id",String.valueOf(ID)));
        nameValuePair.add(new BasicNameValuePair("tensp",edtTensp.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("giasp",edtGiasp.getText().toString()));
        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Gui ket qua ve
        String kq = "";
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            kq = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return kq;
    }

    private String makePostRequest_delete() {
        HttpClient httpClient = new DefaultHttpClient();

        // URL của trang web nhận request
        HttpPost httpPost = new HttpPost("http://mywebhosting.esy.es/delete.php");

        // Các tham số truyền
        List nameValuePair = new ArrayList(1);
        nameValuePair.add(new BasicNameValuePair("id",String.valueOf(ID)));

        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Gui ket qua ve
        String kq = "";
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            kq = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return kq;
    }

    private String makePostRequest_insert() {
        HttpClient httpClient = new DefaultHttpClient();

        // URL của trang web nhận request
        HttpPost httpPost = new HttpPost("http://mywebhosting.esy.es/insert.php");

        // Các tham số truyền
        List nameValuePair = new ArrayList(2);
        nameValuePair.add(new BasicNameValuePair("tensp", edtTensp.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("giasp", edtGiasp.getText().toString()));

        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Gui ket qua ve
        String kq = "";
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            kq = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return kq;
    }

    public String docNoiDung_TuURL(String theurl){
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theurl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = bufferedReader.readLine())!=null){
                content.append(line+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
