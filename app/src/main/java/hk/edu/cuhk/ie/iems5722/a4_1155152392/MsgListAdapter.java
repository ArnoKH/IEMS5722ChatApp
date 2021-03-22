package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MsgListAdapter extends ArrayAdapter<Msg> {
    private final int resourceID;

    public MsgListAdapter(Context context, int textViewResourceID, List<Msg> objects){
        super(context,textViewResourceID,objects);
        resourceID = textViewResourceID;
    }

    static class Viewholder{
        public TextView tvUsername,tvContent,tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position); //获取当前Msg实例
        View view;
        Viewholder viewholder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            //convertView = mLayoutInflater.inflate(R.layout.layout_msglist_item,null);
            viewholder = new Viewholder();
            viewholder.tvUsername = view.findViewById(R.id.msg_username);
            viewholder.tvContent = view.findViewById(R.id.msg_content);
            viewholder.tvTime = view.findViewById(R.id.msg_time);
            view.setTag(viewholder); //将viewholder存储在view中
        }else{
            view = convertView;
            viewholder = (Viewholder) view.getTag(); //重新获取Viewholder
        }
        //给控件赋值
        viewholder.tvUsername.setText(msg.getUserName());
        viewholder.tvContent.setText(msg.getContent());
        viewholder.tvTime.setText(msg.getTime());
        return view;
    }
}
