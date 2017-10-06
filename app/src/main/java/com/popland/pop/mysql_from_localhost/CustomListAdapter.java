package com.popland.pop.mysql_from_localhost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hai on 27/06/2016.
 */
public class CustomListAdapter extends ArrayAdapter<Sanpham> {
    public CustomListAdapter(Context context, int resource, List<Sanpham> objects) {
        super(context, resource, objects);
    }
    public View getView(int position,View convertView,ViewGroup parent){
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.custom_row,null);
        }
        Sanpham p = getItem(position);
        if(p!=null){
            TextView tvId = (TextView)v.findViewById(R.id.TVid);
            tvId.setText(p.id+"");
            TextView tvTensp = (TextView)v.findViewById(R.id.TVtensp);
            tvTensp.setText(p.tensp);
            TextView tvGiasp = (TextView)v.findViewById(R.id.TVgiasp);
            tvGiasp.setText(p.giasp+"");
        }
        return v;
    }
}
