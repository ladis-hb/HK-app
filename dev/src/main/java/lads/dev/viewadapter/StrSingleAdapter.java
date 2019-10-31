package lads.dev.viewadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lads.dev.R;
import lads.dev.entity.KeyValueEntity;


/**
 * Created by Administrator on 2019-07-27
 */
public class StrSingleAdapter extends RecyclerView.Adapter<StrSingleAdapter.ViewHolder>{
    List<KeyValueEntity> kvlist;
    List<Boolean> isClick = new ArrayList<>();
    Context context;

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        public ViewHolder(View view) {
            super(view);
            txt = view.findViewById(R.id.content_item_text);
        }
    }

    public StrSingleAdapter(Context context, List<KeyValueEntity> kvlist) {
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
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt.setText(kvlist.get(position).getValue());

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
                holder.txt.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                holder.txt.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        }

    }

    public interface OnItemClickListener{
        void setOnClickItemListener(View view,int position, String key);
    }


    @Override
    public int getItemCount() {
        return kvlist.size();
    }
}
