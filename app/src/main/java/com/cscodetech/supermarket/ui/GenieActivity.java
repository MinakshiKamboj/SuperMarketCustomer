package com.cscodetech.supermarket.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.Banner;
import com.cscodetech.supermarket.model.PackData;
import com.cscodetech.supermarket.model.PackageCategoryItem;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class GenieActivity extends RootActivity implements GetResult.MyListener {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerBanner;
    @BindView(R.id.my_category)
    RecyclerView myCategory;
    LinearLayoutManager layoutManager;

    int position;
    Timer timer;
    TimerTask timerTask;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    public static GenieActivity activity;

    public static GenieActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genie);
        ButterKnife.bind(this);
        activity = this;
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(GenieActivity.this);
        user = sessionManager.getUserDetails("");
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        myRecyclerBanner.setLayoutManager(layoutManager);

        myCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        setbanner();
        getBanner();
    }


    @OnClick({R.id.img_back, R.id.btn_addpickup})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                finish();
                break;

            case R.id.btn_addpickup:
                if (sessionManager.getBooleanData(SessionManager.login)) {
                    startActivity(new Intent(GenieActivity.this, GeniePayNowActivity.class));
                } else {
                    startActivity(new Intent(GenieActivity.this, LoginActivity.class));
                }

                break;
            default:
                break;
        }
    }

    private void getBanner() {
        custPrograssbar.prograssCreate(GenieActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getPackdata(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    PackData packData;

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                packData = gson.fromJson(result.toString(), PackData.class);
                if (packData.getResult().equalsIgnoreCase("true")) {
                    Categoty bannerAdapter11 = new Categoty(this, packData.getResultData().getPackageCategory());
                    myCategory.setAdapter(bannerAdapter11);
                    BannerAdp bannerAdp = new BannerAdp(this, packData.getResultData().getPackageBanner());
                    myRecyclerBanner.setAdapter(bannerAdp);
                }
            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());
        }
    }

    public class Categoty extends RecyclerView.Adapter<Categoty.BannerHolder> {
        private Context context;
        private List<PackageCategoryItem> mBanner;

        public Categoty(Context context, List<PackageCategoryItem> mBanner) {
            this.context = context;
            this.mBanner = mBanner;
        }

        @NonNull
        @Override
        public Categoty.BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_circle_item, parent, false);
            return new Categoty.BannerHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Categoty.BannerHolder holder, int position) {

            Glide.with(context).load(APIClient.baseUrl + "/" + mBanner.get(position).getCatImg()).thumbnail(Glide.with(context).load(R.drawable.ezgifresize)).centerCrop().into(holder.imgBanner);
        }

        @Override
        public int getItemCount() {
            return mBanner.size();
        }

        public class BannerHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.img_banner)
            ImageView imgBanner;

            public BannerHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public class BannerAdp extends RecyclerView.Adapter<BannerAdp.BannerHolder> {
        private Context context;
        private List<Banner> mBanner;

        public BannerAdp(Context context, List<Banner> mBanner) {
            this.context = context;
            this.mBanner = mBanner;
        }

        @NonNull
        @Override
        public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_item, parent, false);
            return new BannerHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerHolder holder, int position) {

            Glide.with(context).load(APIClient.baseUrl + "/" + mBanner.get(position).getImg()).thumbnail(Glide.with(context).load(R.drawable.ezgifresize)).centerCrop().into(holder.imgBanner);
        }

        @Override
        public int getItemCount() {
            return mBanner.size();
        }

        public class BannerHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.img_banner)
            ImageView imgBanner;

            public BannerHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    private void runAutoScrollBanner() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (position == myRecyclerBanner.getAdapter().getItemCount() - 1) {
                            position = 0;
                            myRecyclerBanner.smoothScrollBy(5, 0);
                            myRecyclerBanner.smoothScrollToPosition(position);
                        } else {
                            position++;
                            myRecyclerBanner.smoothScrollToPosition(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            };
            timer.schedule(timerTask, 4000, 4000);
        }

    }

    private void setbanner() {
        position = 0;
        myRecyclerBanner.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(myRecyclerBanner);
        myRecyclerBanner.smoothScrollBy(5, 0);

        myRecyclerBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 1) {
                    stopAutoScrollBanner();
                } else if (newState == 0) {
                    position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    runAutoScrollBanner();
                }
            }
        });
    }

    private void stopAutoScrollBanner() {
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }
}