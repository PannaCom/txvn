package com.quickcar.thuexe.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Models.BookingObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

import org.joda.time.DateTime;

import java.util.ArrayList;


/**
 * Created by DatNT on 6/28/2016.
 */
public class PassengerHireAdapter extends RecyclerView.Adapter<PassengerHireAdapter.VehicleViewHolder> {

    private ArrayList<BookingObject> vehicles;
    private Context mContext;
    private onClickListener onClick;
    public PassengerHireAdapter(Context mContext, ArrayList<BookingObject> vehicles){
        this.vehicles = vehicles;
        this.mContext = mContext;
    }
    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hire_passenger_detail, parent, false);
        VehicleViewHolder pvh = new VehicleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, final int position) {
        holder.txtName.setText("# "+vehicles.get(position).getId());
        holder.txtFrom.setText(vehicles.get(position).getCarFrom());
        holder.txtTo.setText(vehicles.get(position).getCarTo());
        holder.txtDateFrom.setText(Utilites.convertTime(new DateTime(vehicles.get(position).getDateFrom())));
        holder.txtDateTo.setText(Utilites.convertTime(new DateTime(vehicles.get(position).getDateTo())));
        holder.txtCarType.setText(vehicles.get(position).getCarType()+" - "+vehicles.get(position).getCarHireType());
        if (vehicles.get(position).getCarSize().equals("null"))
            holder.txtCarSize.setText(vehicles.get(position).getCarSize());
        else
            holder.txtCarSize.setText(vehicles.get(position).getCarSize()+" chỗ");
        if (vehicles.get(position).getStatus().equals("1")) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.imgComplete.setVisibility(View.VISIBLE);
            holder.layoutTitle.setBackgroundColor(Color.parseColor("#ff6e6e"));
        }
        else {
            holder.imgComplete.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.layoutTitle.setBackgroundColor(Color.parseColor("#20afc3"));

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteHire(vehicles.get(position).getId());
                }
            });
            holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteHire(vehicles.get(position).getId());
                }
            });
        }

    }

    private void deleteHire(int id) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", id);
        Log.i("params deleteDelivery", params.toString());
        //swipeToRefresh.setRefreshing(true);

        BaseService.getHttpClient().post(Defines.URL_UPDATE_BOOKING, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int result = Integer.valueOf( new String(responseBody));
                if (result > 0 ) {
                    if (onClick != null)
                        onClick.onItemClick();
                    Toast.makeText(mContext, "Cập nhật chuyến xe thành công", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(mContext, "Cập nhật chuyến xe thất bại",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        CardView cardview;
        TextView txtName;
        TextView txtFrom;
        TextView txtTo;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtCarType;
        TextView txtCarSize;
        LinearLayout layoutTitle;
        Button btnDelete;
        Button btnConfirm;
        ImageView imgComplete;
        VehicleViewHolder(View itemView) {
            super(itemView);
            cardview            = (CardView)        itemView.findViewById(R.id.card_view);
            txtName             = (TextView)        itemView.findViewById(R.id.txt_name);
            txtFrom             = (TextView)        itemView.findViewById(R.id.txt_start_place);
            txtTo               = (TextView)        itemView.findViewById(R.id.txt_end_place);
            txtDateFrom         = (TextView)        itemView.findViewById(R.id.txt_time_start);
            txtDateTo           = (TextView)        itemView.findViewById(R.id.txt_time_end);
            txtCarType          = (TextView)        itemView.findViewById(R.id.txt_vehicle_type);
            txtCarSize          = (TextView)        itemView.findViewById(R.id.txt_vehicle_size);
            layoutTitle         = (LinearLayout)    itemView.findViewById(R.id.layout_title);
            btnDelete           = (Button)          itemView.findViewById(R.id.btn_delete);
            btnConfirm          = (Button)          itemView.findViewById(R.id.btn_confirm);
            imgComplete         = (ImageView)       itemView.findViewById(R.id.img_complete);
        }

    }
    public void setOnItemClickListener(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    public interface onClickListener
    {
        public void onItemClick();

    }
}
