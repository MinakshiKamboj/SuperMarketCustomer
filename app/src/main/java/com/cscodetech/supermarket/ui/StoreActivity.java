package com.cscodetech.supermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.adepter.StoreAdepter;
import com.cscodetech.supermarket.model.RestResponse;
import com.cscodetech.supermarket.model.Store;
import com.cscodetech.supermarket.model.StoreDataItem;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.DatabaseHelper;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.pincoded;
import static com.cscodetech.supermarket.utiles.Utility.changeAddress;

public class StoreActivity extends RootActivity implements GetResult.MyListener {

    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    StaggeredGridLayoutManager gridLayoutManager;
    SessionManager sessionManager;
    User user;
    CustPrograssbar custPrograssbar;
    @BindView(R.id.txt_notfount)
    TextView txtNotFount;
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;

    @BindView(R.id.ed_search)
    EditText edSearch;
    StoreAdepter adepter;
    public boolean changeStore = false;
    DatabaseHelper helper;
    public static StoreActivity activity = null;

    public static StoreActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);
        activity = this;
        helper=new DatabaseHelper(StoreActivity.this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(StoreActivity.this);
        user = sessionManager.getUserDetails("");
        gridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        myRecyclerView.setLayoutManager(gridLayoutManager);
        txtLocation.setText("Deliver to " + sessionManager.getStringData(pincoded));
        getStore();
    }

    @OnClick({R.id.img_back, R.id.img_search, R.id.txt_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search:

                break;

            case R.id.txt_location:
                startActivity(new Intent(StoreActivity.this, AddressActivity.class));
                break;

            default:
                break;
        }
    }

    private void getStore() {
        custPrograssbar.prograssCreate(StoreActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("pincode", sessionManager.getStringData(pincode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getStore(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    public void bottonCardClear() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.crearcard_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        TextView txtCrear = sheetView.findViewById(R.id.txt_crear);
        TextView txtNo = sheetView.findViewById(R.id.txt_no);
        mBottomSheetDialog.show();
        txtCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.deleteCard();
                mBottomSheetDialog.cancel();

            }
        });
        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.cancel();
            }
        });
    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Store store = gson.fromJson(result.toString(), Store.class);
                if (store.getResult().equalsIgnoreCase("true")) {
                    lvlNotfound.setVisibility(View.GONE);

                    List<StoreDataItem> itemList = new ArrayList<>();
                    itemList.addAll(store.getStoreData());

                    adepter = new StoreAdepter(StoreActivity.this, itemList);
                    myRecyclerView.setAdapter(adepter);

                } else {
                    myRecyclerView.setVisibility(View.GONE);
                    lvlNotfound.setVisibility(View.VISIBLE);
                    txtNotFount.setText("Your orders will be displayed hear.");
                }
                edSearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        adepter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equalsIgnoreCase("true")) {
                    adepter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null && AddressActivity.getInstance() != null && changeAddress) {
            changeAddress = false;
            recreate();
        }
    }
}