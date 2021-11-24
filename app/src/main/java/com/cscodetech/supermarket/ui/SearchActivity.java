package com.cscodetech.supermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.adepter.CategoryAdapter;
import com.cscodetech.supermarket.adepter.ProductAdapter;
import com.cscodetech.supermarket.model.Category;
import com.cscodetech.supermarket.model.Medicine;
import com.cscodetech.supermarket.model.Search;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.storeid;

public class SearchActivity extends RootActivity implements GetResult.MyListener, ProductAdapter.RecyclerTouchListener, CategoryAdapter.RecyclerTouchListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_notfound)
    ImageView imgNotfound;
    @BindView(R.id.ed_search)
    EditText edSearch;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.lvl_actionsearch)
    LinearLayout lvlActionsearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    @BindView(R.id.recycler_category)
    RecyclerView recyclerCategory;

    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    CategoryAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(SearchActivity.this);
        user = sessionManager.getUserDetails("");
        recyclerProduct.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!edSearch.getText().toString().isEmpty()) {
                        getSearch(edSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        getCategory();

    }

    private void getSearch(String keyword) {
        custPrograssbar.prograssCreate(SearchActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", keyword);
            jsonObject.put("store_id", sessionManager.getStringData(storeid));
            jsonObject.put("pincode", sessionManager.getStringData(pincode));
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getSearch(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @OnClick({R.id.img_back, R.id.img_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search:
                if (!edSearch.getText().toString().isEmpty()) {
                    getSearch(edSearch.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Search search = gson.fromJson(result.toString(), Search.class);
                if (search.getResult().equalsIgnoreCase("true")) {
                    if (!search.getSearchData().isEmpty()) {
                        lvlNotfound.setVisibility(View.GONE);
                        recyclerCategory.setVisibility(View.GONE);
                        recyclerProduct.setVisibility(View.VISIBLE);
                        ProductAdapter productAdapter = new ProductAdapter(SearchActivity.this, search.getSearchData(), this);
                        recyclerProduct.setAdapter(productAdapter);
                    } else {
                        lvlNotfound.setVisibility(View.VISIBLE);
                        Toast.makeText(SearchActivity.this, search.getResponseMsg(), Toast.LENGTH_LONG).show();
                        imgNotfound.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_found));
                    }

                } else {
                    recyclerProduct.setVisibility(View.GONE);
                    lvlNotfound.setVisibility(View.VISIBLE);
                    imgNotfound.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_found));
                    Toast.makeText(SearchActivity.this, search.getResponseMsg(), Toast.LENGTH_LONG).show();

                }
            } else if (callNo.equalsIgnoreCase("category")) {
                Gson gson = new Gson();
                Category category = gson.fromJson(result.toString(), Category.class);
                if (category.getResult().equalsIgnoreCase("true") && !category.getCategoryData().isEmpty()) {

                    lvlNotfound.setVisibility(View.GONE);
                    recyclerCategory.setVisibility(View.VISIBLE);
                    recyclerProduct.setVisibility(View.GONE);
                    categoryAdapter = new CategoryAdapter(SearchActivity.this, category.getCategoryData(), this, "simple");
                    recyclerCategory.setAdapter(categoryAdapter);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClickProductItem(String titel, Medicine medicine) {
        startActivity(new Intent(SearchActivity.this, DetailsActivity.class).putExtra("MyClass", medicine).putParcelableArrayListExtra("PriceList", medicine.getProductInfo()).putStringArrayListExtra("ImageList", medicine.getProductImage()));
    }
    private void getCategory() {
        custPrograssbar.prograssCreate(SearchActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("pincode", sessionManager.getStringData(pincode));
            jsonObject.put("store_id", sessionManager.getStringData(storeid));

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCatList(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "category");
    }
    @Override
    public void onClickCategoryItem(String titel, int position) {
        searchCategory = position;
        finish();
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    public static int searchCategory = -1;
}