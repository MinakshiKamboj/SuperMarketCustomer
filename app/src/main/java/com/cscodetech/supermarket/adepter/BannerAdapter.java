package com.cscodetech.supermarket.adepter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cscodetech.supermarket.R;
import com.cscodetech.supermarket.fragment.SubCategoryFragment;
import com.cscodetech.supermarket.model.Banner;
import com.cscodetech.supermarket.retrofit.APIClient;
import com.cscodetech.supermarket.ui.HomeActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
    private Context context;
    private List<Banner> mBanner;
    public BannerAdapter(Context context, List<Banner> mBanner) {
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

        holder.imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBanner.get(position).getCid().equalsIgnoreCase("0")){

                }else {
                    Bundle args = new Bundle();
                    int pp =Integer.parseInt(mBanner.get(position).getCid())-1;
                    args.putInt("position", pp);
                    Fragment fragment = new SubCategoryFragment();
                    fragment.setArguments(args);
                    HomeActivity.getInstance().openFragment(fragment);
                }

            }
        });

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