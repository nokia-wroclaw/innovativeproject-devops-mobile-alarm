package pwr.android_app.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import pwr.android_app.R;
import pwr.android_app.dataStructures.Service;
import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.dataStructures.SubscriptionData;
import pwr.android_app.view.fragments.MonitorFragment.OnListFragmentInteractionListener;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /* ========================================== DATA ========================================== */

    // ToDo: map
    private List<Service> mValues;
    private OnListFragmentInteractionListener mListener;

    private final int SERVICE_UNKNOWN = 0;
    private final int SERVICE_DOWN = 1;
    private final int SERVICE_UP = 2;
    private final int SERVICE_UNSPECIFIED = 3;
    private final int SERVICE_NOT_SUBSCRIBED = 4;

    /* ====================================== CONSTRUCTORS ====================================== */

    public MyAdapter(List<Service> items,
                     OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /* ========================================= GETTERS ======================================== */

    public List<Service> getList() { return mValues; }

    /* ========================================= SETTERS ======================================== */

    public void setNewServicesDataList(List<ServiceData> newList) {

        this.mValues.clear();

        for (ServiceData serviceData : newList) {

            mValues.add(new Service( serviceData ));
        }
    }

    public void addInformationAboutSubscriptions(List<SubscriptionData> subscriptionList) {

        // ToDo: To jest bardzo nieefektywne, należy poprawić ten kod! ( tablice haszujące?? )

        for (Service service : mValues) {

            service.removeSubscription();

            for (SubscriptionData subscriptionData : subscriptionList) {

                if (service.getServiceId() == subscriptionData.getServiceId()) {

                    service.addSubscription(subscriptionData);
                }
            }
        }
    }

    /* ========================================= METHODS ======================================== */

    // ---------------------------------- View Holder Lifecycle --------------------------------- //
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (mValues.get(position).getSubscriptionStatus()) {

            case SUBSCRIPTION_UP:

                switch (mValues.get(position).getServiceStatus()) {

                    case UP:
                        return SERVICE_UP;

                    case DOWN:
                        return SERVICE_DOWN;

                    case UNSPECIFIED:
                        return SERVICE_UNSPECIFIED;

                    case UNKNOWN:
                        return SERVICE_UNKNOWN;

                    default:
                        return SERVICE_UNKNOWN;
                }

            case SUBSCRIPTION_DOWN:
                return SERVICE_NOT_SUBSCRIBED;

            default:
                return SERVICE_UNKNOWN;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {

            case SERVICE_UP:
                view = inflater.inflate(R.layout.fragment_item_service_up, parent, false);
                viewHolder = new ViewHolderServiceUp(view);
                break;

            case SERVICE_DOWN:
                view = inflater.inflate(R.layout.fragment_item_service_down, parent, false);
                viewHolder = new ViewHolderServiceDown(view);
                break;

            case SERVICE_UNSPECIFIED:
                view = inflater.inflate(R.layout.fragment_item_service_unspecified, parent, false);
                viewHolder = new ViewHolderServiceUnspecified(view);
                break;

            case SERVICE_NOT_SUBSCRIBED:
                view = inflater.inflate(R.layout.fragment_item_service_not_subscribed, parent, false);
                viewHolder = new ViewHolderServiceNotSubscribed(view);
                break;

            case SERVICE_UNKNOWN:
                view = inflater.inflate(R.layout.fragment_item_service_unknown, parent, false);
                viewHolder = new ViewHolderServiceUnknown(view);
                break;

            default:
                view = inflater.inflate(R.layout.fragment_item_service_unknown, parent, false);
                viewHolder = new ViewHolderServiceUnknown(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case SERVICE_UP:
                ViewHolderServiceUp holderServiceUp = (ViewHolderServiceUp) holder;
                onBindServiceUpFragment(holderServiceUp, position);
                break;

            case SERVICE_DOWN:
                ViewHolderServiceDown holderServiceDown = (ViewHolderServiceDown) holder;
                onBindServiceDownFragment(holderServiceDown, position);
                break;

            case SERVICE_UNSPECIFIED:
                ViewHolderServiceUnspecified holderServiceUnspecified = (ViewHolderServiceUnspecified) holder;
                onBindServiceUnspecifiedFragment(holderServiceUnspecified, position);
                break;

            case SERVICE_NOT_SUBSCRIBED:
                ViewHolderServiceNotSubscribed holderServiceNotSubscribed = (ViewHolderServiceNotSubscribed) holder;
                onBindServiceNotSubscribedFragment(holderServiceNotSubscribed, position);
                break;

            case SERVICE_UNKNOWN:
                ViewHolderServiceUnknown holderServiceUnknown = (ViewHolderServiceUnknown) holder;
                onBindServiceUnknownFragment(holderServiceUnknown, position);
                break;

            default:
                ViewHolderServiceUnknown holderDefault = (ViewHolderServiceUnknown) holder;
                onBindServiceUnknownFragment(holderDefault, position);
                break;
        }
    }

    private void onBindServiceUpFragment(final ViewHolderServiceUp holder, int position) {

        holder.getServiceNameLabel().setText(mValues.get(position).getServiceName());
        holder.getServiceAddressLabel().setText((mValues.get(position).getServiceAddress()));

        // ToDo: listener w każdej klasie

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }
    private void onBindServiceDownFragment(final ViewHolderServiceDown holder, int position) {

        holder.getServiceNameLabel().setText(mValues.get(position).getServiceName());
        holder.getServiceAddressLabel().setText((mValues.get(position).getServiceAddress()));
    }
    private void onBindServiceUnspecifiedFragment(final ViewHolderServiceUnspecified holder, int position) {

        holder.getServiceNameLabel().setText(mValues.get(position).getServiceName());
        holder.getServiceAddressLabel().setText((mValues.get(position).getServiceAddress()));
    }
    private void onBindServiceNotSubscribedFragment(final ViewHolderServiceNotSubscribed holder, int position) {

        holder.getServiceNameLabel().setText(mValues.get(position).getServiceName());
        holder.getServiceAddressLabel().setText((mValues.get(position).getServiceAddress()));
    }
    private void onBindServiceUnknownFragment(final ViewHolderServiceUnknown holder, int position) {

        holder.getServiceNameLabel().setText(mValues.get(position).getServiceName());
        holder.getServiceAddressLabel().setText((mValues.get(position).getServiceAddress()));
    }

    /* ========================================= CLASSES ======================================== */

    // ---------------------------------- View Holder Lifecycle --------------------------------- //
    private class ViewHolderServiceUp extends RecyclerView.ViewHolder {

        // --- DATA --- //
        public final TextView serviceNameLabel;
        public final TextView serviceAddressLabel;

        // --- CONSTRUCTORS --- //
        public ViewHolderServiceUp (View view) {
            super(view);

            serviceNameLabel = (TextView) view.findViewById(R.id.service_name_label);
            serviceAddressLabel = (TextView) view.findViewById(R.id.service_address_label);
        }

        // --- GETTERS --- //
        public TextView getServiceNameLabel() {
            return serviceNameLabel;
        }

        public TextView getServiceAddressLabel() {
            return serviceAddressLabel;
        }
    }
    private class ViewHolderServiceDown extends RecyclerView.ViewHolder {

        // --- DATA --- //
        private final TextView serviceNameLabel;
        private final TextView serviceAddressLabel;

        // --- CONSTRUCTORS --- //
        public ViewHolderServiceDown (View view) {
            super(view);

            serviceNameLabel = (TextView) view.findViewById(R.id.service_name_label);
            serviceAddressLabel = (TextView) view.findViewById(R.id.service_address_label);
        }

        // --- GETTERS --- //
        public TextView getServiceNameLabel() {
            return serviceNameLabel;
        }

        public TextView getServiceAddressLabel() {
            return serviceAddressLabel;
        }
    }
    private class ViewHolderServiceUnspecified extends RecyclerView.ViewHolder {

        // --- DATA --- //
        public final TextView serviceNameLabel;
        public final TextView serviceAddressLabel;

        // --- CONSTRUCTORS --- //
        public ViewHolderServiceUnspecified (View view) {
            super(view);

            serviceNameLabel = (TextView) view.findViewById(R.id.service_name_label);
            serviceAddressLabel = (TextView) view.findViewById(R.id.service_address_label);
        }

        // --- GETTERS --- //
        public TextView getServiceNameLabel() {
            return serviceNameLabel;
        }

        public TextView getServiceAddressLabel() {
            return serviceAddressLabel;
        }
    }
    private class ViewHolderServiceNotSubscribed extends RecyclerView.ViewHolder {

        // --- DATA --- //
        public final TextView serviceNameLabel;
        public final TextView serviceAddressLabel;

        // --- CONSTRUCTORS --- //
        public ViewHolderServiceNotSubscribed(View view) {
            super(view);

            serviceNameLabel = (TextView) view.findViewById(R.id.service_name_label);
            serviceAddressLabel = (TextView) view.findViewById(R.id.service_address_label);
        }

        // --- GETTERS --- //
        public TextView getServiceNameLabel() {
            return serviceNameLabel;
        }

        public TextView getServiceAddressLabel() {
            return serviceAddressLabel;
        }
    }
    private class ViewHolderServiceUnknown extends RecyclerView.ViewHolder {

        // --- DATA --- //
        public final TextView serviceNameLabel;
        public final TextView serviceAddressLabel;


        // --- CONSTRUCTORS --- //
        public ViewHolderServiceUnknown (View view) {
            super(view);

            serviceNameLabel = (TextView) view.findViewById(R.id.service_name_label);
            serviceAddressLabel = (TextView) view.findViewById(R.id.service_address_label);
        }

        // --- GETTERS --- //
        public TextView getServiceNameLabel() {
            return serviceNameLabel;
        }

        public TextView getServiceAddressLabel() {
            return serviceAddressLabel;
        }
    }

    /* ========================================================================================== */
}