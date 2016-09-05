package io.github.easyintent.storagebrowser.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.TopLevelDir;

public class TopLevelDirListAdapter extends ArrayAdapter<TopLevelDir> {

    private LayoutInflater inflater;
    private TopLevelDirActionListener listener;

    public TopLevelDirListAdapter(Context context, List<TopLevelDir> list, TopLevelDirActionListener listener) {
        super(context, 0, 0, list);
        this.listener = listener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.item_file_node, parent, false);
            row.setTag(new ViewHolder(row));
        }

        Context context = getContext();
        TopLevelDir item = getItem(position);
        ViewHolder holder = (ViewHolder) row.getTag();
        holder.name.setText(item.getName(context));
        holder.icon.setImageDrawable(item.getIcon(context));
        holder.summary.setText(item.getDescription(context));

        addListener(holder.more, item);

        return row;
    }

    private final void addListener(final View moreView, final TopLevelDir item) {
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.top_level_popup, popup.getMenu());
                addPopupListener(popup, item);
                popup.show();
            }
        });
    }

    private final void addPopupListener(final PopupMenu popup, final TopLevelDir item) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                handleAction(menuItem, item);
                return true;
            }
        });
    }

    private void handleAction(MenuItem menuItem, TopLevelDir item) {
        switch (menuItem.getItemId()) {
            case R.id.action_remove_from_list:
                listener.onRemoveFromList(item);
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
