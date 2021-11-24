package com.cscodetech.supermarket.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.CartValidate;
import com.cscodetech.supermarket.model.Payment;
import com.cscodetech.supermarket.model.PaymentItem;
import com.cscodetech.supermarket.model.RestResponse;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.DatabaseHelper;
import com.cscodetech.supermarket.utiles.MyCart;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
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

import static com.cscodetech.supermarket.utiles.SessionManager.address;
import static com.cscodetech.supermarket.utiles.SessionManager.coupon;
import static com.cscodetech.supermarket.utiles.SessionManager.couponid;
import static com.cscodetech.supermarket.utiles.SessionManager.currency;
import static com.cscodetech.supermarket.utiles.SessionManager.dcharge;
import static com.cscodetech.supermarket.utiles.SessionManager.itemlimit;
import static com.cscodetech.supermarket.utiles.SessionManager.minmumorder;
import static com.cscodetech.supermarket.utiles.SessionManager.pincode;
import static com.cscodetech.supermarket.utiles.SessionManager.wallet;
import static com.cscodetech.supermarket.utiles.Utility.ISORDER;
import static com.cscodetech.supermarket.utiles.Utility.changeAddress;
import static com.cscodetech.supermarket.utiles.Utility.paymentId;
import static com.cscodetech.supermarket.utiles.Utility.paymentsucsses;
import static com.cscodetech.supermarket.utiles.Utility.tragectionID;

