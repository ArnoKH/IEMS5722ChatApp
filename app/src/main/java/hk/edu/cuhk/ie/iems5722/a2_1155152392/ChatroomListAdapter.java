package hk.edu.cuhk.ie.iems5722.a2_1155152392;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> implements View.OnClickListener {
    private final int resourceID;
    private Callback mCallback;

    public interface Callback {
        public void click(View v);
    }

    public ChatroomListAdapter(Context context, int textViewResourceID, List<Chatroom> objects, Callback callback){
        super(context,textViewResourceID,objects);
        resourceID = textViewResourceID;
        mCallback=callback;
    }

    static class Viewholder{
        public Button btnchatroom;
        public TextView rid;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chatroom ctroom = getItem(position); //获取当前Chatroom实例
        View view;
        ChatroomListAdapter.Viewholder viewholder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
            //convertView = mLayoutInflater.inflate(R.layout.layout_chatroomlist_item,null);
            viewholder = new ChatroomListAdapter.Viewholder();
            viewholder.btnchatroom = view.findViewById(R.id.btn_roomname);
            viewholder.rid = view.findViewById(R.id.tv_roomid);
            view.setTag(viewholder); //将viewholder存储在view中
        } else {
            view = convertView;
            viewholder = (ChatroomListAdapter.Viewholder) view.getTag(); //重新获取Viewholder
        }
        //给控件赋值
        viewholder.btnchatroom.setText(ctroom.getRoomname());
        viewholder.rid.setText(ctroom.getRoomID());
        viewholder.btnchatroom.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        mCallback.click(v);
    }
}
