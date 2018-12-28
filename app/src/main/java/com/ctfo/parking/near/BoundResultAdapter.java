package com.ctfo.parking.near;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudItem;
import com.ctfo.parking.R;
import com.ctfo.parking.util.StringUtil;

import java.util.HashMap;
import java.util.List;

public class BoundResultAdapter extends BaseAdapter {

    private List<CloudItem> items;
    private Context context;

    public BoundResultAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return items == null? 0: items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CloudItem item = items.get(position);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.near_search_result_list_item, null);
        }else {
            view=convertView;
        }
        ((TextView)view.findViewById(R.id.title)).setText(item.getTitle());
        ((TextView)view.findViewById(R.id.distance)).setText(String.format("%.2f", ((double)item.getDistance()/1000)) + "km");
        HashMap<String,String> map = item.getCustomfield();
        if(map!= null){
            ((TextView)view.findViewById(R.id.fee_scale)).setText(StringUtil.formatString(map.get("feeScale"),"-"));
        }else{
            ((TextView)view.findViewById(R.id.fee_scale)).setText("-");
        }
        return view;
    }

    public void setItems(List<CloudItem> items){
        this.items = items;
    }
}
