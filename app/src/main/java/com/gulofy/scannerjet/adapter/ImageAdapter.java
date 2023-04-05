package com.gulofy.scannerjet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.gulofy.scannerjet.R;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;

import me.pqpo.smartcropperlib.view.CropImageView;

public class ImageAdapter extends BannerAdapter<String, ImageAdapter.BannerViewHolder> {

    ArrayList<String> bitmapList;
    Context context;

    public static  CropImageView iv_preview_crop;


        public ImageAdapter(ArrayList<String> list, Context context) {
            super(list);
            bitmapList=list;
            this.context=context;
        }

        @Override
        public int getItemCount() {
           return bitmapList.size();
        }


        @Override
        public BannerViewHolder onCreateHolder(ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_mul_edit_page, viewGroup, false);
            inflate.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            return new BannerViewHolder(inflate);
        }

        @Override
        public void onBindView(final BannerViewHolder bannerViewHolder, final String imageFile, int i, int i2) {
            iv_preview_crop=bannerViewHolder.itemView.findViewById(R.id.iv_preview_crop);
            Glide.with(context).asBitmap().load(imageFile).into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    bannerViewHolder.iv_preview_crop_id.setImageToCrop(bitmap);
                    bannerViewHolder.iv_preview_crop_id.setFullImgCrop();
                }
            });


        }


        class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView img_animate;
            ImageView img_animate_alpha;
            ImageView img_delete;
            RelativeLayout rl_animate_alpha_container;
            RelativeLayout rl_camera;
            FrameLayout rl_cropimage_container;
            RelativeLayout rl_image_container;
            public CropImageView iv_preview_crop_id;


            public BannerViewHolder(View view) {
                super(view);

                this.rl_cropimage_container = (FrameLayout) view.findViewById(R.id.rl_cropimage_container);
                this.img_delete = (ImageView) view.findViewById(R.id.img_delete);
                this.rl_image_container = (RelativeLayout) view.findViewById(R.id.rl_image_container);
                this.rl_camera = (RelativeLayout) view.findViewById(R.id.rl_camera);
                this.rl_animate_alpha_container = (RelativeLayout) view.findViewById(R.id.rl_animate_alpha_container);
                this.img_animate = (ImageView) view.findViewById(R.id.img_animate);
                this.img_animate_alpha = (ImageView) view.findViewById(R.id.img_animate_alpha);
                this.iv_preview_crop_id = (CropImageView) view.findViewById(R.id.iv_preview_crop);
            }
        }


    }