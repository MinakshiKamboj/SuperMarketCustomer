package com.cscodetech.supermarket.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.Faq;
import com.cscodetech.supermarket.model.FaqDataItem;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
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

import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.storeid;

public class FaqActivity extends AppCompatActivity implements GetResult.MyListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_actiontitle)
    TextView tatActionTiter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(FaqActivity.this);
        user=sessionManager.getUserDetails("");
        custPrograssbar=new CustPrograssbar();

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProduct.setLayoutManager(mLayoutManager2);
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        getFaq();


    }
    private void getFaq() {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("uid", user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getFaq(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if(callNo.equalsIgnoreCase("1")){
                Gson gson=new Gson();
                Faq faq=gson.fromJson(result.toString(),Faq.class);
                if(faq.getResult().equalsIgnoreCase("true")){
                    recyclerProduct.setAdapter(new MyFaqAdepter(faq.getFaqData()));
                }

            }
        }catch (Exception e){

        }
    }

    public class MyFaqAdepter extends RecyclerView.Adapter<MyFaqAdepter.ViewHolder> {
        private List<FaqDataItem> orderData;

        public MyFaqAdepter(List<FaqDataItem> orderData) {
            this.orderData = orderData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.faq_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Log.e("position", "" + position);
            FaqDataItem order = orderData.get(position);
            holder.txtTital.setText("" + order.getQuestion());
            holder.txtAns.setText("" + order.getAnswer());

            holder.lvlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.lvlItem.getVisibility() == View.VISIBLE) {

                        holder.imgRight.setBackgroundResource(R.drawable.ic_right);
                        TranslateAnimation animate = new TranslateAnimation(
                                0,
                                0,
                                0,
                                holder.lvlItem.getHeight());
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        holder.lvlItem.startAnimation(animate);
                        holder.lvlItem.setVisibility(View.GONE);
                    } else {
                        holder.lvlItem.setVisibility(View.VISIBLE);
                        TranslateAnimation animate = new TranslateAnimation(
                                0,
                                0,
                                holder.lvlItem.getHeight(),
                                0);
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        holder.lvlItem.startAnimation(animate);

                        holder.imgRight.setBackgroundResource(R.drawable.ic_expand);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_tital)
            TextView txtTital;
            @BindView(R.id.txt_ans)
            TextView txtAns;

            @BindView(R.id.lvl_item)
            LinearLayout lvlItem;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;
            @BindView(R.id.img_right)
            ImageView imgRight;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }
}