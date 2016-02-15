package pl.piotrgorczyca.myrunnerapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import pl.piotrgorczyca.myrunnerapp.R;

/**
 * Created by Piotr on 2015-12-26. Enjoy!
 */
public class MailboxListAdapter extends BaseAdapter {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_NEW = 1;
    private static final int TYPE_MAX_COUNT = 2;

    private ArrayList<MailboxListItem> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private TreeSet mNewSet = new TreeSet();



        public MailboxListAdapter(Context context) {
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addNormalItem(String name, String content, int sender_id, int is_new, String created_at) {
            mData.add(new MailboxListItem(name, content, sender_id, is_new, created_at));
            notifyDataSetChanged();
        }

        public void addNewItem(String name, String content, int sender_id, int is_new, String created_at) {
            mData.add(new MailboxListItem(name, content, sender_id, is_new, created_at));
            mNewSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mNewSet.contains(position) ? TYPE_NEW : TYPE_NORMAL;
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
                // inflate the GridView item layout
                viewHolder = new ViewHolder();
                switch(type) {
                    case TYPE_NORMAL:
                        convertView = mInflater.inflate(R.layout.mailbox_list_item, null);
                        viewHolder.name = (TextView) convertView.findViewById(R.id.mailbox_list_item_name);
                        viewHolder.time = (TextView) convertView.findViewById(R.id.mailbox_list_item_time);
                        viewHolder.content = (TextView) convertView.findViewById(R.id.mailbox_list_item_content);
                        break;

                    case TYPE_NEW:
                        convertView = mInflater.inflate(R.layout.mailbox_list_item_new, null);
                        viewHolder.name = (TextView) convertView.findViewById(R.id.mailbox_list_item_new_name);
                        viewHolder.time = (TextView) convertView.findViewById(R.id.mailbox_list_item_new_time);
                        viewHolder.content = (TextView) convertView.findViewById(R.id.mailbox_list_item_new_content);
                        break;
                }
                 convertView.setTag(viewHolder);
            } else {
                // recycle the already inflated view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // update the item view
            MailboxListItem item = getItem(position);

            viewHolder.name.setText(item.getName());
            viewHolder.time.setText(item.getCreatedAt());
            viewHolder.content.setText(item.getContent());
            return convertView;

        }


    private static class ViewHolder {
            TextView name;
            TextView time;
            TextView content;
        }
}

