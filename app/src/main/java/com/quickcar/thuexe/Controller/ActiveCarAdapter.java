package com.quickcar.thuexe.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.UI.ActiveAccountActivity;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;

/**
 * Created by DatNT on 6/28/2016.
 */
public class ActiveCarAdapter extends RecyclerView.Adapter<ActiveCarAdapter.VehicleViewHolder> {

    private ArrayList<CarInforObject> vehicles;
    private Context mContext;
    private onClickListener onClick;
    private DisplayImageOptions options;
    public ActiveCarAdapter(Context mContext, ArrayList<CarInforObject> vehicles) {
        this.vehicles = vehicles;
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.loading)
                .showImageForEmptyUri(R.mipmap.loading)
                .showImageOnFail(R.mipmap.all)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_car_detail, parent, false);
        VehicleViewHolder pvh = new VehicleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, final int position) {
        if (position % 3 == 0)
            holder.layoutCarName.setBackgroundResource(R.mipmap.name_1);
        else if (position % 3 == 1)
            holder.layoutCarName.setBackgroundResource(R.mipmap.name_2);
        else if (position % 3 == 2)
            holder.layoutCarName.setBackgroundResource(R.mipmap.name_3);

        holder.txtCarName.setText(vehicles.get(position).getCarModel());

        holder.txtName.setText(vehicles.get(position).getName());
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_TELEPHONE_PERMISSIONS);
                        return;
                    }
                }
                Log.e("tag", "tag");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + vehicles.get(position).getPhone()));
                mContext.startActivity(intent);
                sendContactToServer(vehicles.get(position).getPhone());
            }
        });
        if (vehicles.get(position).getPrice().equals("-1"))
            holder.txtCarPrice.setText("Giá thỏa thuận");
        else
            holder.txtCarPrice.setText(vehicles.get(position).getPrice()+" vnđ/km");
        holder.txtCarType.setText(vehicles.get(position).getCarType());
        holder.txtCarSize.setText(vehicles.get(position).getCarSize()+ " chỗ");
        holder.txtPhone.setText(vehicles.get(position).getPhone());
        DecimalFormat df = new DecimalFormat("#.#");

        if ((int) vehicles.get(position).getDistance() == 0)
            holder.txtDistance.setText("cách "+df.format(vehicles.get(position).getDistance()*1000) + " m");
        else
            holder.txtDistance.setText("cách "+df.format(vehicles.get(position).getDistance()) + " km");
        ImageLoader.getInstance().displayImage("http://thuexevn.com/"+vehicles.get(position).getImage(), holder.imgIcon, options, new SimpleImageLoadingListener());
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

    public void setOnItemClickListener(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        CardView  cardview;
        TextView  txtName;
        TextView  txtCarPrice;
        TextView  txtCarType;
        ImageView btnCall;
        TextView  txtCarSize;
        TextView  txtCarName;
        TextView  txtPhone;
        TextView  txtDistance;
        FrameLayout layoutCarName;
        ImageView imgIcon;
        VehicleViewHolder(View itemView) {
            super(itemView);
            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtName         = (TextView)        itemView.findViewById(R.id.txt_name);
            txtCarPrice     = (TextView)        itemView.findViewById(R.id.txt_price);
            txtCarType      = (TextView)        itemView.findViewById(R.id.txt_car_type);
            btnCall         = (ImageView)       itemView.findViewById(R.id.img_call);
            txtCarSize      = (TextView)        itemView.findViewById(R.id.txt_car_size);
            txtCarName      = (TextView)        itemView.findViewById(R.id.txt_car_name);
            txtPhone        = (TextView)        itemView.findViewById(R.id.txt_phone);
            txtDistance     = (TextView)        itemView.findViewById(R.id.txt_distance);
            layoutCarName   = (FrameLayout)     itemView.findViewById(R.id.layout_car_name);
            imgIcon         = (ImageView)       itemView.findViewById(R.id.img_icon);
        }

    }
    public interface onClickListener
    {
        public void onItemClick(int position);

    }
}
