package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.R;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import java.util.ArrayList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder> {


    private ArrayList<NotificationBody> notificationBodyArrayList;

    private ButtonActionClickListener buttonActionClickListener;

    public NotificationListAdapter(ButtonActionClickListener buttonActionClickListener) {
        this.buttonActionClickListener = buttonActionClickListener;
        notificationBodyArrayList = new ArrayList<>();

    }

    @NonNull
    @Override
    public NotificationListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_card_view, viewGroup, false);

        return new NotificationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListViewHolder holder, int position) {

        holder.notificationId.setText(String.valueOf(notificationBodyArrayList.get(position).getNotificationId()));
        holder.date.setText(notificationBodyArrayList.get(position).getDate());
        holder.duration.setText(String.valueOf(notificationBodyArrayList.get(position).getDuration()));
    }

    @Override
    public int getItemCount() {
        return notificationBodyArrayList.size();
    }

    public class NotificationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView notificationId;
        private TextView date;
        private TextView duration;
        private ImageButton deleteNotification;

        public NotificationListViewHolder(View itemView) {
            super(itemView);

            notificationId = itemView.findViewById(R.id.tv_notification_id);
            date = itemView.findViewById(R.id.tv_date);
            duration = itemView.findViewById(R.id.tv_duration);
            deleteNotification = itemView.findViewById(R.id.bt_delete);
            deleteNotification.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            switch (v.getId()) {
                case (R.id.bt_delete):
                    buttonActionClickListener.OnDeleteChannelClick(notificationBodyArrayList.get(index).getNotificationId());
                    remove(index);
                    break;


            }
        }
    }


    public void add(NotificationBody notificationBody) {
        notificationBodyArrayList.add(notificationBody);
        notifyItemInserted(notificationBodyArrayList.size() - 1);
    }

    public void addAll(NotificationBody[] notificationBodies) {
        for (NotificationBody notificationBody : notificationBodies) {
            add(notificationBody);
        }

    }

    public void remove(int index) {
        notificationBodyArrayList.remove(index);
        notifyItemRemoved(index);
    }


    public interface ButtonActionClickListener {

        void OnDeleteChannelClick(int notificationId);
    }

}
