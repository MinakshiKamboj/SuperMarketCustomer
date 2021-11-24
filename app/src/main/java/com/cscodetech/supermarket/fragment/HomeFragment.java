package com.cscodetech.supermarket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.adepter.BannerAdapter;
import com.cscodetech.supermarket.adepter.CategoryAdapter;
import com.cscodetech.supermarket.adepter.ProductHomeAdapter;
import com.cscodetech.supermarket.adepter.UserAdapter;
import com.cscodetech.supermarket.model.Banner;
import com.cscodetech.supermarket.model.Home;
import com.cscodetech.supermarket.model.HomeDatum;
import com.cscodetech.supermarket.model.Medicine;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.ui.DetailsActivity;
import com.cscodetech.supermarket.ui.GenieActivity;
import com.cscodetech.supermarket.ui.HomeActivity;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.cscodetech.supermarket.MyApplication.mContext;
import static com.cscodetech.supermarket.utiles.SessionManager.about;
import static com.cscodetech.supermarket.utiles.SessionManager.contact;
import static com.cscodetech.supermarket.utiles.SessionManager.currency;
import static com.cscodetech.supermarket.utiles.SessionManager.itemlimit;
import static com.cscodetech.supermarket.utiles.SessionManager.minmumorder;
import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.policy;
import static com.cscodetech.supermarket.utiles.SessionManager.sgen;
import static com.cscodetech.supermarket.utiles.SessionManager.storeid;
import static com.cscodetech.supermarket.utiles.SessionManager.terms;


public class HomeFragment extends Fragment implements CategoryAdapter.RecyclerTouchListener, UserAdapter.RecyclerTouchListener, ProductHomeAdapter.RecyclerTouchListener, GetResult.MyListener {

    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerBanner;
    @BindView(R.id.recycler_category)
    RecyclerView recyclerCategory;
    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    @BindView(R.id.allproduct)
    LinearLayout lvlAllproduct;
    @BindView(R.id.recycler_user)
    RecyclerView recyclerUser;
    LinearLayoutManager layoutManager;
    int position;
    Timer timer;
    TimerTask timerTask;
    CategoryAdapter categoryAdapter;
    ProductHomeAdapter productAdapter;
    UserAdapter userAdapter;


    @BindView(R.id.txt_viewll_category)
    TextView txtViewAllCategory;
    @BindView(R.id.lvlv)
    LinearLayout lvlv;
    @BindView(R.id.txt_viewll_product)
    TextView txtViewllProduct;
    @BindView(R.id.lvlv1)
    LinearLayout lvlv1;
    @BindView(R.id.txt_viewll_brand)
    TextView txtViewAllBrand;
    @BindView(R.id.lvlv2)
    LinearLayout lvlTwo;
    @BindView(R.id.txt_viewll_user)
    TextView txtViewllUser;
    @BindView(R.id.lvlv3)
    LinearLayout lvlTree;
    @BindView(R.id.rtv_expoler)
    RelativeLayout rtvExpoler;
    @BindView(R.id.img_guin)
    ImageView imgGuin;
    @BindView(R.id.lvl_guin)
    CardView lvlGuin;


    SessionManager sessionManager;
    User user;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList = new ArrayList<>();
    CustPrograssbar custPrograssbar;
    List<HomeDatum> dynamicDataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        myRecyclerBanner.setLayoutManager(layoutManager);
        setbanner();


        recyclerCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());


        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerProduct.setLayoutManager(mLayoutManager2);
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());


        LinearLayoutManager mLayoutManager4 = new LinearLayoutManager(getActivity());
        mLayoutManager4.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerUser.setLayoutManager(mLayoutManager4);
        recyclerUser.setItemAnimator(new DefaultItemAnimator());

        getHome();

        return view;
    }

    private void setJoinPlayrList(LinearLayout lnrView, List<HomeDatum> dataList) {

        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_home_item, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            RecyclerView recyclerViewList = view.findViewById(R.id.recycler_view_list);
            itemTitle.setText(dataList.get(i).getTitle());
            ProductHomeAdapter itemAdp = new ProductHomeAdapter(mContext, dataList.get(i).getProductData(), this);
            recyclerViewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewList.setAdapter(itemAdp);
            lnrView.addView(view);
        }
    }


    @Override
    public void onClickProductItem(String titel, Medicine medicine) {
        startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("MyClass", medicine).putParcelableArrayListExtra("PriceList", medicine.getProductInfo()).putStringArrayListExtra("ImageList", medicine.getProductImage()));
    }

    @Override
    public void onClickCategoryItem(String titel, int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        Fragment fragment = new SubCategoryFragment();
        fragment.setArguments(args);
        HomeActivity.getInstance().openFragment(fragment);
    }


    @Override
    public void onClickUserItem(String titel, int position) {
        Log.e("item", "" + position);
    }

    @OnClick({R.id.txt_viewll_category, R.id.txt_viewll_product, R.id.txt_viewll_brand, R.id.txt_viewll_user, R.id.img_guin})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_viewll_category:
                HomeActivity.getInstance().openFragment(new CategoryFragment());
                break;
            case R.id.txt_viewll_product:
                HomeActivity.getInstance().openFragment(new PopularProductFragment());
                break;
            case R.id.img_guin:
                startActivity(new Intent(getActivity(), GenieActivity.class));
                break;

            default:
                break;
        }
    }


    private void getHome() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("store_id", sessionManager.getStringData(storeid));
            jsonObject.put("pincode", sessionManager.getStringData(pincode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getHome(bodyRequest);
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
                Home home = gson.fromJson(result.toString(), Home.class);
                if (home.getResult().equalsIgnoreCase("true")) {

                    bannerList = home.getResultData().getBanner();
                    sessionManager.setStringData(currency, home.getResultData().getMainData().getCurrency());
                    sessionManager.setStringData(terms, home.getResultData().getMainData().getTerms());
                    sessionManager.setStringData(contact, home.getResultData().getMainData().getContact());
                    sessionManager.setStringData(about, home.getResultData().getMainData().getAbout());
                    sessionManager.setStringData(policy, home.getResultData().getMainData().getPolicy());
                    sessionManager.setStringData(minmumorder, home.getResultData().getMainData().getMinorder());
                    sessionManager.setStringData(itemlimit, home.getResultData().getMainData().getItemlimit());
                    sessionManager.setStringData(sgen, home.getResultData().getMainData().getSgen());
                    Glide.with(mContext).load(APIClient.baseUrl + "/" + home.getResultData().getMainData().getPdbanner()).thumbnail(Glide.with(mContext).load(R.drawable.ezgifresize)).centerCrop().into(imgGuin);

                    bannerAdapter = new BannerAdapter(getActivity(), bannerList);
                    myRecyclerBanner.setAdapter(bannerAdapter);

                    categoryAdapter = new CategoryAdapter(getActivity(), home.getResultData().getCatlist(), this, "single");
                    recyclerCategory.setAdapter(categoryAdapter);
                    if (home.getResultData().getMedicine().isEmpty()) {
                        rtvExpoler.setVisibility(View.GONE);
                    } else {
                        rtvExpoler.setVisibility(View.VISIBLE);
                    }
                    if (home.getResultData().getMainData().getSgen().equalsIgnoreCase("1")) {
                        lvlGuin.setVisibility(View.VISIBLE);
                    } else {
                        lvlGuin.setVisibility(View.GONE);
                    }
                    productAdapter = new ProductHomeAdapter(getActivity(), home.getResultData().getMedicine(), this);
                    recyclerProduct.setAdapter(productAdapter);


                    userAdapter = new UserAdapter(getActivity(), home.getResultData().getTestimonial(), this);
                    recyclerUser.setAdapter(userAdapter);
                    dynamicDataList = home.getResultData().getHomeDatumList();
                    setJoinPlayrList(lvlAllproduct, dynamicDataList);

                } else {
                    Toast.makeText(getActivity(), home.getResponseMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());
        }
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


    @Override
    public void onResume() {
        super.onResume();
        if (dynamicDataList != null) {
            setJoinPlayrList(lvlAllproduct, dynamicDataList);
        }
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }
}