package com.cscodetech.supermarket.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.OrderP;
import com.cscodetech.supermarket.model.User;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.retrofit.GetResult;
import com.cscodetech.supermarket.utiles.CustPrograssbar;
import com.cscodetech.supermarket.utiles.SessionManager;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.android.material.appbar.AppBarLayout;
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

public class GenieOrderDetailsActivity extends RootActivity implements GetResult.MyListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_actiontitle)
    TextView txtActionTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.txt_summary)
    TextView txtSummary;
    @BindView(R.id.txt_item)
    TextView txtItem;
    @BindView(R.id.txt_orderid)
    TextView txtOrderId;
    @BindView(R.id.txt_orderstatus)
    TextView txtOrderStatus;
    @BindView(R.id.txt_orderdate)
    TextView txtOrderDate;
    @BindView(R.id.txt_orderddate)
    TextView txtOrderDDate;
    @BindView(R.id.txt_paymentmethod)
    TextView txtPaymentMethod;

    @BindView(R.id.txt_deliverycharge)
    TextView txtDeliveryCharge;
    @BindView(R.id.txt_discription)
    TextView txtDiscription;
    @BindView(R.id.txt_pickupaddress)
    TextView txtPickupaddress;
    @BindView(R.id.txt_dmobile)
    TextView txtDmobile;
    @BindView(R.id.txt_pmobile)
    TextView txtPmobile;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_aditionalinfo)
    TextView txtAdditionInfo;
    @BindView(R.id.lvl_aditional)
    LinearLayout lvlAditional;


    StaggeredGridLayoutManager gridLayoutManager;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    String oid;

    List<String> mOrderProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genie_order_details);
        ButterKnife.bind(this);
        oid = getIntent().getStringExtra("oid");
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(GenieOrderDetailsActivity.this);
        user = sessionManager.getUserDetails("");
        gridLayoutManager = new StaggeredGridLayoutManager(1, 1);


        getOrderHistiry();
    }

    public void orderTrackData() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_ordertrack, null);
        RecyclerView myRecyclertemp = sheetView.findViewById(R.id.my_recyclertemp);
        TextView txtTitel = sheetView.findViewById(R.id.txt_titel);
        txtTitel.setText("Order Tracking");
        myRecyclertemp.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(mDataList, true);
        myRecyclertemp.setAdapter(mTimeLineAdapter);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    public void orderItem() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_ordertrack, null);
        TextView txtTitel = sheetView.findViewById(R.id.txt_titel);
        txtTitel.setVisibility(View.GONE);
        RecyclerView myRecyclertemp = sheetView.findViewById(R.id.my_recyclertemp);
        myRecyclertemp.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemAdp itemAdp = new ItemAdp(GenieOrderDetailsActivity.this, mOrderProductList);
        myRecyclertemp.setAdapter(itemAdp);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }


    private void getOrderHistiry() {
        custPrograssbar.prograssCreate(GenieOrderDetailsActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("order_id", oid);
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getGenieOrderHistory(bodyRequest);
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
                OrderP orderP = gson.fromJson(result.toString(), OrderP.class);
                if (orderP.getResult().equalsIgnoreCase("true")) {
                    txtOrderId.setText("" + oid);
                    txtOrderStatus.setText("" + orderP.getOrderProductList().get(0).getOrderStatus());
                    txtOrderDate.setText("" + orderP.getOrderProductList().get(0).getOrderDate());
                    txtPaymentMethod.setText("" + orderP.getOrderProductList().get(0).getPMethodName());

                    txtDeliveryCharge.setText(sessionManager.getStringData(SessionManager.currency) + orderP.getOrderProductList().get(0).getDeliveryCharge());
                    txtDiscription.setText(orderP.getOrderProductList().get(0).getDescription());
                    txtPickupaddress.setText(sessionManager.getStringData(SessionManager.currency) + orderP.getOrderProductList().get(0).getCustomerPaddress());
                    txtAddress.setText("" + orderP.getOrderProductList().get(0).getCustomerDaddress());
                    txtPmobile.setText("Mobile : " + orderP.getOrderProductList().get(0).getCustomerPmobile());
                    txtDmobile.setText("Mobile : " + orderP.getOrderProductList().get(0).getCustomerdmobile());

                    setDataListItems(Integer.parseInt(orderP.getOrderProductList().get(0).getOrderflowid()), orderP.getOrderProductList().get(0).getCommentReject());


                    mOrderProductList = orderP.getOrderProductList().get(0).getPhotos();

                }
            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());
        }

    }

    @OnClick({R.id.img_back, R.id.txt_summary, R.id.txt_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_summary:


                orderTrackData();
                break;
            case R.id.txt_item:
                orderItem();
                break;
            default:
                break;
        }
    }


    public class ItemAdp extends RecyclerView.Adapter<ItemAdp.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        Context mContext;


        public ItemAdp(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mContext = context;
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.custome_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {

            Glide.with(mContext).load(APIClient.baseUrl + "/" + mData.get(i)).thumbnail(Glide.with(mContext).load(R.drawable.ezgifresize)).into(holder.imgIcon);

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.img_icon)
            ImageView imgIcon;


            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }


    }

    public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

        private List<TimeLineModel> mFeedList;
        private Context mContext;

        boolean mWithLinePadding;
        LayoutInflater mLayoutInflater;

        public TimeLineAdapter(List<TimeLineModel> feedList, boolean withLinePadding) {
            mFeedList = feedList;
            mWithLinePadding = withLinePadding;
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position, getItemCount());
        }

        @Override
        public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            mLayoutInflater = LayoutInflater.from(mContext);
            View view;


            view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);


            return new TimeLineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(TimeLineViewHolder holder, int position) {

            TimeLineModel timeLineModel = mFeedList.get(position);
            holder.txtComment.setText("" + timeLineModel.getComment());
            if (timeLineModel.getStatus() == OrderStatus.INACTIVE) {
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
            } else if (timeLineModel.getStatus() == OrderStatus.ACTIVE) {
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
            } else {
                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_maeker), ContextCompat.getColor(mContext, R.color.colorPrimary));
            }


            holder.mMessage.setText(timeLineModel.getMessage());
        }

        @Override
        public int getItemCount() {
            return (mFeedList != null ? mFeedList.size() : 0);
        }

    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.text_timeline_title)
        TextView mMessage;
        @BindView(R.id.txt_comment)
        TextView txtComment;
        @BindView(R.id.timeline)
        TimelineView mTimelineView;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mTimelineView.initLine(viewType);
        }
    }

    public class TimeLineModel {

        private String mMessage;
        private String comment;

        private OrderStatus mStatus;

        public TimeLineModel() {
        }

        public TimeLineModel(String mMessage, String comment, OrderStatus mStatus) {
            this.mMessage = mMessage;

            this.comment = comment;
            this.mStatus = mStatus;
        }

        public String getMessage() {
            return mMessage;
        }

        public void semMessage(String message) {
            this.mMessage = message;
        }

        public String getComment() {
            return comment;
        }

        public void semComment(String comment) {
            this.comment = comment;
        }


        public OrderStatus getStatus() {
            return mStatus;
        }

        public void setStatus(OrderStatus mStatus) {
            this.mStatus = mStatus;
        }

    }

    public enum OrderStatus {

        COMPLETED,
        ACTIVE,
        INACTIVE;

    }

    private static class VectorDrawableUtils {

        public static Drawable getDrawable(Context context, int drawableResId) {
            Drawable drawable;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                drawable = context.getResources().getDrawable(drawableResId, context.getTheme());
            } else {
                drawable = VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());
            }

            return drawable;
        }

        public static Drawable getDrawable(Context context, int drawableResId, int colorFilter) {
            Drawable drawable = getDrawable(context, drawableResId);
            drawable.setColorFilter(ContextCompat.getColor(context, colorFilter), PorterDuff.Mode.SRC_IN);
            return drawable;
        }

        public static Bitmap getBitmap(Context context, int drawableId) {
            Drawable drawable = getDrawable(context, drawableId);

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    }

    private List<TimeLineModel> mDataList = new ArrayList<>();

    private void setDataListItems(int a, String comment) {
        switch (a) {
            case 0:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.ACTIVE));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));
                break;
            case 1:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.ACTIVE));
                mDataList.add(new TimeLineModel("Cancelled By Store.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));
                break;
            case 2:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));

                mDataList.add(new TimeLineModel("Cancelled By Store.", comment, OrderStatus.ACTIVE));
                break;
            case 3:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.ACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));

                break;
            case 4:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.ACTIVE));

                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));

                break;
            case 5:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.ACTIVE));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.INACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));
                Log.e("hkjhfs", "fjhdskfhskf");
                break;
            case 6:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.ACTIVE));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.INACTIVE));
                break;
            case 7:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Pick up Order.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Complete Order.", "", OrderStatus.ACTIVE));
                break;
            case 8:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Cancelled By You.", "", OrderStatus.ACTIVE));
                break;
            case 9:
                mDataList.add(new TimeLineModel("Waiting For Store Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Accpeted Searching Delivery Boy.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Waiting For Delivery Boy Decision.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Accpeted Order.", "", OrderStatus.COMPLETED));
                mDataList.add(new TimeLineModel("Delivery Boy Cancelled Order.", comment, OrderStatus.ACTIVE));
                break;
            case 10:
                break;
            default:
                break;

        }

    }
}