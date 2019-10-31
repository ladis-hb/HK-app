package lads.dev.viewadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import lads.dev.R;
import lads.dev.entity.KeyValueEntity;


/**
 * Created by Administrator on 2019-07-27
 */
public class HisDataAdapter extends RecyclerView.Adapter<HisDataAdapter.ViewHolder>{
    List<KeyValueEntity> kvlist;
    Context context;
    HisDataAdapterCallback mHisDataAdapterCallback;
    public void setHisDataAdapterCallback(HisDataAdapterCallback hisDataAdapterCallback) {
        this.mHisDataAdapterCallback = hisDataAdapterCallback;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        TextView createtime;
        public ViewHolder(View view) {
            super(view);
            msg = view.findViewById(R.id.contentitem_hisdata_msg);
            createtime = view.findViewById(R.id.contentitem_hisdata_createtime);
        }
    }

    public HisDataAdapter(Context context, List<KeyValueEntity> kvlist) {
        this.context = context;
        this.kvlist = kvlist;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item_hisdata, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = viewHolder.getAdapterPosition();
                String value = kvlist.get(idx).getValue();
                String value2 = kvlist.get(idx).getValue2();
                if(mHisDataAdapterCallback!=null) {
                    mHisDataAdapterCallback.itemClick(value2);
                }
            }
        });
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.msg.setText(kvlist.get(position).getValue());
        holder.createtime.setText(kvlist.get(position).getKey());
    }

    @Override
    public int getItemCount() {
        return kvlist.size();
    }

    public interface HisDataAdapterCallback{
        void itemClick(String msg);
    }

}
