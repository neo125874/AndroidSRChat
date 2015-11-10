package chat.signalr.android.com.androidsrchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class MainActivity extends AppCompatActivity {

    private static final String HUB_URL = "http://192.168.11.85:3227/signalr/";
    private static final String HUB_NAME = "ChatHub";
    private static final String HUB_EVENT_NAME = "addNewMessageToPage";
    private static final String HUB_METHOD_NAME = "Send";
    private SignalRFuture<Void> mSignalRFuture;
    private HubProxy mHub;
    private HubConnection connection;
    private String mName;

    private ChatAdapter mChatAdapter;
    private ListView mChatLv;
    private EditText mMessageEt;
    private Button mSendBtn;
    private Spinner spinner;
    private ArrayAdapter<String> selectionList;
    private ArrayList<String> selection;
    private int selectionFlag;
    private TextView textView, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChatLv = (ListView)findViewById(R.id.chatLv);
        mMessageEt = (EditText)findViewById(R.id.messageEt);
        mSendBtn = (Button)findViewById(R.id.sendBtn);
        //select option
        spinner = (Spinner)findViewById(R.id.spinner);
        selection = new ArrayList<String>();
        selection.add("All");
        selectionList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selection);
        spinner.setAdapter(selectionList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectionFlag = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);

        mName = "Android-"+System.currentTimeMillis();
        mChatAdapter = new ChatAdapter(this,0,new ArrayList<ChatData>(),mName);

        mChatLv.setAdapter(mChatAdapter);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String message = mMessageEt.getText().toString();

                    if (selection.get(selectionFlag).equals("All")) {
                        mHub.invoke(HUB_METHOD_NAME, mName, message).get();
                    } else {
                        mHub.invoke("sendToSpecific", mName, message, selection.get(selectionFlag)).get();
                    }

                    mMessageEt.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()))
        .done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                mHub.invoke("notify", mName, connection.getConnectionId()).get();
            }
        });
        //可以理解為訊息or事件監聽器
        mHub.on(HUB_EVENT_NAME, new SubscriptionHandler2<String, String>() {
            @Override
            public void run(String name,String message) {
                //使用AsyncTask來更新畫面
                new AsyncTask<String,Void,ChatData>(){
                    @Override
                    protected ChatData doInBackground(String... param) {
                        ChatData chatData = new ChatData(param[0],param[1]);
                        return chatData;
                    }
                    @Override
                    protected void onPostExecute(ChatData chatData) {
                        mChatAdapter.add(chatData);
                        mChatLv.smoothScrollToPosition(mChatAdapter.getCount()-1);
                        super.onPostExecute(chatData);
                    }
                }.execute(name,message);
            }
        }, String.class,String.class);
        //
        mHub.on("online", new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                new AsyncTask<String,Void,ChatData>(){
                    @Override
                    protected ChatData doInBackground(String... param) {
                        ChatData chatData = new ChatData(param[0], "");
                        return chatData;
                    }
                    @Override
                    protected void onPostExecute(ChatData chatData) {
                        if(mName.equals(chatData.getName())){
                            textView.append(Html.fromHtml("<div class='border' style='color:green'>You: " + chatData.getName() + "</div>"));
                        }else {
                            textView.append(Html.fromHtml("<div class='border'>" + chatData.getName() + "</div>"));
                            selectionList.add(chatData.getName());
                            spinner.setAdapter(selectionList);
                        }
                        super.onPostExecute(chatData);
                    }
                }.execute(name);

            }
        }, String.class);
        //
        mHub.on("enters", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                new AsyncTask<String,Void,ChatData>(){
                    @Override
                    protected ChatData doInBackground(String... param) {
                        ChatData chatData = new ChatData(param[0], "");
                        return chatData;
                    }
                    @Override
                    protected void onPostExecute(ChatData chatData) {
                        textView2.append(Html.fromHtml("<div ><i>" + chatData.getName() + " joins the conversation</i></div>"));
                        selectionList.add(chatData.getName());
                        spinner.setAdapter(selectionList);
                        textView.append(Html.fromHtml("<div class='border'>" + chatData.getName() + "</div>"));
                        super.onPostExecute(chatData);
                    }
                }.execute(s);

            }
        }, String.class);
        //
        mHub.on("disconnected", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                new AsyncTask<String,Void,ChatData>(){
                    @Override
                    protected ChatData doInBackground(String... param) {
                        ChatData chatData = new ChatData(param[0], "");
                        return chatData;
                    }
                    @Override
                    protected void onPostExecute(ChatData chatData) {
                        textView2.append(Html.fromHtml("<div ><i>" + chatData.getName() + " leaves the conversation</i></div>"));
                        selectionList.remove(chatData.getName());
                        spinner.setAdapter(selectionList);
                        String html = textView.getText().toString();
                        html = html.replace(chatData.getName() +"\n\n", "");
                        textView.setText(html);
                        super.onPostExecute(chatData);
                    }
                }.execute(s);


            }
        }, String.class);

        //開啟連線
        try {
            mSignalRFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        //關閉連線
        connection.stop();
        mSignalRFuture.cancel();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
