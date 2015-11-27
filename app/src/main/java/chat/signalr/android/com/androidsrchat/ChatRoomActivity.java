package chat.signalr.android.com.androidsrchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class ChatRoomActivity extends AppCompatActivity {

    private String mName;
    private HubConnection connection;
    private static final String HUB_URL = "http://192.168.11.85:3227/signalr/";
    private HubProxy mHub;
    private static final String HUB_NAME = "ChatHub";
    private SignalRFuture<Void> mSignalRFuture;

    ArrayList<User> userArray = new ArrayList<User>();
    UserListAdapter adapter;
    ListView dataList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Intent intent = getIntent();
        mName = intent.getStringExtra("username");

        userArray.add(new User(R.drawable.robot_ui_00, "Tonii"));
        adapter = new UserListAdapter(this, R.layout.list, userArray);
        dataList = (ListView) findViewById(R.id.list);
        dataList.setAdapter(adapter);
        context = this;

        connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()))
                .done(new Action<Void>() {
                    @Override
                    public void run(Void aVoid) throws Exception {
                        mHub.invoke("notify", mName, connection.getConnectionId()).get();
                    }
                });

        mHub.on("online", new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                new AsyncTask<String,Void,User>(){
                    @Override
                    protected User doInBackground(String... param) {
                        User user = new User(R.mipmap.ic_launcher, param[0]);
                        return user;
                    }
                    @Override
                    protected void onPostExecute(User user) {
                        if(!userArray.contains(user))
                            userArray.add(user);
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(mName))
                                userArray.remove(j);
                        }
                        adapter = new UserListAdapter(context, R.layout.list, userArray);
                        dataList.setAdapter(adapter);
                        super.onPostExecute(user);
                    }
                }.execute(name);
            }
        }, String.class);

        mHub.on("enters", new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                new AsyncTask<String,Void,User>(){
                    @Override
                    protected User doInBackground(String... param) {
                        User user = new User(R.mipmap.ic_launcher, param[0]);
                        return user;
                    }
                    @Override
                    protected void onPostExecute(User user) {
                        if(!userArray.contains(user))
                            userArray.add(user);
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(mName))
                                userArray.remove(j);
                        }
                        adapter = new UserListAdapter(context, R.layout.list, userArray);
                        dataList.setAdapter(adapter);
                        super.onPostExecute(user);
                    }
                }.execute(name);
            }
        }, String.class);

    }

    @Override
    protected void onDestroy() {
        //關閉連線
        connection.stop();
        mSignalRFuture.cancel();
        super.onDestroy();
    }
}
