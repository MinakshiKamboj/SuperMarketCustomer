package com.cscodetech.supermarket.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.model.Wallet;
import com.cscodetech.supermarket.model.WalletitemItem;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class WallateHistryActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotFound;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallate_histry);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(WallateHistryActivity.this);
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();

        recyclerView.setLayoutManager(new GridLayoutManager(WallateHistryActivity.this, 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getHistry();

    }

    private void getHistry() {
        custPrograssbar.prograssCreate(WallateHistryActivity.this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("uid", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getHistry(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Wallet walletHistry = gson.fromJson(result.toString(), Wallet.class);
                if (walletHistry.getResult().equalsIgnoreCase("true")) {
                    if(walletHistry.getWalletitem().isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        lvlNotFound.setVisibility(View.VISIBLE);
                    }else {
                        HistryAdp histryAdp = new HistryAdp(walletHistry.getWalletitem());
                        recyclerView.setAdapter(histryAdp);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    lvlNotFound.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());
        }
    }


    public class HistryAdp extends RecyclerView.Adapter<HistryAdp.MyViewHolder> {
        private List<WalletitemItem> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtNo;
            public TextView txtMessage;
            public TextView txtStatus;
            public TextView txtAmount;

            public MyViewHolder(View view) {
                super(view);
                txtNo = (TextView) view.findViewById(R.id.txt_no);
                txtMessage = (TextView) view.findViewById(R.id.txt_message);
                txtStatus = (TextView) view.findViewById(R.id.txt_status);
                txtAmount = (TextView) view.findViewById(R.id.txt_amount);
            }
        }

        public HistryAdp(List<WalletitemItem> list) {
            this.list = list;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_histry, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            WalletitemItem category = list.get(position);
            holder.txtNo.setText("" + position);
            holder.txtMessage.setText(category.getMessage());
            holder.txtStatus.setText(category.getStatus());
            holder.txtAmount.setText(sessionManager.getStringData(SessionManager.currency) + category.getAmt());


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @OnClick({R.id.img_back})
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}