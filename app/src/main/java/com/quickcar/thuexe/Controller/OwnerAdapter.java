package com.quickcar.thuexe.Controller;

/**
 * Created by DatNT on 8/9/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.quickcar.thuexe.Models.OwnerCarObject;
import com.quickcar.thuexe.R;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class OwnerAdapter extends BaseAdapter  {

    /*********** Declare Used Variables *********/
    private Context context;
    private ArrayList<OwnerCarObject> data;
    private static LayoutInflater inflater=null;
    private onClickListener onClick;
    private onUpdateListener onUpdate;
    /*************  CustomAdapter Constructor *****************/
    public OwnerAdapter(Context context, ArrayList<OwnerCarObject> data) {
        this.context = context;
        this.data = data;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView txtTilte;
        public TextView txtRoute;
        public LinearLayout layoutOwner;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.custom_owner_listview, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtTilte     = (TextView)     vi.findViewById(R.id.txt_title);
            holder.txtRoute     =(TextView)      vi.findViewById(R.id.txt_route);
            holder.layoutOwner  =(LinearLayout)  vi.findViewById(R.id.layout_owner);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.txtTilte.setText("Không có xe thỏa mãn");
            holder.txtRoute.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.txtTilte.setText( data.get(position).getOwnerName() );
            holder.txtRoute.setText( data.get(position).getStartPlace() +" - "+data.get(position).getEndPlace() );
            if (position % 2==0)
                holder.layoutOwner.setBackgroundResource(R.drawable.click_white);
            else
                holder.layoutOwner.setBackgroundResource(R.drawable.click_grey);
            holder.layoutOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClick!=null)
                        onClick.onItemClick(position);
                }
            });
        }
        return vi;
    }
    public void setOnItemClickListener(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    public interface onClickListener
    {
        public void onItemClick(int position);

    }
    public void setOnUpdateData(final onUpdateListener onUpdate)
    {
        this.onUpdate = onUpdate;
    }
    public interface onUpdateListener
    {
        public void onUpdate();

    }
}