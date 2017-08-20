package com.cn.hisdar.cra.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Hisdar on 2017/8/20.
 */

public class MessagedImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "MessagedImageView";
    private String message;
    private Paint messagePaint;

    public MessagedImageView(Context context) {
        super(context);

        initResources();
    }

    public MessagedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initResources();
    }

    public MessagedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initResources();
    }

    private void initResources() {

        message = "testedMessage";

        messagePaint = new Paint();
        messagePaint.setColor(Color.RED);
        messagePaint.setAntiAlias(true);
        messagePaint.setTextSize(30);
    }

    public void setMessage(String message) {
        this.message = message;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(message, 0, 100, messagePaint);
    }
}
