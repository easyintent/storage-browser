package io.github.bangun.storagebrowser.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.Node;

public class NodeListAdapter<T extends Node> extends ArrayAdapter<T> {

    private LayoutInflater inflater;
    private NodeActionListener listener;

    private SimpleDateFormat dateFormat;

    public NodeListAdapter(Context context, List<T> list, NodeActionListener listener) {
        super(context, 0, 0, list);
        this.listener = listener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        holder.summary.setText(dateFormat.format(node.getModified()));
        holder.icon.setImageDrawable(node.getIcon(getContext()));

        addListener(holder.more, node);

        return row;
    }

    private final void addListener(final View moreView, final Node node) {
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menu = node.isDirectory() ? R.menu.dir_popup : R.menu.file_popup;
                menu = !node.hasParent() ? R.menu.top_level_popup : menu;
                final PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(menu, popup.getMenu());
                addPopupListener(popup, node);
                popup.show();
            }
        });
    }

    private final void addPopupListener(final PopupMenu popup, final Node node) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleAction(item, node);
                return true;
            }
        });
    }

    private void handleAction(MenuItem item, Node node) {
        switch (item.getItemId()) {
            case R.id.action_view:
                listener.onView(node);
                break;
            case R.id.action_delete:
                listener.onDelete(node);
                break;
            case R.id.action_rename:
                listener.onRename(node);
                break;
            case R.id.action_copy_to:
                listener.onCopy(node);
                break;
            case R.id.action_remove_from_list:
                listener.onRemoveFromList(node);
                break;
        }
    }

    static class ViewHolder {
        TextView name;
        TextView summary;
        ImageView icon;
        View more;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.name_view);
            this.summary = (TextView) view.findViewById(R.id.summary_view);
            this.icon = (ImageView) view.findViewById(R.id.icon_view);
            this.more = view.findViewById(R.id.more_view);
        }
    }
}
