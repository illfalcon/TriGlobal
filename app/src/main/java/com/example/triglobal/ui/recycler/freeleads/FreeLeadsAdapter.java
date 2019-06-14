package com.example.triglobal.ui.recycler.freeleads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.triglobal.R;
import com.example.triglobal.models.FreeLead;

import java.util.List;

public class FreeLeadsAdapter extends
        RecyclerView.Adapter<FreeLeadsAdapter.FreeLeadViewHolder> {

    public static final String TAG = FreeLeadsAdapter.class.getSimpleName();

    private OnFreeLeadClickListener mListener;
    private List<FreeLead> mFreeLeadList;
    private LayoutInflater mLayoutInflater;

    public FreeLeadsAdapter(Context context,
                        List<FreeLead> freeLeads, OnFreeLeadClickListener listener) {
        mLayoutInflater = LayoutInflater.from(context);
        mFreeLeadList = freeLeads;
        mListener = listener;
    }

    @NonNull
    @Override
    public FreeLeadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.free_leads_item, viewGroup, false);
        return new FreeLeadViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FreeLeadViewHolder freeLeadViewHolder, int i) {
        FreeLead curLead = mFreeLeadList.get(i);
        freeLeadViewHolder.mFreeLeadName.setText(curLead.getFullName());
        freeLeadViewHolder.mFreeLeadDate.setText(curLead.getMovingDate());
        freeLeadViewHolder.mAmountMatched.setText(curLead.getAmountMatched());
        freeLeadViewHolder.mCost.setText(String.valueOf(curLead.getCost()));
        freeLeadViewHolder.mTimeLeft.setText(curLead.getTimeLeft());
    }

    @Override
    public int getItemCount() {
        if (mFreeLeadList != null)
            return mFreeLeadList.size();
        return 0;
    }

    public void updateData(List<FreeLead> freeLeads) {
        mFreeLeadList = freeLeads;
        notifyDataSetChanged();
    }

    public interface OnFreeLeadClickListener {
        void onFreeLeadClicked(FreeLead freeLead);
    }

    class FreeLeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout mVisiblePart;
        public LinearLayout mInvisiblePart;
        public TextView mFreeLeadName;
        public TextView mFreeLeadDate;
        public TextView mAmountMatched;
        public TextView mCost;
        public TextView mTimeLeft;
        public ImageButton mDownArrow;
        final FreeLeadsAdapter mAdapter;
        public View.OnClickListener onClickTransfomer;

        public FreeLeadViewHolder(@NonNull View itemView, FreeLeadsAdapter adapter) {
            super(itemView);
            onClickTransfomer = v -> {
                toggleVisibility();
            };
            mFreeLeadName = itemView.findViewById(R.id.freelead_name);
            mFreeLeadDate = itemView.findViewById(R.id.freelead_date);
            mDownArrow = itemView.findViewById(R.id.freelead_downarrow);
            mAmountMatched = itemView.findViewById(R.id.freelead_matched);
            mCost = itemView.findViewById(R.id.freelead_cost);
            mTimeLeft = itemView.findViewById(R.id.freelead_time_left);
            mDownArrow.setOnClickListener(onClickTransfomer);
            mVisiblePart = itemView.findViewById(R.id.freelead_visible_layout);
            mInvisiblePart = itemView.findViewById(R.id.freelead_hidden_layout);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
        }

        public void toggleVisibility() {
            if (mInvisiblePart.getVisibility() == View.GONE)
                mInvisiblePart.setVisibility(View.VISIBLE);
            else
                mInvisiblePart.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            int i = getLayoutPosition();
            Log.d(TAG, "onClick: i = " + i);
            FreeLead freeLead = mFreeLeadList.get(i);
            mListener.onFreeLeadClicked(freeLead);
        }
    }
}
