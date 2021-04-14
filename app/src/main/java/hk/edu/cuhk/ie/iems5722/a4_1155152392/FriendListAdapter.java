package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<User> {
    private final int resourceID;

    public FriendListAdapter(Context context, int textViewResourceID, List<User> objects){
        super(context,textViewResourceID,objects);
        resourceID = textViewResourceID;
    }

    static class Viewholder{
        public TextView tvUsername,tvUserid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position); //获取当前User实例
        View view;
        FriendListAdapter.Viewholder viewholder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            //convertView = mLayoutInflater.inflate(R.layout.layout_msglist_item,null);
            viewholder = new FriendListAdapter.Viewholder();
            viewholder.tvUsername = view.findViewById(R.id.fl_username);
            viewholder.tvUserid = view.findViewById(R.id.fl_userid);
            view.setTag(viewholder); //将viewholder存储在view中
        }else{
            view = convertView;
            viewholder = (FriendListAdapter.Viewholder) view.getTag(); //重新获取Viewholder
        }
        //给控件赋值
        viewholder.tvUsername.setText(user.getUserName());
        viewholder.tvUserid.setText(user.getUserID());
        return view;
    }

}
