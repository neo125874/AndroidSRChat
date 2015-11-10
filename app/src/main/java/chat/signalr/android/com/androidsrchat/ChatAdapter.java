package chat.signalr.android.com.androidsrchat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tw4585 on 2015/11/9.
 */
public class ChatAdapter extends ArrayAdapter<ChatData> {
    private String mName;
    public ChatAdapter(Context context, int resource, List<ChatData> objects,String mName) {
        super(context, resource, objects);
        this.mName = mName;
    }

    private class ViewHolder {
        TextView nameTv;
        TextView messageTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatData chatData = getItem(position);
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
            holder.messageTv = (TextView) convertView.findViewById(R.id.messageTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTv.setText(chatData.getName());
        holder.messageTv.setText(chatData.getMessage());

        if(chatData.getName().equals(mName)){
            holder.nameTv.setTextColor(Color.RED);
        }
        return convertView;
    }
}
