package io.github.bangun.storagebrowser.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.Node;

public class DocumentListAdapter<T extends Node> extends ArrayAdapter<T> {

    private LayoutInflater inflater;
    public DocumentListAdapter(Context context, List<T> list) {
        super(context, 0, 0, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.item_file_node, parent, false);
            row.setTag(new ViewHolder(row));
        }

        Node node = getItem(position);
        ViewHolder holder = (ViewHolder) row.getTag();
        holder.name.setText(node.getName());
        holder.summary.setText(node.getSummary(getContext()));
        holder.icon.setImageDrawable(node.getIcon(getContext()));

        return row;
    }

    static class ViewHolder {
        TextView name;
        TextView summary;
        ImageView icon;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.name_view);
            this.summary = (TextView) view.findViewById(R.id.summary_view);
            this.icon = (ImageView) view.findViewById(R.id.icon_view);
        }
    }
}
