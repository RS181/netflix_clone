package com.example.NETFRIXAPP;


import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Customlistviewadapter extends BaseAdapter {
    Context context;
    private List<Map<String,String>> list = new ArrayList<>();
    LayoutInflater inflater;

    public Customlistviewadapter(Context ctx, List<Map<String,String>> list){
        this.context = ctx;
        this.list = list;
        inflater = LayoutInflater.from(ctx);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.customlistview, null);
        TextView textView = (TextView) view.findViewById(R.id.moviedesc);
        textView.setText((String) list.get(i).get("MovieName"));

        Button LowQ = view.findViewById(R.id.lowQ);
        Button HighQ = view.findViewById(R.id.highQ);


        LowQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick(i, view, 1);
            }
        });

        HighQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick(i, view, 2);
            }
        });
        return view;
    }

    private void onButtonClick(int position, View view, int control) {
        Context context = view.getContext();
        Intent intent = new Intent(context,Player_Activity.class);
        Toast.makeText(view.getContext(), list.get(position).get("MovieName"), Toast.LENGTH_SHORT ).show();

        if(control == 1) {
            intent.putExtra("MovieLink",list.get(position).get("MovieLow"));
        }else {
            intent.putExtra("MovieLink",list.get(position).get("MovieHigh"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