public class CartActivity extends RootActivity implements GetResult.MyListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_actiontitle)
    TextView txtActionTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @BindView(R.id.img_coopncode)
    ImageView imgCoupnCode;
    @BindView(R.id.lvl_wallet)
    CardView lvlWallet;
    @BindView(R.id.img_wallet)
    ImageView imgWallet;
    @BindView(R.id.ed_customnot)
    EditText edCustom;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_changeadress)
    TextView tatChangeless;
    @BindView(R.id.btn_proceed)
    TextView btnProceed;
    StaggeredGridLayoutManager gridLayoutManager;
    List<MyCart> myCartList = new ArrayList<>();
    DatabaseHelper databaseHelper;
    @BindView(R.id.txt_itemtotal)
    TextView txtItemTotal;
    @BindView(R.id.txt_dcharge)
    TextView txtDCharge;
    @BindView(R.id.txt_Discount)
    TextView txtDiscount;
    @BindView(R.id.txt_topay)
    TextView txtToPay;
    @BindView(R.id.txt_atype)
    TextView txtAType;
    @BindView(R.id.txt_wallet)
    TextView txtWallet;

    SessionManager sessionManager;
    User user;
    double total = 0;
    double subtotal = 0;
    @BindView(R.id.lvl_main)
    LinearLayout lvlMain;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotFound;
    CustPrograssbar custPrograssbar;
    String hase = "##.##";
    String position = "position";
    List<PaymentItem> paymentList = new ArrayList<>();
    boolean isWallet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(CartActivity.this);
        sessionManager.setIntData(coupon, 0);
        custPrograssbar = new CustPrograssbar();
        databaseHelper = new DatabaseHelper(CartActivity.this);
        gridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        myRecyclerView.setLayoutManager(gridLayoutManager);
        user = sessionManager.getUserDetails("");
        jsoncreate();

    }

    private void jsoncreate() {
        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            lvlNotFound.setVisibility(View.VISIBLE);
            lvlMain.setVisibility(View.GONE);
            return;
        }
        JSONArray jsonArray = new JSONArray();
        while (res.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("aid", res.getString(9));
                jsonObject.put("price", res.getString(6));
                jsonObject.put("discount", res.getString(8));
                jsonObject.put("pincode", sessionManager.getStringData(pincode));
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getAddressAndCartDatra(jsonArray);
    }

    private void getPayment() {
        custPrograssbar.prograssCreate(CartActivity.this);

        JSONObject jsonObject = new JSONObject();
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getPaymentList(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }

    private void cartdataset() {
        Cursor res = databaseHelper.getAllData();
        myCartList = new ArrayList<>();
        while (res.moveToNext()) {
            MyCart rModel = new MyCart();
            rModel.setId(res.getString(0));
            rModel.setPid(res.getString(1));
            rModel.setProductName(res.getString(2));
            rModel.setProductImage(res.getString(3));
            rModel.setBrandName(res.getString(4));
            rModel.setShortDesc(res.getString(5));
            rModel.setProductPrice(res.getString(6));
            rModel.setQty(res.getString(7));
            rModel.setDiscount(res.getInt(8));
            rModel.setAttributeId(res.getString(9));
            rModel.setProductType(res.getString(10));
            myCartList.add(rModel);
        }
        ItemAdp itemAdp = new ItemAdp(CartActivity.this, myCartList);
        myRecyclerView.setAdapter(itemAdp);
        updateItem();
    }

    private void getAddressAndCartDatra(JSONArray jsonArray) {
        custPrograssbar.prograssCreate(CartActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("cartdata", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getProductCartAddress(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    public class ItemAdp extends RecyclerView.Adapter<ItemAdp.ViewHolder> {
        final int[] count = {0};
        double[] totalAmount = {0};
        private List<MyCart> mData;
        private LayoutInflater mInflater;
        Context mContext;
        SessionManager sessionManager;
        DatabaseHelper helper;

        public ItemAdp(Context context, List<MyCart> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mContext = context;
            sessionManager = new SessionManager(CartActivity.this);
            helper = new DatabaseHelper(CartActivity.this);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.custome_mycard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {
            MyCart myCart = mData.get(i);
            if (myCart.getDiscount() != 0) {

                double res = (Double.parseDouble(myCart.getProductPrice()) * myCart.getDiscount()) / 100;
                res = Double.parseDouble(myCart.getProductPrice()) - res;
                holder.txtPrice.setText(sessionManager.getStringData(currency) + new DecimalFormat(hase).format(res));
                holder.txtDscount.setPaintFlags(holder.txtDscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtDscount.setText(sessionManager.getStringData(currency) + myCart.getProductPrice());
                holder.lvlOffer.setVisibility(View.VISIBLE);
                holder.txtOffer.setText(myCart.getDiscount() + "% \nOFF ");
            } else {
                holder.txtPrice.setText(sessionManager.getStringData(currency) + myCart.getProductPrice());
                holder.lvlOffer.setVisibility(View.GONE);
                holder.txtDscount.setVisibility(View.GONE);

            }
            Glide.with(mContext).load(APIClient.baseUrl + "/" + myCart.getProductImage()).thumbnail(Glide.with(mContext).load(R.drawable.ezgifresize)).centerCrop().into(holder.imgIcon);

            holder.txtTitle.setText("" + myCart.getProductName());
            holder.txtPtype.setText("" + myCart.getProductType());

            int qrt = helper.getCard(myCart.getAttributeId());
            if (qrt != -1) {
                count[0] = qrt;
                holder.txtcount.setText("" + count[0]);
                holder.txtcount.setVisibility(View.VISIBLE);
                holder.imgMins.setVisibility(View.VISIBLE);

            } else {
                holder.txtcount.setVisibility(View.INVISIBLE);
                holder.imgMins.setVisibility(View.INVISIBLE);
            }
            double ress = (Double.parseDouble(myCart.getProductPrice()) / 100.0f) * myCart.getDiscount();
            ress = Double.parseDouble(myCart.getProductPrice()) - ress;
            double temp = ress * qrt;
            totalAmount[0] = totalAmount[0] + temp;
            holder.imgMins.setOnClickListener(v -> {
                sessionManager.setIntData(coupon, 0);

                count[0] = Integer.parseInt(holder.txtcount.getText().toString());
                count[0] = count[0] - 1;
                if (count[0] <= 0) {
                    holder.txtcount.setVisibility(View.INVISIBLE);
                    holder.imgMins.setVisibility(View.INVISIBLE);
                    holder.txtcount.setText("" + count[0]);
                    helper.deleteRData(myCart.getAttributeId());
                    mData.remove(myCart);

                    totalAmount[0] = totalAmount[0] - Double.parseDouble(myCart.getProductPrice());
                    Toast.makeText(CartActivity.this, "" + myCart.getProductName() + "  is Remove", Toast.LENGTH_LONG).show();

                    notifyDataSetChanged();
                    updateItem();
                } else {
                    holder.txtcount.setVisibility(View.VISIBLE);
                    holder.txtcount.setText("" + count[0]);
                    myCart.setQty(String.valueOf(count[0]));
                    totalAmount[0] = totalAmount[0] - Double.parseDouble(myCart.getProductPrice());
                    helper.insertData(myCart);
                    notifyDataSetChanged();
                    updateItem();
                }
            });
            holder.imgPlus.setOnClickListener(v -> {
                if (Integer.parseInt(myCart.getQty()) < Integer.parseInt(sessionManager.getStringData(itemlimit))) {
                    sessionManager.setIntData(coupon, 0);
                    holder.txtcount.setVisibility(View.VISIBLE);
                    holder.imgMins.setVisibility(View.VISIBLE);
                    count[0] = Integer.parseInt(holder.txtcount.getText().toString());
                    totalAmount[0] = totalAmount[0] + Double.parseDouble(myCart.getProductPrice());
                    count[0] = count[0] + 1;
                    holder.txtcount.setText("" + count[0]);
                    myCart.setQty(String.valueOf(count[0]));
                    helper.insertData(myCart);
                    updateItem();
                } else {
                    Toast.makeText(mContext, "Exceeded the limit", Toast.LENGTH_SHORT).show();

                }
            });
            holder.imgDelete.setOnClickListener(v -> {
                sessionManager.setIntData(coupon, 0);
                AlertDialog myDelete = new AlertDialog.Builder(CartActivity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete?")
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton("Delete", (dialog, whichButton) -> {
                            Log.d("sdj", "" + whichButton);
                            dialog.dismiss();
                            totalAmount[0] = totalAmount[0] - Double.parseDouble(myCart.getProductPrice());
                            helper.deleteRData(myCart.getAttributeId());
                            mData.remove(myCart);
                            updateItem();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> {
                            Log.d("sdj", "" + which);
                            dialog.dismiss();
                        })
                        .create();
                myDelete.show();
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.img_icon)
            ImageView imgIcon;
            @BindView(R.id.txt_title)
            TextView txtTitle;
            @BindView(R.id.txt_dscount)
            TextView txtDscount;
            @BindView(R.id.txt_price)
            TextView txtPrice;
            @BindView(R.id.img_delete)
            ImageView imgDelete;
            @BindView(R.id.img_mins)
            LinearLayout imgMins;
            @BindView(R.id.txtcount)
            TextView txtcount;
            @BindView(R.id.img_plus)
            LinearLayout imgPlus;
            @BindView(R.id.lvl_addremove)
            LinearLayout lvlAddremove;
            @BindView(R.id.txt_offer)
            TextView txtOffer;
            @BindView(R.id.txt_ptype)
            TextView txtPtype;
            @BindView(R.id.lvl_offer)
            LinearLayout lvlOffer;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }

    }

    double tempWallet = 0;

    public void updateItem() {
        Cursor res = databaseHelper.getAllData();
        subtotal = 0;
        total = 0;
        double ress = 0;
        int totalItem = 0;

        if (res.getCount() == 0) {

            lvlNotFound.setVisibility(View.VISIBLE);
            lvlMain.setVisibility(View.GONE);

        }
        while (res.moveToNext()) {
            MyCart rModel = new MyCart();
            rModel.setProductPrice(res.getString(6));
            rModel.setQty(res.getString(7));
            rModel.setDiscount(res.getInt(8));
            if (rModel.getDiscount() != 0) {
                ress = (Double.parseDouble(rModel.getProductPrice()) * rModel.getDiscount()) / 100;
                ress = Double.parseDouble(rModel.getProductPrice()) - ress;
            } else {
                ress = Double.parseDouble(rModel.getProductPrice());
            }

            double temp = Integer.parseInt(rModel.getQty()) * ress;
            subtotal = subtotal + temp;
            totalItem = totalItem + Integer.parseInt(res.getString(6));

        }
        txtDCharge.setText(sessionManager.getStringData(currency) + sessionManager.getFloatData(dcharge));

        txtItemTotal.setText(sessionManager.getStringData(currency) + new DecimalFormat(hase).format(subtotal));


        total = subtotal + sessionManager.getFloatData(dcharge);
        if (sessionManager.getIntData(coupon) != 0) {
            imgCoupnCode.setImageResource(R.drawable.ic_cancel_coupon);
        } else {
            imgCoupnCode.setImageResource(R.drawable.ic_apply_coupon);

        }
        txtDiscount.setText(sessionManager.getStringData(currency) + sessionManager.getIntData(coupon));
        total = total - sessionManager.getIntData(coupon);


        if (sessionManager.getIntData(wallet) != 0) {
            lvlWallet.setVisibility(View.VISIBLE);
            if (isWallet) {
                if (sessionManager.getIntData(SessionManager.wallet) <= total) {

                    total = total - sessionManager.getIntData(SessionManager.wallet);
                    txtWallet.setText(sessionManager.getStringData(SessionManager.currency) + "0");
                    tempWallet = sessionManager.getIntData(SessionManager.wallet);
                } else {
                    tempWallet = sessionManager.getIntData(SessionManager.wallet) - total;
                    txtWallet.setText(sessionManager.getStringData(SessionManager.currency) + tempWallet);
                    tempWallet = total;
                    total = 0;

                }
            } else {
                txtWallet.setText(sessionManager.getStringData(SessionManager.currency) + sessionManager.getIntData(SessionManager.wallet));
            }
        } else {
            lvlWallet.setVisibility(View.GONE);
        }
        txtToPay.setText(sessionManager.getStringData(currency) + new DecimalFormat(hase).format(total));

    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                CartValidate cartdataset = gson.fromJson(result.toString(), CartValidate.class);
                if (cartdataset.getResult().equalsIgnoreCase("true")) {
                    if (!cartdataset.getAddressList().isEmpty()) {
                        if (!cartdataset.getCartValidateData().isEmpty()) {
                            for (int i = 0; i < cartdataset.getCartValidateData().size(); i++) {
                                databaseHelper.deleteRData(cartdataset.getCartValidateData().get(i).getAid());
                            }
                        }
                        cartdataset();
                        txtAType.setText("" + cartdataset.getAddressList().get(sessionManager.getIntData(position)).getType());
                        txtAddress.setText(cartdataset.getAddressList().get(sessionManager.getIntData(position)).getHno() + "," + cartdataset.getAddressList().get(sessionManager.getIntData(position)).getLandmark() + "," + cartdataset.getAddressList().get(sessionManager.getIntData(position)).getAddress());
                        sessionManager.setFloatData(dcharge, Float.parseFloat(cartdataset.getAddressList().get(sessionManager.getIntData(position)).getDeliveryCharge()));
                        sessionManager.setStringData(address, cartdataset.getAddressList().get(sessionManager.getIntData(position)).getHno() + "," + cartdataset.getAddressList().get(sessionManager.getIntData(position)).getLandmark() + "," + cartdataset.getAddressList().get(sessionManager.getIntData(position)).getAddress());
                        getPayment();
                    } else {
                        startActivity(new Intent(CartActivity.this, AddressActivity.class));
                        finish();
                    }
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Payment payment = gson.fromJson(result.toString(), Payment.class);
                paymentList = payment.getData();
            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                RestResponse restResponse = gson.fromJson(result.toString(), RestResponse.class);
                if (restResponse.getResult().equalsIgnoreCase("true")) {
                    ISORDER = true;
                    sessionManager.setIntData(wallet, Integer.parseInt(restResponse.getWallet()));
                    Intent intent = new Intent(this, CompleOrderActivity.class);
                    startActivity(intent);
                    finish();
                    databaseHelper.deleteCard();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.img_coopncode, R.id.img_wallet, R.id.txt_changeadress, R.id.btn_proceed, R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_coopncode:
                if (sessionManager.getIntData(coupon) != 0) {
                    imgCoupnCode.setImageResource(R.drawable.ic_cancel_coupon);
                    sessionManager.setIntData(coupon, 0);
                    updateItem();
                } else {

                    int temp = (int) Math.round(total);
                    startActivity(new Intent(CartActivity.this, CoupunActivity.class).putExtra("amount", temp));
                }

                break;
            case R.id.img_wallet:
                if (isWallet) {
                    isWallet = false;
                    imgWallet.setImageResource(R.drawable.ic_inactive_toggle);

                } else {
                    imgWallet.setImageResource(R.drawable.ic_active_toggle);
                    isWallet = true;
                }
                updateItem();
                break;
            case R.id.txt_changeadress:
                startActivity(new Intent(CartActivity.this, AddressActivity.class));
                break;
            case R.id.btn_proceed:
                int temtoal = (int) Math.round(subtotal);
                if (Integer.parseInt(sessionManager.getStringData(minmumorder)) <= temtoal) {
                    if (total == 0) {
                        paymentId = "5";
                        new AsyncTaskRunner().execute("");

                    } else {
                        bottonPaymentList();
                    }

                } else {
                    Toast.makeText(this, "Minimum order value of " + sessionManager.getStringData(currency) + " " + sessionManager.getStringData(minmumorder), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_back:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null) {
            if (AddressActivity.getInstance() != null && changeAddress) {
                changeAddress = false;
                jsoncreate();
            } else {
                updateItem();
            }
        }

        if (paymentsucsses == 1) {
            paymentsucsses = 0;
            new AsyncTaskRunner().execute("0");

        }
    }

    public void bottonPaymentList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_payment, null);
        LinearLayout listView = sheetView.findViewById(R.id.lvl_list);
        TextView txtTotal = sheetView.findViewById(R.id.txt_total);
        txtTotal.setText("item total " + sessionManager.getStringData(currency) + total);
        for (int i = 0; i < paymentList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(CartActivity.this);
            PaymentItem paymentItem = paymentList.get(i);
            View view = inflater.inflate(R.layout.custome_paymentitem, null);
            ImageView imageView = view.findViewById(R.id.img_icon);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtSubtitel = view.findViewById(R.id.txt_subtitel);
            txtTitle.setText("" + paymentList.get(i).getmTitle());
            txtSubtitel.setText("" + paymentList.get(i).getSubtitle());
            Glide.with(CartActivity.this).load(APIClient.baseUrl + "/" + paymentList.get(i).getmImg()).thumbnail(Glide.with(CartActivity.this).load(R.drawable.ezgifresize)).into(imageView);
            int finalI = i;
            view.setOnClickListener(v -> {
                paymentId = paymentList.get(finalI).getmId();
                try {
                    switch (paymentList.get(finalI).getmTitle()) {
                        case "Razorpay":
                            int temtoal = (int) Math.round(total);
                            startActivity(new Intent(CartActivity.this, RazerpayActivity.class).putExtra("amount", temtoal).putExtra("detail", paymentItem));
                            break;
                        case "Cash On Delivery":
                            new AsyncTaskRunner().execute("");
                            break;
                        case "Paypal":
                            startActivity(new Intent(CartActivity.this, PaypalActivity.class).putExtra("amount", total).putExtra("detail", paymentItem));
                            break;
                        case "Stripe":
                            startActivity(new Intent(CartActivity.this, StripPaymentActivity.class).putExtra("amount", total).putExtra("detail", paymentItem));
                            break;
                        case "PayStack":
                            int temtoal1 = (int) Math.round(total);
                            startActivity(new Intent(CartActivity.this, PaystackActivity.class).putExtra("amount", temtoal1).putExtra("detail", paymentItem));
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            listView.addView(view);
        }
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, JSONArray> {
        String storeID = "0";

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray jsonArray = new JSONArray();
            Cursor res = databaseHelper.getAllData();
            while (res.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("title", res.getString(2));
                    jsonObject.put("image", res.getString(3));
                    jsonObject.put("type", res.getString(10));
                    jsonObject.put("cost", res.getString(6));
                    jsonObject.put("qty", res.getString(7));
                    jsonObject.put("discount", res.getString(8));
                    jsonObject.put("attribute_id", res.getString(9));
                    jsonArray.put(jsonObject);
                    storeID = res.getString(11);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }


        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            // execution of result of Long time consuming operation
            orderPlace(jsonArray, storeID);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    public void orderPlace(JSONArray jsonArray, String store) {
        custPrograssbar.prograssCreate(CartActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("p_method_id", paymentId);
            jsonObject.put("full_address", sessionManager.getStringData(address));
            jsonObject.put("d_charge", sessionManager.getFloatData(dcharge));
            jsonObject.put("cou_id", sessionManager.getIntData(couponid));
            jsonObject.put("cou_amt", sessionManager.getIntData(coupon));
            jsonObject.put("store_id", store);
            jsonObject.put("transaction_id", tragectionID);
            jsonObject.put("product_total", total);
            jsonObject.put("product_subtotal", subtotal);
            jsonObject.put("a_note", edCustom.getText().toString());
            jsonObject.put("wall_amt", tempWallet);

            jsonObject.put("ProductData", jsonArray);
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getOrderNow(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}