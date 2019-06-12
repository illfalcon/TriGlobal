package com.example.triglobal.ui.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.triglobal.R;
import com.example.triglobal.models.Lead;

import java.util.List;

public class LeadsAdapter extends
        RecyclerView.Adapter<LeadsAdapter.LeadViewHolder> {
    private static final String TAG = LeadsAdapter.class.getSimpleName();

    public interface onItemClickListener { //TODO: think about better or more elegant ways
        void onItemClicked(Lead lead);
    }

    private List<Lead> mLeadList;
    private LayoutInflater mLayoutInflater;
    private onItemClickListener mListener;

    public LeadsAdapter(Context context,
                           List<Lead> leads, onItemClickListener listener) {
        mLayoutInflater = LayoutInflater.from(context);
        mLeadList = leads;
        mListener = listener;
    }

    class LeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final LinearLayout leadItemView;
        final LeadsAdapter mAdapter;

        public LeadViewHolder(@NonNull View itemView, LeadsAdapter adapter) {
            super(itemView);
            leadItemView = itemView.findViewById(R.id.lead_container);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int i = getLayoutPosition();
            Lead lead = mLeadList.get(i);
            mListener.onItemClicked(lead);
        }
    }

    public void updateData(List<Lead> data) {
        Log.d(TAG, "updateData: data updated");
        mLeadList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mLayoutInflater.inflate(R.layout.leads_item, viewGroup, false);
        return new LeadViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadViewHolder leadViewHolder, int i) {
        Lead mCurrent = mLeadList.get(i);
        TextView nameTextView = leadViewHolder.leadItemView.findViewById(R.id.lead_name);
        TextView dateTextView = leadViewHolder.leadItemView.findViewById(R.id.lead_date);
        nameTextView.setText(mCurrent.getFullName());
        dateTextView.setText(mCurrent.getMovingDate());
    }

    @Override
    public int getItemCount() {
        if (mLeadList != null)
            return mLeadList.size();
        return 0;
    }

}
