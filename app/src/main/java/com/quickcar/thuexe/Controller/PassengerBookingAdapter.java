package com.quickcar.thuexe.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by DatNT on 6/28/2016.
 */
public class PassengerBookingAdapter extends RecyclerView.Adapter<PassengerBookingAdapter.VehicleViewHolder> {

    private ArrayList<BookingObject> vehicles;
    private Context mContext;
    public PassengerBookingAdapter(Context mContext, ArrayList<BookingObject> vehicles){
        this.vehicles = vehicles;
        this.mContext = mContext;
    }
    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_passenger_detail, parent, false);
        VehicleViewHolder pvh = new VehicleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, final int position) {
        holder.txtName.setText(vehicles.get(position).getName());
        holder.txtFrom.setText(vehicles.get(position).getCarFrom());
        holder.txtTo.setText(vehicles.get(position).getCarTo());
        holder.txtDateFrom.setText(Utilites.convertTime(new DateTime(vehicles.get(position).getDateFrom())));
        holder.txtDateTo.setText(Utilites.convertTime(new DateTime(vehicles.get(position).getDateTo())));
        holder.txtCarType.setText(vehicles.get(position).getCarType()+" - "+vehicles.get(position).getCarHireType());
        if (vehicles.get(position).getCarSize().equals("null"))
            holder.txtCarSize.setText(vehicles.get(position).getCarSize());
        else
            holder.txtCarSize.setText(vehicles.get(position).getCarSize()+" chỗ");
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + vehicles.get(position).getPhone()));
                if (Build.VERSION.SDK_INT >= 22) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                        return;
                    }
                }
                mContext.startActivity(intent);
                sendLogToServer(vehicles.get(position).getPhone());
                deleteHire(vehicles.get(position).getId());
            }
        });

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
    private void sendLogToServer(String toPhone) {
        SharePreference preference = new SharePreference(mContext);
        RequestParams params;
        params = new RequestParams();
        params.put("from_number", preference.getPhone() );
        params.put("to_number", toPhone);
        Log.i("params deleteDelivery", params.toString());
        //swipeToRefresh.setRefreshing(true);

        BaseService.getHttpClient().post(Defines.URL_LOG_DRIVER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));

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

        ImageView btnCall;
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

            btnCall             = (ImageView)       itemView.findViewById(R.id.btn_book);
        }

    }
}
