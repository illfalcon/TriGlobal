package com.example.triglobal.ui.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.triglobal.R;
import com.example.triglobal.models.FreeLead;

import java.util.List;

public class FreeLeadsAdapter extends
        RecyclerView.Adapter<FreeLeadsAdapter.FreeLeadViewHolder> {

    public static final String TAG = FreeLeadsAdapter.class.getSimpleName();

    private OnFreeLeadClickListener onFreeLeadClickListener;
    private OnFreeLeadPurchase onFreeLeadPurchase;
    private List<FreeLead> mFreeLeadList;
    private LayoutInflater mLayoutInflater;

    public FreeLeadsAdapter(Context context,
                        List<FreeLead> freeLeads, OnFreeLeadClickListener onFreeLeadClickListener, OnFreeLeadPurchase purchaseListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mFreeLeadList = freeLeads;
        this.onFreeLeadClickListener = onFreeLeadClickListener;
        this.onFreeLeadPurchase = purchaseListener;
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
        freeLeadViewHolder.mMovingDate.setText(curLead.getMovingDate());
        freeLeadViewHolder.mMovingSize.setText(
                curLead.getVolumeMeters() != 0 ? curLead.getVolumeMeters() + " m3" :
                "Unknown"
        );
        freeLeadViewHolder.mFreeLeadFrom.setText(curLead.getCityFrom());
        freeLeadViewHolder.mFreeLeadTo.setText(curLead.getCityTo());
        freeLeadViewHolder.mTimeLeft.setText(curLead.getTimeLeft());
        freeLeadViewHolder.mCost.setText(String.valueOf(curLead.getCost()));
        freeLeadViewHolder.mInvisiblePart.setVisibility(curLead.isVisible() ? View.VISIBLE : View.GONE);
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

    public interface OnFreeLeadPurchase {
        void onFreeLeadBuyClicked(FreeLead freeLead);
    }

    class FreeLeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout mVisiblePart;
        public LinearLayout mInvisiblePart;
        public TextView mFreeLeadFrom;
        public TextView mFreeLeadTo;
        public TextView mMovingSize;
        public TextView mMovingDate;
        public TextView mCost;
        public TextView mTimeLeft;
        public ImageButton mDownArrow;
        final FreeLeadsAdapter mAdapter;
        public View.OnClickListener onClickTransformer;
        public View.OnClickListener onBuyListener;
        public Button mBuyFreeLead;

        public FreeLeadViewHolder(@NonNull View itemView, FreeLeadsAdapter adapter) {
            super(itemView);
            onClickTransformer = v -> {
                int pos = getLayoutPosition();
                toggleVisibility(pos);
            };
            onBuyListener = v -> {
                onFreeLeadPurchase.onFreeLeadBuyClicked(mFreeLeadList.get(getLayoutPosition()));
            };
            mFreeLeadFrom = itemView.findViewById(R.id.freelead_from);
            mFreeLeadTo = itemView.findViewById(R.id.freelead_to);
            mDownArrow = itemView.findViewById(R.id.freelead_downarrow);
            mMovingSize = itemView.findViewById(R.id.freelead_moving_size);
            mMovingDate = itemView.findViewById(R.id.freelead_moving_date);
            mCost = itemView.findViewById(R.id.freelead_cost);
            mTimeLeft = itemView.findViewById(R.id.freelead_time_left);
            mDownArrow.setOnClickListener(onClickTransformer);
            mVisiblePart = itemView.findViewById(R.id.freelead_visible_layout);
            mInvisiblePart = itemView.findViewById(R.id.freelead_hidden_layout);
            mBuyFreeLead = itemView.findViewById(R.id.freelead_buy);
            mBuyFreeLead.setOnClickListener(onBuyListener);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
        }

        public void toggleVisibility(int viewPosition) {
            FreeLead freeLead = mFreeLeadList.get(viewPosition);
            if (freeLead.isVisible()) {
                freeLead.setVisible(false);
                mInvisiblePart.setVisibility(View.GONE);
            } else {
                freeLead.setVisible(true);
                mInvisiblePart.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            int i = getLayoutPosition();
            Log.d(TAG, "onClick: i = " + i);
            FreeLead freeLead = mFreeLeadList.get(i);
            onFreeLeadClickListener.onFreeLeadClicked(freeLead);
        }
    }
}
