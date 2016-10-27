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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.Defines;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by DatNT on 6/28/2016.
 */
public class CarTypesAdapter extends RecyclerView.Adapter<CarTypesAdapter.VehicleViewHolder> {

    private ArrayList<String> carTypes;
    private Integer[] carTypeIcon = {R.mipmap.all_car, R.mipmap.free_car, R.mipmap.taxi,R.mipmap.wedding_car,R.mipmap.contract_car,R.mipmap.self_driver,R.mipmap.delivery_car,R.mipmap.container,R.mipmap.coach};
    private Context mContext;
    private onClickListener onClick;
    private String selectedPoss = "Tất cả";
    private RecyclerView cars;
    public CarTypesAdapter(Context mContext, ArrayList<String> vehicles, RecyclerView cars) {
        this.carTypes = vehicles;
        this.mContext = mContext;
        this.cars = cars;
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_car_types, parent, false);
        VehicleViewHolder pvh = new VehicleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final VehicleViewHolder holder, final int position) {
        holder.imgCarTypes.setImageResource(carTypeIcon[position]);
        if (carTypes.get(position).equals(selectedPoss)) {
            holder.cardview.setBackgroundResource(R.drawable.click_car_type);
            cars.getLayoutManager().scrollToPosition(position);
        }else
            holder.cardview.setBackgroundResource(R.drawable.bg_car_type);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null)
                    onClick.onItemClick(position);
                selectedPoss = carTypes.get(position);
                notifyDataSetChanged();
            }
        });
        holder.txtCarTypes.setText(carTypes.get(position));

    }
    public void setSelectedPostion(String position){
        this.selectedPoss = position;
    }
    public void setOnItemClickListener(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    @Override
    public int getItemCount() {
        return carTypes.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardview;
        ImageView imgCarTypes;
        TextView  txtCarTypes;
        VehicleViewHolder(View itemView) {
            super(itemView);
            cardview        = (RelativeLayout)  itemView.findViewById(R.id.cv_car_type);
            imgCarTypes     = (ImageView)       itemView.findViewById(R.id.img_car_type);
            txtCarTypes     = (TextView)        itemView.findViewById(R.id.txt_car_type);
        }

    }
    public interface onClickListener
    {
        public void onItemClick(int position);

    }
}
