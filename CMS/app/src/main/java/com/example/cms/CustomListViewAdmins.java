package com.example.cms;


import android.annotation.SuppressLint;
import android.content.Context;
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

public class CustomListViewAdmins extends BaseAdapter {
    Context context;
    private List<Map<String,String>> list = new ArrayList<>();
    LayoutInflater inflater;

    public CustomListViewAdmins(Context ctx, List<Map<String,String>> list){
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

    @SuppressLint({"ViewHolder", "SetTextI18n", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.customlistview, null);
        TextView textView = (TextView) view.findViewById(R.id.moviedesc);
        textView.setText((String) list.get(i).get("Password"));
        TextView textView2 = (TextView) view.findViewById(R.id.id);
        textView2.setText("Admin : " + (String) list.get(i).get("Admin"));
        Button remover = view.findViewById(R.id.remover);

        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onRemoveButtonClick(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    private void onRemoveButtonClick(int position) throws JSONException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        String aux = (String) list.get(position).get("Admin");
        JSONObject jsonData = new JSONObject();
        jsonData.put("login",aux);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonData.toString());
        Toast.makeText(inflater.getContext(), aux ,Toast.LENGTH_SHORT).show();
        Call<Post> call = jsonPlaceHolderApi.removeAdmin(body);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){
                    Toast.makeText(inflater.getContext(),  position + "REMOVED - Refresh the page", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else {
                    Toast.makeText(inflater.getContext(),  position + "Try again - Refresh the page", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(inflater.getContext(), "FAILED FR FR", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
