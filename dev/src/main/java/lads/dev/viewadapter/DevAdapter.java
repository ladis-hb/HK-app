package lads.dev.viewadapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lads.dev.R;
import lads.dev.entity.KeyValueEntity;
import lads.dev.fragment.DevUpsFragment;


/**
 * Created by Administrator on 2019-07-27
 */
public class DevAdapter extends RecyclerView.Adapter<DevAdapter.ViewHolder>{
    List<KeyValueEntity> kvlist;
    Context context;
    List<Boolean> isClick = new ArrayList<>();

    DevAdapterCallback mDevAdapterCallback;
    public void setDevAdapterCallback(DevAdapterCallback devAdapterCallback) {
        this.mDevAdapterCallback = devAdapterCallback;
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView devText;
        public ViewHolder(View view) {
            super(view);
            devText = view.findViewById(R.id.content_item_text);
        }
    }

    public DevAdapter(Context context, List<KeyValueEntity> kvlist) {
        this.context = context;
        this.kvlist = kvlist;
        if(kvlist.size()>0) {
            for(int i=0;i<kvlist.size();i++) {
                isClick.add(false);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int idx = viewHolder.getAdapterPosition();
//                String key = kvlist.get(idx).getKey();
//                if(mDevAdapterCallback!=null) {
//                    mDevAdapterCallback.itemClick(key);
//                }
//            }
//        });
//
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                int idx = viewHolder.getAdapterPosition();
//                String str = kvlist.get(idx).getKey();
//                showPopmenu(v, str);
//
//                return false;
//            }
//        });

        return viewHolder;
    }

    private void showPopmenu(final View view, final String devcode) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_dev, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_hisdata:
                        if(mDevAdapterCallback!=null) {
                            mDevAdapterCallback.itemMenuClick(devcode);
                        }

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
        holder.devText.setText(kvlist.get(position).getValue());

        if(mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    if(isClick!=null && isClick.size()>0) {
                        for(int i=0;i<isClick.size();i++) {
                            isClick.set(i, false);
                        }
                        isClick.set(position, true);
                        notifyDataSetChanged();
                    }
                    String key = kvlist.get(position).getKey();
                    mOnItemClickListener.setOnClickItemListener(view, position, key);
                }
            });
        }

        if(isClick!=null && isClick.size()>0) {
            if(isClick.get(position)) {
                holder.devText.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                holder.devText.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        }
    }

    @Override
    public int getItemCount() {
        return kvlist.size();
    }

    public interface DevAdapterCallback{
        void itemClick(String devCode);
        void itemMenuClick(String devCode);
    }

    public interface OnItemClickListener{
        void setOnClickItemListener(View view,int position, String key);
    }

}
