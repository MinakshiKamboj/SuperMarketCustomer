package com.cscodetech.supermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.Codes;
import com.cscodetech.supermarket.model.CountryCode;
import com.cscodetech.supermarket.model.Login;
import com.cscodetech.supermarket.model.Register;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.cscodetech.supermarket.utiles.Utility;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class LoginActivity extends RootActivity implements GetResult.MyListener {

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.ed_mobile)
    EditText edMobile;
    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.txt_olduser)
    TextView txtSingUp;
    @BindView(R.id.btn_proceed)
    TextView btnProceed;
    @BindView(R.id.lvl_singup)
    LinearLayout lvlSingup;
    @BindView(R.id.txt_newuser)
    TextView txtLogin;
    @BindView(R.id.lvl_login)
    LinearLayout lvlLogin;
    List<CountryCode> cCodes = new ArrayList<>();
    String codeSelect;
    String codeSelectF;
    boolean isNewuser = true;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    String mobile="mobile";
    String jsons="application/json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(LoginActivity.this);
        String sourceString = "Have a <b>" + "Email/Password" + "</b> " + "Account?";
        String sourceCreate = "<b>Sign Up?</b>";
        txtSingUp.setText(Html.fromHtml(sourceString));
        txtLogin.setText(Html.fromHtml(sourceCreate));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codeSelect = cCodes.get(position).getCcode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Test","part");
            }
        });
        getContryCode();

    }

    private void getContryCode() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        RequestBody bodyRequest = RequestBody.create(MediaType.parse(jsons), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCountry(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    private void chackUser() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(mobile, edMobile.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse(jsons), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCheckMobile(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }

    private void loginUser() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(mobile, edEmail.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse(jsons), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().login(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "3");

    }
    private void chackUserForgot(String mobile) {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobile);
        } catch (Exception e) {
            e.printStackTrace();

        }

        RequestBody bodyRequest = RequestBody.create(MediaType.parse(jsons), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCheckMobile(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "4");

    }

    @OnClick({R.id.txt_olduser, R.id.btn_proceed, R.id.txt_newuser, R.id.txt_forgotclick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_olduser:
                txtSingUp.setVisibility(View.GONE);
                lvlSingup.setVisibility(View.GONE);
                lvlLogin.setVisibility(View.VISIBLE);
                isNewuser = false;
                break;
            case R.id.btn_proceed:
                if (isNewuser) {
                    if (!TextUtils.isEmpty(edMobile.getText().toString())) {
                        chackUser();
                    } else {
                        edMobile.setError("Enter Mobile Number");
                    }
                } else {
                    if (TextUtils.isEmpty(edEmail.getText().toString())) {
                        edEmail.setError("Enter Mobile / Email");
                    } else if (TextUtils.isEmpty(edPassword.getText().toString())) {
                        edPassword.setError("Enter Password");
                    } else {
                        loginUser();
                    }
                }
                break;
            case R.id.txt_newuser:
                isNewuser = true;
                txtSingUp.setVisibility(View.VISIBLE);
                lvlSingup.setVisibility(View.VISIBLE);
                lvlLogin.setVisibility(View.GONE);
                break;
            case R.id.txt_forgotclick:
                bottonVelidation();
                break;
            default:
                break;
        }
    }

    List<String> countryCodeList;

    @Override
    public void callback(JsonObject result, String callNo) {

        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {

                Gson gson = new Gson();
                Codes contry = gson.fromJson(result.toString(), Codes.class);
                cCodes = contry.getCountryCode();
                countryCodeList = new ArrayList<>();
                for (int i = 0; i < contry.getCountryCode().size(); i++) {
                    CountryCode countryCode = contry.getCountryCode().get(i);
                    if (countryCode.getStatus().equalsIgnoreCase("1")) {
                        this.countryCodeList.add(countryCode.getCcode());
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinnercode_layout, countryCodeList);
                dataAdapter.setDropDownViewResource(R.layout.spinnercode_layout);
                spinner.setAdapter(dataAdapter);
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Register register = gson.fromJson(result.toString(), Register.class);
                if (register.getResult().equalsIgnoreCase("true")) {
                    Utility.isvarification = 1;
                    startActivity(new Intent(LoginActivity.this, VerifyPhoneActivity.class).putExtra("code", codeSelect).putExtra("phone", edMobile.getText().toString()));


                } else {
                    Toast.makeText(LoginActivity.this, register.getResponseMsg(), Toast.LENGTH_SHORT).show();
                    edEmail.setText("" + edMobile.getText().toString());
                    isNewuser = false;
                    txtSingUp.setVisibility(View.GONE);
                    lvlSingup.setVisibility(View.GONE);
                    lvlLogin.setVisibility(View.VISIBLE);
                }


            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                Login login = gson.fromJson(result.toString(), Login.class);
                Toast.makeText(LoginActivity.this, login.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (login.getResult().equalsIgnoreCase("true")) {
                    OneSignal.sendTag("userid", login.getUserLogin().getId());
                    sessionManager.setUserDetails("", login.getUserLogin());
                    sessionManager.setBooleanData(SessionManager.login, true);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }else  if (callNo.equalsIgnoreCase("4")) {
                Gson gson = new Gson();
                Register register = gson.fromJson(result.toString(), Register.class);
                if (register.getResult().equalsIgnoreCase("true")) {
                    Toast.makeText(LoginActivity.this, register.getResponseMsg(), Toast.LENGTH_SHORT).show();

                } else {
                    Utility.isvarification = 0;
                    startActivity(new Intent(LoginActivity.this, VerifyPhoneActivity.class).putExtra("code", codeSelectF).putExtra("phone", edMobiles.getText().toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    EditText edMobiles;
    public void bottonVelidation() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_demo_layout, null);
        Spinner spinnerD = sheetView.findViewById(R.id.spinner);
        edMobiles = sheetView.findViewById(R.id.ed_mobile);
        TextView txtSubmit = sheetView.findViewById(R.id.btn_submit);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinnercode_layout, countryCodeList);
        dataAdapter.setDropDownViewResource(R.layout.spinnercode_layout);
        spinnerD.setAdapter(dataAdapter);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        spinnerD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codeSelectF = cCodes.get(position).getCcode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Test","part");

            }
        });
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edMobiles.getText().toString().isEmpty()) {
                    chackUserForgot(edMobiles.getText().toString());
                } else {
                    edMobile.setError("Enter Mobile");
                }
            }
        });


    }
}