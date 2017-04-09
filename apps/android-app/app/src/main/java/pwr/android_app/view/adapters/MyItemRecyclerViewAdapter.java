package pwr.android_app.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import pwr.android_app.R;
import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.view.fragments.MonitorFragment.OnListFragmentInteractionListener;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<ServiceData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<ServiceData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.idView.setText(String.valueOf(mValues.get(position).getId()));
        holder.ipNumber.setText(mValues.get(position).getAddress());
        holder.siteName.setText(mValues.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView idView;
        public final TextView ipNumber;
        public final TextView siteName;
        public final ImageView stateImage;

        public ServiceData mItem;

        public ViewHolder(View view) {
            super(view);
            idView = (TextView) view.findViewById(R.id.id);
            ipNumber = (TextView) view.findViewById(R.id.ip);
            siteName = (TextView) view.findViewById(R.id.site);
            stateImage = (ImageView) view.findViewById(R.id.icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ipNumber.getText() + "'";
        }
    }

    public void addService(ServiceData item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void removeService(int id) {
        mValues.remove(id);
        notifyDataSetChanged();
    }

    public void changeState(int id, int newState) {
        mValues.get(id).setCurrent_state(newState);
        notifyItemChanged(id);
    };
}
