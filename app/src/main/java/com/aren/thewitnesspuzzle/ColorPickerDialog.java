package com.aren.thewitnesspuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

public class ColorPickerDialog extends Dialog {

    ImageView svImageView;
    ImageView hImageView;

    TextView cancelText;
    TextView applyText;

    View svColorInner, svColorOuter, hColorInner, hColorOuter;

    Bitmap svBitmap;
    Bitmap hBitmap;

    float[] hsv;

    Handler handler;

    int[] rgbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.color_picker_dialog);

        svImageView = findViewById(R.id.sv);
        hImageView = findViewById(R.id.h);
        svColorInner = findViewById(R.id.sv_color_inner);
        svColorOuter = findViewById(R.id.sv_color_outer);
        hColorInner = findViewById(R.id.h_color_inner);
        hColorOuter = findViewById(R.id.h_color_outer);

        svBitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        hBitmap = Bitmap.createBitmap(256, 1, Bitmap.Config.ARGB_8888);

        drawSVBitmap();
        drawHBitmap();

        svImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Must use handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateSVCursorColor();
                    }
                });
            }
        });

        svImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    float screenX = event.getX();
                    float screenY = event.getY();
                    float x = (screenX - v.getLeft()) / v.getWidth();
                    float y = (screenY - v.getTop()) / v.getHeight();
                    x = Math.min(Math.max(x, 0), 1);
                    y = Math.min(Math.max(y, 0), 1);
                    hsv[1] = x;
                    hsv[2] = 1 - y;
                    updateColor();
                }
                return true;
            }
        });

        hImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateHCursorColor();
                    }
                });
            }
        });

        hImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    float screenX = event.getX();
                    float x = (screenX - v.getLeft()) / v.getWidth();
                    x = Math.min(Math.max(x, 0), 1);
                    hsv[0] = x * 360f;
                    updateColor();
                }
                return true;
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        applyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                rgbRef[0] = Color.HSVToColor(hsv);
            }
        });

        svImageView.setImageBitmap(svBitmap);
        hImageView.setImageBitmap(hBitmap);

        handler = new Handler();
    }

    public ColorPickerDialog(@NonNull Context context, int[] rgbRef) {
        super(context);
        hsv = new float[3];
        this.rgbRef = rgbRef;
        Color.RGBToHSV(Color.red(rgbRef[0]), Color.green(rgbRef[0]), Color.blue(rgbRef[0]), hsv);
    }

    public void drawHBitmap(){
        float[] _hsv = new float[3];
        _hsv[1] = _hsv[2] = 1;
        for(int i = 0; i < 256; i++){
            _hsv[0] = i / 255f * 360;
            for(int j = 0; j < 1; j++){
                hBitmap.setPixel(i, j, Color.HSVToColor(_hsv));
            }
        }
    }

    public void drawSVBitmap(){
        float[] _hsv = new float[3];
        _hsv[0] = hsv[0];
        for(int i = 0; i < 64; i++){
            for(int j = 0; j < 64; j++){
                _hsv[1] = i / 63f;
                _hsv[2] = j / 63f;
                svBitmap.setPixel(i, 63 - j, Color.HSVToColor(_hsv));
            }
        }
    }

    public void updateColor(){
        drawSVBitmap();

        updateSVCursorColor();
        updateHCursorColor();
    }

    public void updateSVCursorColor(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(32), dpToPx(32));
        params.setMargins(-dpToPx(16) + (int)(hsv[1] * svImageView.getWidth()), -dpToPx(16) + (int)((1 - hsv[2]) * svImageView.getHeight()), -dpToPx(16), -dpToPx(16));
        svColorOuter.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(dpToPx(24), dpToPx(24));
        params.setMargins(-dpToPx(12) + (int)(hsv[1] * svImageView.getWidth()), -dpToPx(12) + (int)((1 - hsv[2]) * svImageView.getHeight()), -dpToPx(12), -dpToPx(12));
        svColorInner.setLayoutParams(params);

        svColorInner.getBackground().setColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_ATOP);
    }

    public void updateHCursorColor(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(32), dpToPx(32));
        params.setMargins(-dpToPx(16) + (int)(hsv[0] / 360f * hImageView.getWidth()), hImageView.getTop() -dpToPx(16) + (int)(0.5f * hImageView.getHeight()), -dpToPx(16), -dpToPx(16));
        hColorOuter.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(dpToPx(24), dpToPx(24));
        params.setMargins(-dpToPx(12) + (int)(hsv[0] / 360f * hImageView.getWidth()), hImageView.getTop() -dpToPx(12) + (int)(0.5f * hImageView.getHeight()), -dpToPx(12), -dpToPx(12));
        hColorInner.setLayoutParams(params);

        float[] onlyH = new float[]{hsv[0], 1, 1};
        hColorInner.getBackground().setColorFilter(Color.HSVToColor(onlyH), PorterDuff.Mode.SRC_ATOP);
    }

    public int dpToPx(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
