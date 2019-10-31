package lads.dev.viewadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lads.dev.R;


/**
 * Created by Administrator on 2019-07-27
 */
public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.ViewHolder> {

    List<String> warninglist;
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView warnText;
        public ViewHolder(View view) {
            super(view);
            warnText = view.findViewById(R.id.content_item_text);
        }
    }

    public WarningAdapter(List<String> warninglist) {
        this.warninglist = warninglist;
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
                String str = warninglist.get(idx);
                Toast.makeText(view.getContext(), str, Toast.LENGTH_SHORT).show();
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.warnText.setText(warninglist.get(position));
    }

    @Override
    public int getItemCount() {
        return warninglist.size();
    }

}
