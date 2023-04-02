package com.gulofy.scaniby.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scanlibrary.ScanActivity;
import com.gulofy.scaniby.R;
import com.gulofy.scaniby.adapter.ImageAdapter;
import com.gulofy.scaniby.db.DBHelper;
import com.gulofy.scaniby.main_utils.AdjustUtil;
import com.gulofy.scaniby.main_utils.BitmapUtils;
import com.gulofy.scaniby.main_utils.Constant;
import com.gulofy.scaniby.models.DBModel;
import com.gulofy.scaniby.utils.AdsUtils;
import com.youth.banner.Banner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.pqpo.smartcropperlib.view.CropImageView;

public class MultiEditActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    ArrayList<String> imageList;
    ImageAdapter mAdapterImage;
    ImageView img_page_right;
    ImageView img_page_left;
    TextView iv_delete;
    Banner banner;
    Drawable drawableCircleChecked;
    Drawable drawableCircleUnChecked;
    Drawable drawableLeft;
    Drawable drawableLeftDisable;
    Drawable drawableRight;
    Drawable drawableRightDisable;
    TextView tv_numbers;
    TextView iv_original;
    TextView iv_color;
    TextView iv_ocv_black;
    TextView iv_sharp_black;
    TextView saveAndNextBtn;
    private ProgressDialog progressDialog;
    private Bitmap tempBitmap;
    public String current_docs_name;
    private SeekBar seekBarBrightness;
    CropImageView iv_preview_crop;
    int selectedId = 0;
    public String selected_group_name;
    Bitmap original;
    public DBHelper dbHelper;

    String group_name_1;
    boolean isFromCamera;
    boolean fromCameraBtn;

    boolean isFirstSave = false;
    protected ImageView iv_edit;
    int n = 0;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Crop")) {
                Intent intent2 = new Intent(MultiEditActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", "SavedDocumentActivity");
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("CurrentFilterActivity")) {
                startActivity(new Intent(MultiEditActivity.this, CurrentFilterActivity.class));
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("ScannerActivity_Retake")) {
                Intent newIntent=new Intent(MultiEditActivity.this,ScannerActivity.class);
                newIntent.putExtra("fromCameraBtn",false);
                startActivity(newIntent);
                finish();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_Crop"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CurrentFilterActivity"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity_Retake"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_edit);
        dbHelper = new DBHelper(this);
        imageList = getIntent().getStringArrayListExtra("imageList");
        isFromCamera = getIntent().getBooleanExtra("isFromCamera", false);
        fromCameraBtn = getIntent().getBooleanExtra("fromCameraBtn1", false);
        group_name_1 = getIntent().getStringExtra("group_name");

        Log.e("fromCameraBtn1", String.valueOf(fromCameraBtn));
        banner = findViewById(R.id.banner);
        img_page_right = findViewById(R.id.img_page_right);
        iv_sharp_black = findViewById(R.id.iv_sharp_black);
        iv_ocv_black = findViewById(R.id.iv_ocv_black);
        saveAndNextBtn = findViewById(R.id.saveAndNextBtn);
        iv_color = findViewById(R.id.iv_color);
        iv_preview_crop = findViewById(R.id.iv_preview_crop);
        iv_original = findViewById(R.id.iv_original);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        img_page_left = findViewById(R.id.img_page_left);
        iv_delete = findViewById(R.id.iv_delete);
        tv_numbers = findViewById(R.id.tv_numbers);
        Log.e("imagelist", String.valueOf(imageList.size()));
        seekBarBrightness = (SeekBar) findViewById(R.id.seekBarBrightness);
        resetNumbers();
        mAdapterImage = new ImageAdapter(imageList, MultiEditActivity.this);
        this.drawableLeft = getDrawableByRsid(R.drawable.ic_page_left);
        this.drawableLeftDisable = getDrawableByRsid(R.drawable.ic_page_left_disable);
        this.drawableRight = getDrawableByRsid(R.drawable.ic_page_right);
        this.drawableRightDisable = getDrawableByRsid(R.drawable.ic_page_right_disable);
        this.drawableCircleChecked = getDrawableByRsid(R.drawable.ic_circle_checked);
        this.drawableCircleUnChecked = getDrawableByRsid(R.drawable.ic_circle_uncheck);
        this.img_page_left.setImageDrawable(this.drawableLeftDisable);
        this.img_page_right.setImageDrawable(this.drawableRight);


        Glide.with(getApplicationContext()).asBitmap().load(imageList.get(n)).into(new SimpleTarget<Bitmap>() {
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                original = bitmap;
                iv_preview_crop.setImageToCrop(bitmap);
                iv_preview_crop.setFullImgCrop();
            }
        });

        saveAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFromCamera) {
                    if (fromCameraBtn) {
                        if (isFirstSave) {
                            Constant.inputType = "GroupItem";
                            if (iv_preview_crop.canRightCrop()) {
                                Constant.original = iv_preview_crop.crop();
                                new addDocGroup().execute(new Bitmap[]{Constant.original});

                                if (n == imageList.size() - 1) {
                                    Intent intent2 = new Intent(MultiEditActivity.this, GroupDocumentActivity.class);
                                    intent2.putExtra("current_group", selected_group_name);
                                    startActivity(intent2);
                                    Constant.IdentifyActivity = "";
                                    finish();
                                }
                                btu_next();

                            }
                        }else {
                            Constant.inputType = "Group";
                            if (iv_preview_crop.canRightCrop()) {
                                Constant.original = iv_preview_crop.crop();
                                new addDocGroup().execute(new Bitmap[]{Constant.original});
                                isFirstSave = true;
                                btu_next();

                            }
                        }

                    }
                    else {
                        Constant.inputType = "GroupItem";
                        if (iv_preview_crop.canRightCrop()) {
                            Constant.original = iv_preview_crop.crop();
                            new addDocGroup().execute(new Bitmap[]{Constant.original});
                            if (n == imageList.size() - 1) {
                                Intent intent2 = new Intent(MultiEditActivity.this, GroupDocumentActivity.class);
                                intent2.putExtra("current_group", group_name_1);
                                startActivity(intent2);
                                Constant.IdentifyActivity = "";
                                finish();
                            }

                            btu_next();

                        }
                    }

                } else {
                    if (isFirstSave) {
                        Constant.inputType = "GroupItem";
                        if (iv_preview_crop.canRightCrop()) {
                            Constant.original = iv_preview_crop.crop();
                            new addDocGroup().execute(new Bitmap[]{Constant.original});

                            if (n == imageList.size() - 1) {
                                Intent intent2 = new Intent(MultiEditActivity.this, GroupDocumentActivity.class);
                                intent2.putExtra("current_group", selected_group_name);
                                startActivity(intent2);
                                Constant.IdentifyActivity = "";
                                finish();
                            }
                            btu_next();

                        }
                    } else {
                        Constant.inputType = "Group";
                        if (iv_preview_crop.canRightCrop()) {
                            Constant.original = iv_preview_crop.crop();
                            new addDocGroup().execute(new Bitmap[]{Constant.original});
                            isFirstSave = true;
                            btu_next();

                        }
                    }
                }


            }
        });

      /*  this.banner.setAdapter(mAdapterImage);
        this.banner.addPageTransformer(new AlphaPageTransformer());
        new Handler().postDelayed(new Runnable() {
            public void run() {
               mAdapterImage.notifyItemChanged(banner.getCurrentItem());
            }
        }, 600);
        this.banner.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                selectedId=i;
              //  original= BitmapFactory.decodeFile(imageList.get(i));
                try {
                    original= MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageList.get(i)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("selected",String.valueOf(i));
            }
        });*/

        int size = this.imageList.size();
        int i = 1;
       /* if (size > i) {
            this.imageList.get(i).isShowDelIcon = true;
        }*/

        // resetNumbers(i);
        seekBarBrightness.setOnSeekBarChangeListener(this);

        img_page_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btu_next();
            }
        });

        img_page_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btu_prev();
            }
        });

    }

    public void btu_prev() {
        if (n > 0) {
            n--;
        }
        Glide.with(getApplicationContext()).asBitmap().load(imageList.get(n)).into(new SimpleTarget<Bitmap>() {
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                original = bitmap;
                iv_preview_crop.setImageToCrop(bitmap);
                iv_preview_crop.setFullImgCrop();
            }
        });

        resetNumbers();
    }

    public void btu_next() {

        if (n < imageList.size() - 1) {
            n++;
        }
        Glide.with(getApplicationContext()).asBitmap().load(imageList.get(n)).into(new SimpleTarget<Bitmap>() {
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                original = bitmap;
                iv_preview_crop.setImageToCrop(bitmap);
                iv_preview_crop.setFullImgCrop();
            }
        });

        resetNumbers();
    }


    public Bitmap getBitmapNew(String filePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math
                .abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);
        return output;
    }

    public Drawable getDrawableByRsid(int i) {
        return getResources().getDrawable(i);
    }

    public void resetNumbers() {
        this.tv_numbers.setText(String.format("%d/%d", new Object[]{Integer.valueOf(n + 1), Integer.valueOf(imageList.size())}));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarBrightness:
                changeBrightness(i);
                break;
        }

    }

    private void changeBrightness(float brightness) {
        getCurrentBitmap();
        iv_preview_crop.setImageBitmap(AdjustUtil.changeBitmapContrastBrightness(original, 1.0f, brightness));

    }


    public void nextButtonClick() {


        int currentItem = this.banner.getCurrentItem() + 1;
        if (currentItem < this.imageList.size()) {
            this.banner.setCurrentItem(currentItem, true);

        }
    }


    public Bitmap getCurrentBitmap() {
        Glide.with(getApplicationContext()).asBitmap().load(imageList.get(n)).into(new SimpleTarget<Bitmap>() {
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                original = bitmap;
            }
        });
        return original;
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_add_new_scan:
                return;
            case R.id.iv_original:
                try {
                    showProgressDialog();
                    tempBitmap = original;
                    iv_preview_crop.setImageBitmap(original);
                    dismissProgressDialog();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

                iv_original.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_original.setTextColor(getResources().getColor(R.color.white));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_ocv_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getBWBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;

                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });

                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.white));

                return;
            case R.id.iv_color:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getMagicColorBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_color.setTextColor(getResources().getColor(R.color.white));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_sharp_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getGrayBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_crop.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_crop.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.white));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_delete:
                if (imageList.size() == 1) {
                    Toast.makeText(MultiEditActivity.this,getString(R.string.delete_req_1) , Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(R.string.delete_image);
                    alert.setMessage(R.string.delete_current_umage);
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int afterDelete;
                            imageList.remove(n);
                            if(imageList.size()==n){
                                n=n-1;
                            }
                            Glide.with(getApplicationContext()).asBitmap().load(imageList.get(n)).into(new SimpleTarget<Bitmap>() {
                                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                    original = bitmap;
                                    iv_preview_crop.setImageToCrop(bitmap);
                                    iv_preview_crop.setFullImgCrop();
                                }
                            });
                            resetNumbers();
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return;
            case R.id.img_back:
                onBackPressed();
                return;
            case R.id.iv_done:
                if (iv_preview_crop.canRightCrop()) {
                    original = iv_preview_crop.crop();
                    //  new CropDocumentActivity.addDocGroup().execute(new Bitmap[]{Constant.original});
                    return;
                }
                return;
            case R.id.iv_Rotate_Doc:
                Bitmap bitmap = getCurrentBitmap();
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                original = createBitmap;
                iv_preview_crop.setImageToCrop(original);
                iv_preview_crop.setFullImgCrop();
                //  Log.e(TAG, "onClick: Rotate");
                /*if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    Constant.IdentifyActivity = "CurrentFilterActivity";
                    AdsUtils.showGoogleInterstitialAd(CropDocumentActivity.this, true);
                    return;
                }*/
                return;
            case R.id.iv_edit:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    new addGroup().execute(new Bitmap[]{Constant.original});
                    return;
                }
                return;
            case R.id.iv_full_crop:
                iv_preview_crop.setFullImgCrop();
                return;
            case R.id.iv_retake:
                Constant.IdentifyActivity = "ScannerActivity_Retake";
                AdsUtils.showGoogleInterstitialAd(MultiEditActivity.this, true);
                return;
            case R.id.ly_current_filter:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    Constant.IdentifyActivity = "CurrentFilterActivity";
                    AdsUtils.showGoogleInterstitialAd(MultiEditActivity.this, true);
                    return;
                }
                return;
          /*  case R.id.ly_rotate_doc:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                iv_preview_crop.setImageToCrop(Constant.original);
                iv_preview_crop.setFullImgCrop();
                return;*/
            default:
                return;
        }

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.apply_filter));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    private class addGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MultiEditActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(bitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = selected_group_name;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;
            Constant.IdentifyActivity = "DocumentEditorActivity_Crop";
            AdsUtils.showGoogleInterstitialAd(MultiEditActivity.this, true);
        }
    }

    private class addDocGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addDocGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MultiEditActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(bitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            if (isFromCamera && !fromCameraBtn) {
                group_name = group_name_1;
            } else {
                group_name = selected_group_name;
            }
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            try {
                if ((this.progressDialog!=null) && this.progressDialog.isShowing()){
                   this.progressDialog.dismiss();
                }
            }
            catch (IllegalArgumentException e){
                Log.e("error",e.getMessage());
            }
            selected_group_name = group_name;
            current_docs_name = current_doc_name;
            if (imageList.size() == 1) {
                Intent intent2 = new Intent(MultiEditActivity.this, GroupDocumentActivity.class);
                intent2.putExtra("current_group", selected_group_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            }
        }
    }


    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



}