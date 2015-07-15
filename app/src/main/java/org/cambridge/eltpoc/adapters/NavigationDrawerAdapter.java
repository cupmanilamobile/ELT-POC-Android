package org.cambridge.eltpoc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.cambridge.eltpoc.R;

/**
 * Created by etorres on 6/23/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {
    private static final int ITEM_COUNT = 3;
    private String[] navigationArray;
    private int[] navigationDrawables;
    private Context context;
    private boolean isRemoved = false;

    public NavigationDrawerAdapter(Context context, String[] navigationArray, int[] navigationDrawables) {
        this.navigationArray = navigationArray;
        this.navigationDrawables = navigationDrawables;
        this.context = context;
    }

    public void removeTeachingTab(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public int getCount() {
        if (isRemoved)
            return ITEM_COUNT - 1;
        return ITEM_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.navigation_item, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.navigation_text);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.navigation_icon);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        if(!isRemoved || position < getCount()-1) {
            viewHolder.text.setText(navigationArray[position]);
            viewHolder.icon.setImageResource(navigationDrawables[position]);
        }
        else if(isRemoved && position == getCount()-1) {
            viewHolder.text.setText(navigationArray[2]);
            viewHolder.icon.setImageResource(navigationDrawables[2]);
        }
        convertView.setId(position);
        return convertView;
    }

    private static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
