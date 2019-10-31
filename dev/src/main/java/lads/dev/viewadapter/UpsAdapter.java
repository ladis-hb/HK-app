package lads.dev.viewadapter;

import android.database.sqlite.SQLiteDatabase;
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
import lads.dev.fragment.DevUpsFragment;


/**
 * Created by Administrator on 2019-07-27
 */
public class UpsAdapter extends RecyclerView.Adapter<UpsAdapter.ViewHolder>{
    List<String> upslist;
    DevUpsFragment devUpsFragment;
    SQLiteDatabase db;
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView devText;
        public ViewHolder(View view) {
            super(view);
            devText = view.findViewById(R.id.content_item_text);
        }
    }

    public UpsAdapter(DevUpsFragment devUpsFragment, SQLiteDatabase db,List<String> upslist) {
        this.devUpsFragment = devUpsFragment;
        this.db = db;
        this.upslist = upslist;
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
                String str = upslist.get(idx);
                Toast.makeText(view.getContext(), str, Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("UpsAdapter", "onLongClick");
                int idx = viewHolder.getAdapterPosition();
                String str = upslist.get(idx);
                showPopmenu(v, str);

                return false;
            }
        });

        return viewHolder;
    }

    private void showPopmenu(final View view, final String devname) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.popmenu_opt, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_opt_delete:
                        try {
                            db.execSQL("delete from dev_ups where dev_name=?", new String[]{devname});
                            Toast.makeText(view.getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                            //reloadUpslist();
                            devUpsFragment.reloadDev();
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "操作失败，"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

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
                //Toast.makeText(view.getContext(), "close", Toast.LENGTH_SHORT).show();
            }
        });
        menu.show();
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.devText.setText(upslist.get(position));
    }

    @Override
    public int getItemCount() {
        return upslist.size();
    }

    public List<String> getUpslist() {
        return upslist;
    }

    public void setUpslist(List<String> upslist) {
        this.upslist = upslist;
    }
}
