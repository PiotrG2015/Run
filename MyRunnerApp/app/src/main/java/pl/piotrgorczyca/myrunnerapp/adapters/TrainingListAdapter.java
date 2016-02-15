package pl.piotrgorczyca.myrunnerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.piotrgorczyca.myrunnerapp.R;

/**
 * Created by Piotr on 2015-12-13. Enjoy!
 */
public class TrainingListAdapter extends BaseAdapter {

    private ArrayList<TrainingListItem> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public TrainingListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public TrainingListItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(String name, String place, int distance, String time_of_training, int tid, int user_id) {
        mData.add(new TrainingListItem(name, place, distance, time_of_training, tid, user_id));
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.training_list_item, null);

            // initialize the view holder
            viewHolder.name = (TextView) convertView.findViewById(R.id.training_list_item_name);
            viewHolder.place = (TextView) convertView.findViewById(R.id.training_list_item_place);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.training_list_item_distance);
            viewHolder.date = (TextView) convertView.findViewById(R.id.training_list_item_time);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        TrainingListItem item = getItem(position);
        viewHolder.name.setText(item.getName());
        viewHolder.place.setText(item.getPlace());
        viewHolder.distance.setText(String.valueOf(item.getDistance()) + " km");
        viewHolder.date.setText(item.getDate());
        return convertView;

    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView name;
        TextView distance;
        TextView place;
        TextView date;
    }
}
