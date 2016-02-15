package pl.piotrgorczyca.myrunnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import pl.piotrgorczyca.myrunnerapp.Activities.ProfileActivity;
import pl.piotrgorczyca.myrunnerapp.R;

/**
 * Created by Piotr on 2015-12-29. Enjoy!
 */
public class CommentsListAdapter extends BaseAdapter {

    private ArrayList<CommentsListItem> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public CommentsListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CommentsListItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comments_list_item, null);
            viewHolder.name = (TextView)convertView.findViewById(R.id.comments_list_name);
            viewHolder.content = (TextView)convertView.findViewById(R.id.comments_list_content);
            viewHolder.created_at = (TextView)convertView.findViewById(R.id.comments_list_created_at);
            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        CommentsListItem item = getItem(position);
        viewHolder.name.setText(item.getName());
        viewHolder.content.setText(item.getContent());
        viewHolder.created_at.setText(item.getCreated_at());
        return convertView;

    }

    public void add(String name, String content, String created_at) {
        mData.add(new CommentsListItem(name, content, created_at));
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView name;
        TextView content;
        TextView created_at;
    }
}
