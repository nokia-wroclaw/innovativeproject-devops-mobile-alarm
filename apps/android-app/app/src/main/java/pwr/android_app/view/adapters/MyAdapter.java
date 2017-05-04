package pwr.android_app.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import pwr.android_app.R;
import pwr.android_app.dataStructures.Service;
import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.dataStructures.SubscriptionData;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /* ========================================== DATA ========================================== */

    // ToDo: map
    private List<Service> mValues;

    private final int SERVICE_UNKNOWN = 0;
    private final int SERVICE_DOWN = 1;
    private final int SERVICE_UP = 2;
    private final int SERVICE_UNSPECIFIED = 3;
    private final int SERVICE_NOT_SUBSCRIBED = 4;

    /* ====================================== CONSTRUCTORS ====================================== */

    public MyAdapter(List<Service> items) {
        mValues = items;
    }

    /* ========================================= GETTERS ======================================== */

    public List<Service> getList() {
        return mValues;
    }

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {

            case SERVICE_UP:
                onBindServiceUp((ViewHolderServiceUp) holder, position);
                break;

            case SERVICE_DOWN:
                onBindServiceDown((ViewHolderServiceDown) holder, position);
                break;

            case SERVICE_UNSPECIFIED:
                onBindServiceUnspecified((ViewHolderServiceUnspecified) holder, position);
                break;

            case SERVICE_NOT_SUBSCRIBED:
                onBindServiceNotSubscribed((ViewHolderServiceNotSubscribed) holder, position);
                break;

            case SERVICE_UNKNOWN:
                onBindServiceUnknown((ViewHolderServiceUnknown) holder, position);
                break;

            default:
                onBindServiceUnknown((ViewHolderServiceUnknown) holder, position);
                break;
        }
    }

    private void onBindServiceUp(final ViewHolderServiceUp holder, final int position) {

        holder.serviceNameLabel.setText(mValues.get(position).getServiceName());
        holder.serviceAddressLabel.setText((mValues.get(position).getServiceAddress()));

        holder.stopSubscribingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: write code here
            }
        });
    }
    private void onBindServiceDown(final ViewHolderServiceDown holder, final int position) {

        holder.serviceNameLabel.setText(mValues.get(position).getServiceName());
        holder.serviceAddressLabel.setText((mValues.get(position).getServiceAddress()));

        holder.stopSubscribingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: write code here
            }
        });
        holder.repairServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: write code here
            }
        });
    }
    private void onBindServiceUnspecified(final ViewHolderServiceUnspecified holder, final int position) {

        holder.serviceNameLabel.setText(mValues.get(position).getServiceName());
        holder.serviceAddressLabel.setText((mValues.get(position).getServiceAddress()));

        holder.stopSubscribingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: write code here
            }
        });
    }
    private void onBindServiceNotSubscribed(final ViewHolderServiceNotSubscribed holder, final int position) {

        holder.serviceNameLabel.setText(mValues.get(position).getServiceName());
        holder.serviceAddressLabel.setText((mValues.get(position).getServiceAddress()));

        holder.startSubscribingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: write code here
            }
        });
    }
    private void onBindServiceUnknown(final ViewHolderServiceUnknown holder, final int position) {

        holder.serviceNameLabel.setText(mValues.get(position).getServiceName());
        holder.serviceAddressLabel.setText((mValues.get(position).getServiceAddress()));
    }

    /* ========================================= CLASSES ======================================== */

    // ---------------------------------- View Holder Lifecycle --------------------------------- //
    // .......................................... STATIC ........................................ //
    static class ViewHolderServiceUp extends RecyclerView.ViewHolder {

        // --- DATA --- //
        @BindView(R.id.service_name_label)
        TextView serviceNameLabel;
        @BindView(R.id.service_address_label)
        TextView serviceAddressLabel;
        @BindView(R.id.stop_subscribing_button)
        ImageButton stopSubscribingButton;

        // --- CONSTRUCTORS --- //
        private ViewHolderServiceUp (View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
    static class ViewHolderServiceDown extends RecyclerView.ViewHolder {

        // --- DATA --- //
        @BindView(R.id.service_name_label)
        TextView serviceNameLabel;
        @BindView(R.id.service_address_label)
        TextView serviceAddressLabel;
        @BindView(R.id.stop_subscribing_button)
        ImageButton stopSubscribingButton;
        @BindView(R.id.repair_button)
        ImageButton repairServiceButton;

        // --- CONSTRUCTORS --- //
        private ViewHolderServiceDown (View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
    static class ViewHolderServiceUnspecified extends RecyclerView.ViewHolder {

        // --- DATA --- //
        @BindView(R.id.service_name_label)
        TextView serviceNameLabel;
        @BindView(R.id.service_address_label)
        TextView serviceAddressLabel;
        @BindView(R.id.stop_subscribing_button)
        ImageButton stopSubscribingButton;

        // --- CONSTRUCTORS --- //
        private ViewHolderServiceUnspecified (View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
    static class ViewHolderServiceNotSubscribed extends RecyclerView.ViewHolder {

        // --- DATA --- //
        @BindView(R.id.service_name_label)
        TextView serviceNameLabel;
        @BindView(R.id.service_address_label)
        TextView serviceAddressLabel;
        @BindView(R.id.start_subscribing_button)
        ImageButton startSubscribingButton;

        // --- CONSTRUCTORS --- //
        private ViewHolderServiceNotSubscribed(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
    static class ViewHolderServiceUnknown extends RecyclerView.ViewHolder {

        // --- DATA --- //
        @BindView(R.id.service_name_label)
        TextView serviceNameLabel;
        @BindView(R.id.service_address_label)
        TextView serviceAddressLabel;

        // --- CONSTRUCTORS --- //
        private ViewHolderServiceUnknown (View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    /* ========================================================================================== */
}