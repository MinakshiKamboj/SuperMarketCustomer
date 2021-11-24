package com.cscodetech.supermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.Payment;
import com.cscodetech.supermarket.model.PaymentItem;
import com.cscodetech.supermarket.model.RestResponse;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.model.Wallet;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
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

import static com.cscodetech.supermarket.utiles.Utility.paymentsucsses;


public class WalletActivity extends AppCompatActivity implements GetResult.MyListener {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.lvl_histry)
    LinearLayout lvlHistry;
    @BindView(R.id.img_histry)
    ImageView imgHistry;
    @BindView(R.id.ed_amount)
    EditText edAmount;
    @BindView(R.id.txt_one)
    TextView txtOne;
    @BindView(R.id.txt_two)
    TextView txtTwo;
    @BindView(R.id.txt_tree)
    TextView txtTree;
    @BindView(R.id.txt_fore)
    TextView txtFore;
    @BindView(R.id.btn_addmoney)
    TextView btnAddmoney;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    List<PaymentItem> paymentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(WalletActivity.this);
        user = sessionManager.getUserDetails("");
        getWalletHistri();
    }

    private void getPayment() {
        custPrograssbar.prograssCreate(WalletActivity.this);

        JSONObject jsonObject = new JSONObject();
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getPaymentList(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    private void updateWallet(String w) {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("uid", user.getId());
            jsonObject.put("wallet", w);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().walletUp(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }

    private void getWalletHistri() {
        custPrograssbar.prograssCreate(WalletActivity.this);

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
        getResult.callForLogin(call, "3");

    }

    public void setData() {
        edAmount.setText("100");
    }

        @OnClick({R.id.btn_addmoney, R.id.lvl_histry, R.id.img_back, R.id.txt_one, R.id.txt_two, R.id.txt_tree, R.id.txt_fore})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_addmoney:
                    getPayment();
                    break;
            case R.id.lvl_histry:
                startActivity(new Intent(WalletActivity.this, WallateHistryActivity.class));
                break;
            case R.id.img_back:
                finish();
                break;

            case R.id.txt_one:
                edAmount.setText("10");
                break;
            case R.id.txt_two:
                edAmount.setText("100");
                break;
            case R.id.txt_tree:
                edAmount.setText("200");
                break;
            case R.id.txt_fore:
                edAmount.setText("250");
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
                Payment payment = gson.fromJson(result.toString(), Payment.class);
                paymentList = new ArrayList<>();
                for (int i = 0; i < payment.getData().size(); i++) {
                    if (payment.getData().get(i).getwShow().equalsIgnoreCase("1")) {
                        paymentList.add(payment.getData().get(i));
                    }
                }
                bottonPaymentList();
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    recreate();
                } else {
                    Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_LONG).show();
                }
            } else if (callNo.equalsIgnoreCase("3")) {

                    Gson gson = new Gson();
                    Wallet walletHistry = gson.fromJson(result.toString(), Wallet.class);
                    if (walletHistry.getResult().equalsIgnoreCase("true")) {
                        sessionManager.setIntData(SessionManager.wallet, Integer.parseInt(walletHistry.getWallets()));
                        txtTotal.setText(sessionManager.getStringData(SessionManager.currency) + walletHistry.getWallets());
                    }


            }
        } catch (Exception e) {
e.toString();
        }
    }

    public void bottonPaymentList() {
        double total = Double.parseDouble(edAmount.getText().toString());
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_payment, null);
        LinearLayout listView = sheetView.findViewById(R.id.lvl_list);
        TextView txtTotal = sheetView.findViewById(R.id.txt_total);
        txtTotal.setText("item total " + sessionManager.getStringData(SessionManager.currency) + total);
        for (int i = 0; i < paymentList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            PaymentItem paymentItem = paymentList.get(i);
            View view = inflater.inflate(R.layout.custome_paymentitem, null);
            ImageView imageView = view.findViewById(R.id.img_icon);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtSubtitel = view.findViewById(R.id.txt_subtitel);
            txtTitle.setText("" + paymentList.get(i).getmTitle());
            txtSubtitel.setText("" + paymentList.get(i).getSubtitle());
            Glide.with(this).load(APIClient.baseUrl + "/" + paymentList.get(i).getmImg()).thumbnail(Glide.with(this).load(R.drawable.ezgifresize)).into(imageView);
            int finalI = i;
            view.setOnClickListener(v -> {

                try {
                    switch (paymentList.get(finalI).getmTitle()) {
                        case "Razorpay":
                            int temtoal = (int) Math.round(total);
                            startActivity(new Intent(this, RazerpayActivity.class).putExtra("amount", temtoal).putExtra("detail", paymentItem));
                            break;

                        case "Paypal":
                            startActivity(new Intent(this, PaypalActivity.class).putExtra("amount", total).putExtra("detail", paymentItem));
                            break;
                        case "Stripe":
                            startActivity(new Intent(this, StripPaymentActivity.class).putExtra("amount", total).putExtra("detail", paymentItem));
                            break;
                        case "PayStack":
                            int temtoal1 = (int) Math.round(total);
                            startActivity(new Intent(this, PaystackActivity.class).putExtra("amount", temtoal1).putExtra("detail", paymentItem));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (paymentsucsses == 1) {
            paymentsucsses = 0;
            updateWallet(edAmount.getText().toString());
        }

    }
}