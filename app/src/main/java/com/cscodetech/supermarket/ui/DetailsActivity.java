package com.cscodetech.supermarket.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.MyApplication;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.adepter.ProductAdapter;
import com.cscodetech.supermarket.adepter.ProductHomeAdapter;
import com.cscodetech.supermarket.model.BProduct;
import com.cscodetech.supermarket.model.Medicine;
import com.cscodetech.supermarket.model.ProductPrice;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.DatabaseHelper;
import com.cscodetech.supermarket.utiles.MyCart;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.cscodetech.supermarket.utiles.SessionManager.currency;
import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.storeid;


public class DetailsActivity extends RootActivity implements GetResult.MyListener, ProductHomeAdapter.RecyclerTouchListener {

    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.txt_item_offer)
    TextView txtItemOffer;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.txt_countcard)
    TextView txtCountcard;
    @BindView(R.id.txt_offer)
    TextView txtOffer;
    @BindView(R.id.lvl_offer)
    LinearLayout lvlOffer;
    @BindView(R.id.lvl_addcart)
    LinearLayout lvlAddcart;
    @BindView(R.id.txt_desc)
    TextView txtDesc;
    @BindView(R.id.txt_brandname)
    TextView txtBrandname;
    @BindView(R.id.btn_addtocart)
    TextView btnAddtocart;
    @BindView(R.id.txt_priceone)
    TextView txtPriceone;
    @BindView(R.id.lvl_spineer)
    LinearLayout lvlSpineer;
    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabview)
    TabLayout tabview;
    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;

    Medicine medicine;
    ArrayList<String> productImage = new ArrayList<>();
    private ArrayList<ProductPrice> productInfo = new ArrayList<>();
    SessionManager sessionManager;
    DatabaseHelper helper;
    CustPrograssbar custPrograssbar;



    public static DetailsActivity detailsActivity;

    public static DetailsActivity getInstance() {
        return detailsActivity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        ButterKnife.bind(this);
        helper = new DatabaseHelper(DetailsActivity.this);
        detailsActivity = this;
        custPrograssbar=new CustPrograssbar();
        sessionManager = new SessionManager(DetailsActivity.this);
        medicine = (Medicine) getIntent().getParcelableExtra("MyClass");
        productInfo = getIntent().getParcelableArrayListExtra("PriceList");
        productImage = getIntent().getStringArrayListExtra("ImageList");


        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerProduct.setLayoutManager(mLayoutManager2);
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDesc.setText(Html.fromHtml(medicine.getShortDesc(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtDesc.setText(Html.fromHtml(medicine.getShortDesc()));
        }
        txtTitle.setText("" + medicine.getProductName());
        txtBrandname.setText("" + medicine.getBrandName());


        if (productImage != null && !productImage.isEmpty()) {

            tabview.setupWithViewPager(viewPager, true);
        }

        MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(this, productImage);
        viewPager.setAdapter(myCustomPagerAdapter);


        List<String> arealist = new ArrayList<>();
        if (productInfo.size() == 1) {
            txtPriceone.setText("" + productInfo.get(0).getProductType());
            lvlSpineer.setVisibility(View.GONE);
            setJoinPlayrList(lvlAddcart, productInfo.get(0));

        } else {
            for (int i = 0; i < productInfo.size(); i++) {
                arealist.add(productInfo.get(i).getProductType());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arealist);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    positontype = position;
                    setJoinPlayrList(lvlAddcart, productInfo.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Log.e("tets", "dkjah");
                }

            });
        }
        getReletedProduct();
    }

    private void getReletedProduct() {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("pincode", sessionManager.getStringData(pincode));
            jsonObject.put("cid", sessionManager.getStringData(pincode));
            jsonObject.put("sid", sessionManager.getStringData(storeid));
            jsonObject.put("pid",medicine.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getReleted(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @OnClick({R.id.img_back, R.id.rlt_cart, R.id.btn_addtocart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_addtocart:
                finish();
                break;
            case R.id.rlt_cart:
                if (sessionManager.getBooleanData(SessionManager.login)) {
                    startActivity(new Intent(DetailsActivity.this, CartActivity.class));

                } else {
                    startActivity(new Intent(DetailsActivity.this, LoginActivity.class));

                }
                break;
            default:
                break;
        }
    }

    private void setJoinPlayrList(LinearLayout lnrView, ProductPrice productPrice) {
        lnrView.removeAllViews();
        final int[] count = {0};
        LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
        View view = inflater.inflate(R.layout.custome_prized, null);
        TextView txtcount = view.findViewById(R.id.txtcount);
        LinearLayout txtOutstock = view.findViewById(R.id.txt_outstock);
        LinearLayout lvlAddremove = view.findViewById(R.id.lvl_addremove);
        LinearLayout lvlAddcart1 = view.findViewById(R.id.lvl_addcart);
        LinearLayout imgMins = view.findViewById(R.id.img_mins);
        LinearLayout imgPlus = view.findViewById(R.id.img_plus);
        MyCart myCart = new MyCart();
        myCart.setPid(medicine.getId());
        myCart.setProductName(medicine.getProductName());
        myCart.setProductPrice(productPrice.getProductPrice());
        myCart.setProductImage(medicine.getProductImage().get(0));
        myCart.setBrandName(medicine.getBrandName());
        myCart.setDiscount(productPrice.getProductDiscount());
        myCart.setShortDesc(medicine.getShortDesc());
        myCart.setAttributeId(productPrice.getAttributeId());
        myCart.setProductType(productPrice.getProductType());
        updateItem();

        if (productPrice.getProductInStock().equalsIgnoreCase("1")) {
            txtOutstock.setVisibility(View.VISIBLE);
            lvlAddremove.setVisibility(View.GONE);
            lvlAddcart1.setVisibility(View.GONE);

        } else {
            int qrt = helper.getCard(myCart.getAttributeId());
            if (qrt != -1) {
                count[0] = qrt;
                txtcount.setText("" + count[0]);
                lvlAddremove.setVisibility(View.VISIBLE);
                lvlAddcart1.setVisibility(View.GONE);
            } else {
                lvlAddremove.setVisibility(View.GONE);
                lvlAddcart1.setVisibility(View.VISIBLE);
            }
        }
        if (productPrice.getProductDiscount() == 0) {
            lvlOffer.setVisibility(View.GONE);
            txtPrice.setText(sessionManager.getStringData(currency) + productPrice.getProductPrice());
            txtItemOffer.setVisibility(View.GONE);
        } else {
            lvlOffer.setVisibility(View.VISIBLE);
            txtItemOffer.setVisibility(View.VISIBLE);
            DecimalFormat format = new DecimalFormat("0.#");
            txtOffer.setText(format.format(productPrice.getProductDiscount()) + "% OFF");
            double res = (Double.parseDouble(productPrice.getProductPrice()) / 100.0f) * productPrice.getProductDiscount();
            res = Double.parseDouble(productPrice.getProductPrice()) - res;
            txtPrice.setText(sessionManager.getStringData(currency) + new DecimalFormat("##.##").format(res));
            txtItemOffer.setText(sessionManager.getStringData(currency) + productPrice.getProductPrice());
            txtItemOffer.setPaintFlags(txtItemOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        imgMins.setOnClickListener(v -> {
            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] - 1;
            if (count[0] <= 0) {
                txtcount.setText("" + count[0]);
                lvlAddremove.setVisibility(View.GONE);
                lvlAddcart1.setVisibility(View.VISIBLE);
                helper.deleteRData(myCart.getAttributeId());
                updateItem();
            } else {
                txtcount.setVisibility(View.VISIBLE);
                txtcount.setText("" + count[0]);
                myCart.setQty(String.valueOf(count[0]));
                updateItem();
            }
        });
        imgPlus.setOnClickListener(v -> {
            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] + 1;
            myCart.setQty(String.valueOf(count[0]));
            if (helper.insertData(myCart)) {
                txtcount.setText("" + count[0]);
                updateItem();

            }
        });
        lvlAddcart1.setOnClickListener(v -> {

            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] + 1;
            myCart.setQty(String.valueOf(count[0]));

            if (helper.insertData(myCart)) {
                updateItem();
                txtcount.setText("" + count[0]);
                lvlAddcart1.setVisibility(View.GONE);
                lvlAddremove.setVisibility(View.VISIBLE);
            }

        });
        lnrView.addView(view);
    }

    public void updateItem() {
        Cursor res = helper.getAllData();
        if (res.getCount() == 0) {
            txtCountcard.setText("0");
        } else {
            txtCountcard.setText("" + res.getCount());
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                BProduct product = gson.fromJson(result.toString(), BProduct.class);
                if (product.getResult().equalsIgnoreCase("true")) {
                    ProductHomeAdapter productAdapter = new ProductHomeAdapter(this, product.getResultData(), this);
                    recyclerProduct.setAdapter(productAdapter);
                }

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClickProductItem(String titel, Medicine medicine) {

    }

    public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
        private Context context;
        private List<String> mBanner;

        public BannerAdapter(Context context, List<String> mBanner) {
            this.context = context;
            this.mBanner = mBanner;
        }

        @NonNull
        @Override
        public BannerAdapter.BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
            return new BannerAdapter.BannerHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
            Glide.with(context).load(APIClient.baseUrl + "/" + mBanner.get(position)).thumbnail(Glide.with(context).load(R.drawable.ezgifresize)).centerCrop().into(holder.imgBanner);
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

    int positontype = 0;

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();

        if (txtCountcard != null) {
            updateItem();
            setJoinPlayrList(lvlAddcart, productInfo.get(positontype));
        }
    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        List<String> imageList;
        LayoutInflater layoutInflater;

        public MyCustomPagerAdapter(Context context, List<String> bannerDatumList) {
            this.context = context;
            this.imageList = bannerDatumList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.item_image, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Glide.with(DetailsActivity.this).load(APIClient.baseUrl + "/" + imageList.get(position)).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();

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
                updateItem();

            }
        });
        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.cancel();
            }
        });
    }
}
