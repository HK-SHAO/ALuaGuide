package com.sf.LuaEditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;


public class CodeEditText extends ShaderEditor {
    private Context context;
    private transient Paint paint = new Paint();
    private Layout layout;

    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.MONOSPACE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("nightMode", false)) {
            paint.setColor(Color.parseColor("#eeeeee"));
        } else {
            paint.setColor(Color.parseColor("#424242"));
        }
        paint.setTextSize(getPixels(14));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout = getLayout();
            }
        });
    }

    private int getDigitCount() {
        int count = 0;
        int len = getLineCount();
        while (len > 0) {
            count++;
            len /= 10;
        }
        return count;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int padding = (int) getPixels(getDigitCount() * 10 + 10);
        setPadding(padding, 0, 0, 0);


        int scrollY = getScrollY();
        int firstLine = layout.getLineForVertical(scrollY), lastLine;

        try {
            lastLine = layout.getLineForVertical(scrollY + (getHeight() - getExtendedPaddingTop() - getExtendedPaddingBottom()));
        } catch (NullPointerException npe) {
            lastLine = layout.getLineForVertical(scrollY + (getHeight() - getPaddingTop() - getPaddingBottom()));
        }

        int positionY = getBaseline() + (layout.getLineBaseline(firstLine) - layout.getLineBaseline(0));
        drawLineNumber(canvas, layout, positionY, firstLine);
        for (int i = firstLine + 1; i <= lastLine; i++) {
            positionY += layout.getLineBaseline(i) - layout.getLineBaseline(i - 1);
            drawLineNumber(canvas, layout, positionY, i);
        }

        super.onDraw(canvas);

    }

    private void drawLineNumber(Canvas canvas, Layout layout, int positionY, int line) {
        int positionX = (int) layout.getLineLeft(line);
        canvas.drawText(String.valueOf(line + 1), positionX + getPixels(2), positionY, paint);

    }

    private float getPixels(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


}
