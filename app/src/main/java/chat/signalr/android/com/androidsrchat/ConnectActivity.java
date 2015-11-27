package chat.signalr.android.com.androidsrchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity {

    private EditText userName, passWord;
    private CryptoHandler _crypto;
    private Button btnLogin;

    // Creating HTTP client
    private HttpClient httpClient;
    // Creating HTTP Post
    private HttpPost httpPost;
    private String Server_Url = "http://192.168.11.85:3227/api/AndroidApi/Validation";
    private HttpResponse response;
    private HttpEntity entity;
    private InputStream is;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initialize();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userName.getText().toString();
                String pwd = passWord.getText().toString();
                validateUserTask task = new validateUserTask(ConnectActivity.this);
                task.execute(new String[]{user, pwd});

            }
        });
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setVisibility(View.GONE);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void initialize(){
        userName = (EditText)findViewById(R.id.et_username);
        passWord = (EditText)findViewById(R.id.et_password);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        _crypto = new CryptoHandler("TelexpressTonii");

        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(Server_Url);

    }

    private class validateUserTask extends AsyncTask<String, Void, String[]> {

        private ProgressDialog dialog;
        private AppCompatActivity activity;
        private Context context;
        private InputMethodManager inputMethodManager;

        public validateUserTask(AppCompatActivity activity){
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
            inputMethodManager = (InputMethodManager)activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String user = params[0];
            String pwd = params[1];
            //encrypt
            byte[] bytes = _crypto.Encrypt(pwd.getBytes());
            pwd = Base64.encodeToString(bytes, Base64.DEFAULT);

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", user));
            postParameters.add(new BasicNameValuePair("password", pwd));
            // Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }
            // Making HTTP Request
            try {
                response = httpClient.execute(httpPost);
                // writing response to log
                //Log.d("Http Response:", response.toString());

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();

            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }

            JSONObject jObject = null;
            try {
                jObject =new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String result = "";
            String[] results;
            try {
                result = jObject.get("validMsg").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            results = new String[]{user, result};

            return results;
            //return response.toString();
            /*entity = response.getEntity();
            try {
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return convertStreamToString(is);*/
        }//close doInBackground

        @Override
        protected void onPostExecute(String[] result) {

            if(result[1].equals("Valid") || result[1].equals("Insert")){
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", result[0]);
                context.startActivity(intent);
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }//close onPostExecute
    }// close validateUserTask

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
