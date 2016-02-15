package pl.piotrgorczyca.myrunnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import pl.piotrgorczyca.myrunnerapp.R;

/**
 * Created by Piotr on 2015-12-27. Enjoy!
 */
public class ChatListAdapter extends BaseAdapter {

    private static final int TYPE_SENDED = 0;
    private static final int TYPE_RECEIVED = 1;
    private static final int TYPE_MAX_COUNT = 2;

    private ArrayList<MailboxListItem> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private TreeSet mReceivedSet = new TreeSet();

    public ChatListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addSendedItem(String content, String created_at) {
        mData.add(new MailboxListItem(content, created_at, true));
        notifyDataSetChanged();
    }

    public void addReceivedItem(String content, String created_at) {
        mData.add(new MailboxListItem(content, created_at, false));
        mReceivedSet.add(mData.size() - 1);
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return mReceivedSet.contains(position) ? TYPE_RECEIVED : TYPE_SENDED;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MailboxListItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        int type = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            switch(type) {
                case TYPE_SENDED:
                    convertView = mInflater.inflate(R.layout.chat_list_item_sended, null);
                    viewHolder.content = (TextView)convertView.findViewById(R.id.chat_list_sended_content);
                    viewHolder.time = (TextView)convertView.findViewById(R.id.chat_list_sended_time);
                    break;
                case TYPE_RECEIVED:
                    convertView = mInflater.inflate(R.layout.chat_list_item, null);
                    viewHolder.content = (TextView)convertView.findViewById(R.id.chat_list_content);
                    viewHolder.time = (TextView)convertView.findViewById(R.id.chat_list_time);
                    break;
            }
            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        MailboxListItem item = getItem(position);
        viewHolder.time.setText(item.getCreatedAt());
        viewHolder.content.setText(item.getFullContent());
        return convertView;

    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView time;
        TextView content;
    }
}
