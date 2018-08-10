package com.naman14.timber.streaming;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.naman14.timber.R;

import java.util.ArrayList;
import java.util.List;

import static com.naman14.timber.service.Config.URL;

/**
 * Created by Programmer Jalanan on 16/11/17.
 */

public class SitusAdapter extends RecyclerView.Adapter<SitusAdapter.MyViewHolder>
        implements Filterable {

    private Context mContext;
    private List<SitusList> dataList;
    private List<SitusList> situsListFiltered;
    private SitusAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            thumbnail = view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSitusSelected(situsListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public SitusAdapter(Context context, ArrayList<SitusList> dataList, SitusAdapterListener listener) {
        this.mContext = context;
        this.dataList = dataList;
        this.listener = listener;
        this.situsListFiltered = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_streaming_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SitusList data = situsListFiltered.get(position);
        data.getId();
        holder.name.setText(data.Listsitus());
        if (data.getFoto() == null){

        }else {
           Glide.with(mContext).load(data.getFoto()).
                   into(holder.thumbnail);
        }
        //        Glide.with(context)
//                .load(contact.getImage())
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        //kalau data tidak ada di database tidak akan null pointer
        if (situsListFiltered == null){
            return 0;
        }else {
            return situsListFiltered.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    situsListFiltered = dataList;
                } else {
                    List<SitusList> filteredList = new ArrayList<>();
                    for (SitusList row : dataList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.Listsitus().toLowerCase().contains(charString.toLowerCase()) || row.Listsitus().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    situsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = situsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                situsListFiltered = (ArrayList<SitusList>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface SitusAdapterListener {
        void onSitusSelected(SitusList barang);
    }
}
