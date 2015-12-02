package chat.signalr.android.com.androidsrchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
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

    public HubProxy getHub(){
        return this.mHub;
    }

    private String chatting = "";
    private ChatMsgHelper chatMsgHelper;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("chatFrag");
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            dataList.setVisibility(View.VISIBLE);
            chatting = "";
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Intent intent = getIntent();
        mName = intent.getStringExtra("username");

        chatMsgHelper = new ChatMsgHelper(this);

        userArray.add(new User(R.drawable.robot_ui_00, "Tonii", 0));
        adapter = new UserListAdapter(this, R.layout.list, userArray);
        dataList = (ListView) findViewById(R.id.list);
        dataList.setAdapter(adapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Instantiate a new fragment.
                Fragment newFragment = new ChatFragment();
                Bundle args = new Bundle();
                chatting = userArray.get(position).getName();
                args.putString("chatName", chatting);
                args.putString("myName", mName);
                newFragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("chatFrag");
                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                ft.add(R.id.ll_container, newFragment, "chatFrag");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();

                //View thisView = dataList.getChildAt(position);
                //thisView.findViewById(R.id.tv_count).setVisibility(View.GONE);
                userArray.get(position).setMsgCnt(0);
                adapter.notifyDataSetChanged();
                dataList.setVisibility(View.GONE);
            }
        });
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

        mHub.on("addNewMessageToPage", new SubscriptionHandler2<String, String>() {
            @Override
            public void run(String name,String message) {
                //使用AsyncTask來更新畫面
                new AsyncTask<String,Void,ChatMessage>(){
                    @Override
                    protected ChatMessage doInBackground(String... param) {
                        ChatMessage chatMessage =
                                new ChatMessage(param[0], false, param[1], DateFormat.getDateTimeInstance().format(new Date()));
                        return chatMessage;
                    }
                    @Override
                    protected void onPostExecute(ChatMessage chatMessage) {
                        chatMsgHelper.insert(chatMessage);

                        if(!TextUtils.isEmpty(chatting) && chatting.equals(chatMessage.getWithName())){
                            ChatFragment fragment = (ChatFragment)getSupportFragmentManager().findFragmentByTag("chatFrag");
                            if (fragment != null)
                                fragment.displayMessage(chatMessage);
                        }else if(TextUtils.isEmpty(chatting)){
                            for(int j = userArray.size()-1; j >= 0; j--){
                                if(userArray.get(j).getName().equals(chatMessage.getWithName())){
                                    int cnt = userArray.get(j).getMsgCnt();
                                    userArray.get(j).setMsgCnt(cnt + 1);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        super.onPostExecute(chatMessage);
                    }
                }.execute(name,message);
            }
        }, String.class,String.class);

        mHub.on("robotFeedback", new SubscriptionHandler2<String, String>() {
            @Override
            public void run(String who, String message) {
                //使用AsyncTask來更新畫面
                new AsyncTask<String,Void,ChatMessage>(){
                    @Override
                    protected ChatMessage doInBackground(String... param) {
                        ChatMessage chatMessage =
                                new ChatMessage("Tonii", false, param[1], DateFormat.getDateTimeInstance().format(new Date()));
                        return chatMessage;
                    }
                    @Override
                    protected void onPostExecute(ChatMessage chatMessage) {
                        chatMsgHelper.insert(chatMessage);

                        if(!TextUtils.isEmpty(chatting) && chatting.equals(chatMessage.getWithName())){
                            ChatFragment fragment = (ChatFragment)getSupportFragmentManager().findFragmentByTag("chatFrag");
                            if (fragment != null)
                                fragment.displayMessage(chatMessage);
                        }else if(TextUtils.isEmpty(chatting)){
                            for(int j = userArray.size()-1; j >= 0; j--){
                                if(userArray.get(j).getName().equals(chatMessage.getWithName())){
                                    int cnt = userArray.get(j).getMsgCnt();
                                    userArray.get(j).setMsgCnt(cnt + 1);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        super.onPostExecute(chatMessage);
                    }
                }.execute(who, message);
            }
        }, String.class, String.class);

        mHub.on("online", new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                new AsyncTask<String,Void,User>(){
                    @Override
                    protected User doInBackground(String... param) {
                        User user = new User(R.mipmap.ic_launcher, param[0], 0);
                        return user;
                    }
                    @Override
                    protected void onPostExecute(User user) {
                        boolean flag = true;
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(user.getName())){
                                flag = false;
                            }
                        }
                        if(flag) userArray.add(user);
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(mName))
                                userArray.remove(j);
                        }
                        adapter.notifyDataSetChanged();
                        //adapter = new UserListAdapter(context, R.layout.list, userArray);
                        //dataList.setAdapter(adapter);
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
                        User user = new User(R.mipmap.ic_launcher, param[0], 0);
                        return user;
                    }
                    @Override
                    protected void onPostExecute(User user) {
                        boolean flag = true;
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(user.getName())){
                                flag = false;
                            }
                        }
                        if(flag) userArray.add(user);
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(mName))
                                userArray.remove(j);
                        }
                        adapter.notifyDataSetChanged();
                        //adapter = new UserListAdapter(context, R.layout.list, userArray);
                        //dataList.setAdapter(adapter);
                        super.onPostExecute(user);
                    }
                }.execute(name);
            }
        }, String.class);

        mHub.on("disconnected", new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                new AsyncTask<String,Void,User>(){
                    @Override
                    protected User doInBackground(String... param) {
                        User user = new User(R.mipmap.ic_launcher, param[0], 0);
                        return user;
                    }
                    @Override
                    protected void onPostExecute(User user) {
                        for(int j = userArray.size()-1; j >= 0; j--){
                            if(userArray.get(j).getName().equals(user.getName())){
                                userArray.remove(j);
                                adapter.notifyDataSetChanged();
                            }
                        }
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
