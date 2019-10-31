package lads.dev.viewadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lads.dev.R;


/**
 * Created by Administrator on 2019-07-27
 */
public class EmAdapter extends RecyclerView.Adapter<EmAdapter.ViewHolder>{
    List<String> devlist;
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        public ViewHolder(View view) {
            super(view);
            txt = view.findViewById(R.id.content_item_text);
        }
    }

    public EmAdapter(List<String> devlist) {
        this.devlist = devlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = viewHolder.getAdapterPosition();
                String str = devlist.get(idx);
                Toast.makeText(view.getContext(), str, Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("EmAdapter", "onLongClick");
                showPopmenu(v);

                return false;
            }
        });

        return viewHolder;
    }

    private void showPopmenu(final View view) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.popmenu_opt, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_opt_delete:
                        Toast.makeText(view.getContext(), "delete Em", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_opt_setserial:
                        Toast.makeText(view.getContext(), "set serial", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(view.getContext(), "close", Toast.LENGTH_SHORT).show();
            }
        });
        menu.show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt.setText(devlist.get(position));
    }

    @Override
    public int getItemCount() {
        return devlist.size();
    }
}
