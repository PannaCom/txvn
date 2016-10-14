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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.Defines;

/**
 * Created by DatNT on 6/28/2016.
 */
public class ActiveCarAdapter extends RecyclerView.Adapter<ActiveCarAdapter.VehicleViewHolder> {

    private ArrayList<CarInforObject> vehicles;
    private Context mContext;
    private onClickListener onClick;

    public ActiveCarAdapter(Context mContext, ArrayList<CarInforObject> vehicles) {
        this.vehicles = vehicles;
        this.mContext = mContext;
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
                Log.e("tag", "tag");
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + vehicles.get(position).getPhone()));
                if (Build.VERSION.SDK_INT >= 22) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        return;
                    }
                }
                mContext.startActivity(intent);
            }
        });

        holder.txtCarMade.setText(vehicles.get(position).getCarMade());
        holder.txtCarType.setText(vehicles.get(position).getCarType());
        holder.txtCarSize.setText(vehicles.get(position).getCarSize()+ " chỗ");
        holder.txtPhone.setText(vehicles.get(position).getPhone());
        holder.txtDistance.setText(vehicles.get(position).getDistance()+ "km");
        DecimalFormat df = new DecimalFormat("#.#");
        if ((int) vehicles.get(position).getDistance() == 0)
            holder.txtDistance.setText(df.format(vehicles.get(position).getDistance()*1000) + " m");
        else
            holder.txtDistance.setText(df.format(vehicles.get(position).getDistance()) + " km");
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
        TextView  txtCarMade;
        TextView  txtCarType;
        ImageView btnCall;
        TextView  txtCarSize;
        TextView  txtCarName;
        TextView  txtPhone;
        TextView  txtDistance;
        FrameLayout layoutCarName;
        VehicleViewHolder(View itemView) {
            super(itemView);
            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtName         = (TextView)        itemView.findViewById(R.id.txt_name);
            txtCarMade      = (TextView)        itemView.findViewById(R.id.txt_car_made);
            txtCarType      = (TextView)        itemView.findViewById(R.id.txt_car_type);
            btnCall         = (ImageView)       itemView.findViewById(R.id.img_call);
            txtCarSize      = (TextView)        itemView.findViewById(R.id.txt_car_size);
            txtCarName      = (TextView)        itemView.findViewById(R.id.txt_car_name);
            txtPhone        = (TextView)        itemView.findViewById(R.id.txt_phone);
            txtDistance     = (TextView)        itemView.findViewById(R.id.txt_distance);
            layoutCarName   = (FrameLayout)     itemView.findViewById(R.id.layout_car_name);
        }

    }
    public interface onClickListener
    {
        public void onItemClick(int position);

    }
}