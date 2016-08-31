package main.app.tbee3app;

/**
 * Created by igorkhomenko on 9/12/14.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import vc908.stickerfactory.StickersManager;

public class DialogsAdapter extends BaseAdapter {
    private List<QBDialog> dataSource;
    private List<String> userimages;
    private LayoutInflater inflater;
    private Activity ctx;

    public DialogsAdapter(List<QBDialog> dataSource, Activity ctx, List<String> userimages) {
        this.dataSource = dataSource;
        this.userimages = userimages;
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public List<QBDialog> getDataSource() {
        return dataSource;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // initIfNeed view
        //
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_room, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.roomName);
            holder.lastMessage = (TextView)convertView.findViewById(R.id.lastMessage);
            holder.groupType = (TextView)convertView.findViewById(R.id.textViewGroupType);
            holder.userimage = (ImageView)convertView.findViewById(R.id.roomImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set data
        //
        QBDialog dialog = dataSource.get(position);
        if(dialog.getType().equals(QBDialogType.GROUP)){
            holder.name.setText(dialog.getName());
        }else{
            // get opponent name for private dialog
            //
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
            QBUser user = ChatService.getInstance().getDialogsUsers().get(opponentID);
            if(user != null){
                holder.name.setText(user.getFullName() != null ? user.getFullName() : user.getLogin());
            }
        }
        if (dialog.getLastMessage() != null && StickersManager.isSticker(dialog.getLastMessage())) {
            holder.lastMessage.setText("Sticker");
        } else {
            holder.lastMessage.setText(dialog.getLastMessage());
        }
        holder.groupType.setText(dialog.getType().toString());
        try {
            Picasso.with(ctx).load(userimages.get(position)).into(holder.userimage);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView name;
        TextView lastMessage;
        TextView groupType;
        ImageView userimage;
    }



}
