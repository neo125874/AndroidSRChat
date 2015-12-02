package chat.signalr.android.com.androidsrchat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by tw4585 on 2015/11/27.
 */
public class ChatFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private HubProxy fragHub;
    private String chatName, mName;

    //sqlite
    private ChatMsgHelper chatMsgHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        Bundle args = getArguments();
        TextView textView = (TextView)v.findViewById(R.id.chat_text);
        chatName = args.getString("chatName");
        mName = args.getString("myName");
        textView.setText("Chat with " + chatName);

        final EditText editText = (EditText)v.findViewById(R.id.et_send);
        Button button = (Button)v.findViewById(R.id.button);

        chatMsgHelper = new ChatMsgHelper(getActivity());
        initControls(v);
        fragHub = ((ChatRoomActivity)getActivity()).getHub();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    if(chatName.equals("Tonii")){
                        try {
                            fragHub.invoke("sendToRobot", mName, msg).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            fragHub.invoke("sendToSpecific", mName, msg, chatName).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    //chat body
                    ChatMessage chatMessage = new ChatMessage();
                    //chatMessage.setId(122);//dummy
                    chatMessage.setWithName(chatName);
                    chatMessage.setMessage(msg);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);

                    editText.setText("");

                    displayMessage(chatMessage);

                    chatMsgHelper.insert(chatMessage);
                }
            }
        });

        return v;
    }

    private ListView messagesContainer;
    private ChatBodyAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    private void initControls(View view) {
        messagesContainer = (ListView) view.findViewById(R.id.messagesContainer);

        loadDummyHistory();
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){
        chatHistory = new ArrayList<ChatMessage>();

        if(chatMsgHelper.getCount(chatName) != 0){
            chatHistory = chatMsgHelper.getAll(chatName);
        }
        /*ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setMe(false);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);*/

        adapter = new ChatBodyAdapter(getActivity(), new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }
}
