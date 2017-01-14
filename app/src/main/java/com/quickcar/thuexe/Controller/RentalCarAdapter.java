package com.quickcar.thuexe.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.Models.RentalInfo;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.Utilites;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by DatNT on 6/28/2016.
 */
public class RentalCarAdapter extends RecyclerView.Adapter<RentalCarAdapter.VehicleViewHolder> {

    private ArrayList<RentalInfo> mVehicle;
    private Context mContext;
    private onClickListener onClick;
    public RentalCarAdapter(Context mContext, ArrayList<RentalInfo> vehicles) {
        this.mVehicle = vehicles;
        this.mContext = mContext;
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rental_car_detail, parent, false);
        VehicleViewHolder pvh = new VehicleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, final int position) {
        holder.txtCarFrom.setText(mVehicle.get(position).getCarFrom());
        holder.txtCarTo.setText(mVehicle.get(position).getCarTo());
        holder.txtHireType.setText(mVehicle.get(position).getCarHireType());
        holder.txtCarType.setText(mVehicle.get(position).getCarType());
        DateTime jDateFrom = new DateTime(mVehicle.get(position).getDateFrom());

        holder.txtDateFrom.setText(Utilites.convertTimeShort(jDateFrom));
        DateTime jDateTo = new DateTime(mVehicle.get(position).getDateTo());
        holder.txtDateTo.setText(Utilites.convertTimeShort(jDateTo));
        holder.txtName.setText(mVehicle.get(position).getName());
        //   holder.txtTimeReduce.setText(mVehicle.get(position).getTimeToReduce());
        if (mVehicle.get(position).getCarSize() == 4)
            holder.txtCarSize.setText(mVehicle.get(position).getCarSize()+" chỗ(giá siêu rẻ, không cốp)");
        else if (mVehicle.get(position).getCarSize() == 5)
            holder.txtCarSize.setText(mVehicle.get(position).getCarSize()+" chỗ(có cốp)");
        else
            holder.txtCarSize.setText(mVehicle.get(position).getCarSize()+" chỗ");
        holder.btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick != null)
                    onClick.onItemClick(position);
            }
        });
    }
    public void setOnCallListener(onClickListener onClick){
        this.onClick = onClick;
    }
    private void sendContactToServer(String phone) {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", phone);

        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_CALL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVehicle.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        CardView cardview;
        TextView txtCarFrom;
        TextView txtCarTo;
        TextView txtHireType;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtCarType;
        TextView txtCarSize;
        TextView txtName;
        LinearLayout btnBooking;

        public VehicleViewHolder(View itemView) {
            super(itemView);


            cardview = (CardView) itemView.findViewById(R.id.card_view);
            txtCarFrom = (TextView) itemView.findViewById(R.id.txt_from);
            txtCarTo = (TextView) itemView.findViewById(R.id.txt_to);
            txtHireType = (TextView) itemView.findViewById(R.id.txt_hire_type);
            txtDateFrom = (TextView) itemView.findViewById(R.id.txt_date_from);
            txtDateTo = (TextView) itemView.findViewById(R.id.txt_date_to);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtCarType = (TextView) itemView.findViewById(R.id.txt_car_type);
            txtCarSize = (TextView) itemView.findViewById(R.id.txt_car_size);
            btnBooking = (LinearLayout) itemView.findViewById(R.id.btn_booking);


        }
    }
    public interface onClickListener
    {
        public void onItemClick(int position);

    }
}
