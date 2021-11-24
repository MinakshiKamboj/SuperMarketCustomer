package com.cscodetech.supermarket.adepter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.model.Medicine;
import com.cscodetech.supermarket.model.ProductPrice;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.utiles.DatabaseHelper;
import com.cscodetech.supermarket.utiles.MyCart;
import com.cscodetech.supermarket.utiles.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cscodetech.supermarket.utiles.SessionManager.currency;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<Medicine> medicineList;
    private RecyclerTouchListener listener;
    SessionManager sessionManager;
    DatabaseHelper helper;

    public interface RecyclerTouchListener {
        public void onClickProductItem(String titel, Medicine medicine);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_icon)
        ImageView imgIcon;
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.priceoofer)
        TextView priceoofer;
        @BindView(R.id.lvl_click)
        LinearLayout lvlClick;
        @BindView(R.id.txt_offer)
        TextView txtOffer;
        @BindView(R.id.lvl_offer)
        LinearLayout lvlOffer;

        @BindView(R.id.lvl_cart)
        LinearLayout lvlCart;

        @BindView(R.id.spinner)
        Spinner spinner;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ProductAdapter(Context mContext, List<Medicine> medicineList, final RecyclerTouchListener listener) {
        this.mContext = mContext;
        this.medicineList = medicineList;
        this.listener = listener;
        sessionManager = new SessionManager(mContext);
        helper = new DatabaseHelper(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        if (medicine.getProductInfo().get(0).getProductDiscount() == 0) {
            holder.lvlOffer.setVisibility(View.GONE);
            holder.priceoofer.setVisibility(View.GONE);
            holder.price.setText(sessionManager.getStringData(currency) + medicine.getProductInfo().get(0).getProductPrice());
        } else {
            DecimalFormat format = new DecimalFormat("0.#");
            holder.txtOffer.setText(format.format(medicine.getProductInfo().get(0).getProductDiscount()) + "% \nOFF");
            double res = (Double.parseDouble(medicine.getProductInfo().get(0).getProductPrice()) / 100.0f) * medicine.getProductInfo().get(0).getProductDiscount();
            res = Double.parseDouble(medicine.getProductInfo().get(0).getProductPrice()) - res;
            holder.price.setText(sessionManager.getStringData(currency) + new DecimalFormat("##.##").format(res));
            holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.priceoofer.setText(sessionManager.getStringData(currency) + medicine.getProductInfo().get(0).getProductPrice());

        }
        holder.txtTitle.setText(medicine.getProductName());
        Glide.with(mContext).load(APIClient.baseUrl + "/" + medicine.getProductImage().get(0)).thumbnail(Glide.with(mContext).load(R.drawable.ezgifresize)).centerCrop().into(holder.imgIcon);
        holder.lvlClick.setOnClickListener(v -> listener.onClickProductItem("category.getCatname()", medicine));
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < medicine.getProductInfo().size(); i++) {
            arrayList.add(medicine.getProductInfo().get(i).getProductType());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout, arrayList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        holder.spinner.setAdapter(dataAdapter);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (medicine.getProductInfo().get(position).getProductDiscount() == 0) {
                    holder.lvlOffer.setVisibility(View.GONE);
                    holder.priceoofer.setVisibility(View.GONE);
                    holder.price.setText(sessionManager.getStringData(currency) + medicine.getProductInfo().get(position).getProductPrice());
                } else {
                    holder.lvlOffer.setVisibility(View.VISIBLE);
                    holder.priceoofer.setVisibility(View.VISIBLE);
                    DecimalFormat format = new DecimalFormat("0.#");
                    holder.txtOffer.setText(format.format(medicine.getProductInfo().get(position).getProductDiscount()) + "% \nOFF");
                    double res = (Double.parseDouble(medicine.getProductInfo().get(position).getProductPrice()) / 100.0f) * medicine.getProductInfo().get(position).getProductDiscount();
                    res = Double.parseDouble(medicine.getProductInfo().get(position).getProductPrice()) - res;
                    holder.price.setText(sessionManager.getStringData(currency) + new DecimalFormat("##.##").format(res));
                    holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.priceoofer.setText(sessionManager.getStringData(currency) + medicine.getProductInfo().get(position).getProductPrice());

                }
                setJoinPlayrList(holder.lvlCart, medicine, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setJoinPlayrList(holder.lvlCart, medicine, 0);
    }

    private void setJoinPlayrList(LinearLayout lnrView, Medicine medicine, int pos) {
        lnrView.removeAllViews();

        ProductPrice productPrice = medicine.getProductInfo().get(pos);
        final int[] count = {0};
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custome_prize, null);
        TextView txtcount = view.findViewById(R.id.txtcount);
        LinearLayout txtOutstock = view.findViewById(R.id.txt_outstock);
        LinearLayout lvlAddremove = view.findViewById(R.id.lvl_addremove);
        LinearLayout lvlAddcart = view.findViewById(R.id.lvl_addcart);
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

        if (productPrice.getProductInStock().equalsIgnoreCase("1")) {
            txtOutstock.setVisibility(View.VISIBLE);
            lvlAddremove.setVisibility(View.GONE);
            lvlAddcart.setVisibility(View.GONE);

        } else {
            int qrt = helper.getCard(myCart.getAttributeId());
            if (qrt != -1) {
                count[0] = qrt;
                txtcount.setText("" + count[0]);
                lvlAddremove.setVisibility(View.VISIBLE);
                lvlAddcart.setVisibility(View.GONE);
            } else {
                lvlAddremove.setVisibility(View.GONE);
                lvlAddcart.setVisibility(View.VISIBLE);
            }
        }


        imgMins.setOnClickListener(v -> {
            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] - 1;
            if (count[0] <= 0) {
                txtcount.setText("" + count[0]);
                lvlAddremove.setVisibility(View.GONE);
                lvlAddcart.setVisibility(View.VISIBLE);
                helper.deleteRData(myCart.getAttributeId());
            } else {
                txtcount.setVisibility(View.VISIBLE);
                txtcount.setText("" + count[0]);
                myCart.setQty(String.valueOf(count[0]));
            }
        });
        imgPlus.setOnClickListener(v -> {

            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] + 1;

            myCart.setQty(String.valueOf(count[0]));
            if (helper.insertData(myCart)) {

                txtcount.setText("" + count[0]);

            }
        });
        lvlAddcart.setOnClickListener(v -> {

            count[0] = Integer.parseInt(txtcount.getText().toString());
            count[0] = count[0] + 1;
            myCart.setQty(String.valueOf(count[0]));
            if (helper.insertData(myCart)) {
                txtcount.setText("" + count[0]);
                lvlAddcart.setVisibility(View.GONE);
                lvlAddremove.setVisibility(View.VISIBLE);
            }
        });
        lnrView.addView(view);
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }
}