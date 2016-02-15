package pl.piotrgorczyca.myrunnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.piotrgorczyca.myrunnerapp.R;

/**
 * Created by Piotr on 2015-12-30. Enjoy!
 */
public class AttendingListAdapter extends BaseAdapter {

    private ArrayList<AttendingListItem> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public AttendingListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public AttendingListItem getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.attending_list_item, null);
            viewHolder.name = (TextView)convertView.findViewById(R.id.attending_list_name);
            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        AttendingListItem item = getItem(position);
        viewHolder.name.setText(item.getName());
        return convertView;

    }

    public void add(String name, int user_id) {
        mData.add(new AttendingListItem(name, user_id));
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView name;
    }
}

