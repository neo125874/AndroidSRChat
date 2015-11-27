package chat.signalr.android.com.androidsrchat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tw4585 on 2015/11/26.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    Context context;
    int layoutResourceId;
    LinearLayout linearMain;
    ArrayList<User> data = new ArrayList<User>();

    public UserListAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            linearMain = (LinearLayout) row.findViewById(R.id.lineraMain);

            User user = data.get(position);
            TextView textView = new TextView(context);
            textView.setText(user.getName());
            linearMain.addView(textView);
            ImageView imageView = new ImageView(context);
            int image = user.getImage();
            imageView.setImageResource(image);
            linearMain.addView(imageView);
        }

        return row;
    }
}