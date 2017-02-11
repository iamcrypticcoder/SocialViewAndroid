package com.crypticcoder.android.socialviewandroid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by mahbub.kr on 1/27/2015.
 */
public class LikeViewVertical extends LinearLayout {

    private static final double EPS = 0.0005;
    private static final int BILLION = 1000000000;
    private static final int MILLION = 1000000;
    private static final int THOUSAND = 1000;
    private static final int HUNDRED = 100;
    private static final int TEN = 10;

    private static final int SIZE_SMALL = 0;
    private static final int SIZE_MEDIUM = 1;
    private static final int SIZE_LARGE = 2;
    private static final int SIZE_X_LARGE = 3;

    private static final int[] IMAGE_VIEW_SIZE = {20, 30, 40, 50};
    private static final int[][] COUNT_TEXT_VIEW_WIDTH = {new int[] {20, 30}, new int[] {40, 50}, new int[] {60, 70}, new int[] {80, 90}};
    private static final int[] COUNT_TEXT_SIZE = {8, 12, 16, 20};

    private static final int COUNT_FORMAT_SHORT = 0;
    private static final int COUNT_FORMAT_LONG = 1;

    // UI View References
    private ImageView iconImageView;
    private TextView countTextView;
    private View rightBorderView;

    private boolean isLiked;

    // Attribute values
    private ColorStateList mTextColor;
    private int mIconSrc;
    private int mLikedIconSrc;
    private ColorStateList mIconFilterColor;
    private int mLikeViewSize;
    private int mCountFormat;
    private long mCount;
    private boolean mOnClickToggle;

    ScaleAnimation animation;

    public interface OnClickListener {
        void onClicked(LikeViewVertical view);
    }

    private OnClickListener mListener;

    public LikeViewVertical(Context context) {
        this(context, null);
    }

    public LikeViewVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeViewVertical(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.social_view_android_like_view_vertical, this);
        final float displayScale = getResources().getDisplayMetrics().density;

        // Taking references
        iconImageView = (ImageView) findViewById(R.id.icon);
        countTextView = (TextView) findViewById(R.id.count);

        // Getting Attribute Info(s)
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LikeViewVertical,0, 0);
        try {
            mTextColor = a.getColorStateList(R.styleable.LikeViewVertical_socialViewAndroid_textColor);
            mIconSrc = a.getResourceId(R.styleable.LikeViewVertical_socialViewAndroid_iconSrc, -1);
            mLikedIconSrc = a.getResourceId(R.styleable.LikeViewVertical_socialViewAndroid_likedIconSrc, -1);
            mIconFilterColor = a.getColorStateList(R.styleable.LikeViewVertical_socialViewAndroid_iconFilterColor);
            mLikeViewSize = a.getInt(R.styleable.LikeViewVertical_socialViewAndroid_likeViewSize, SIZE_SMALL);
            mCount = a.getInt(R.styleable.LikeViewVertical_socialViewAndroid_count, 0);
            mCountFormat = a.getInt(R.styleable.LikeViewVertical_socialViewAndroid_countFormat, COUNT_FORMAT_SHORT);
            mOnClickToggle = a.getBoolean(R.styleable.LikeViewVertical_socialViewAndroid_onClickToggle, false);
        } finally {
            a.recycle();
        }

        // Setting Attributes
        if(mIconSrc != -1) iconImageView.setImageResource(mIconSrc);
        if(null != mIconFilterColor) iconImageView.setColorFilter(mIconFilterColor.getColorForState(getDrawableState(), Color.BLACK), PorterDuff.Mode.SRC_IN);
        else iconImageView.setColorFilter(ContextCompat.getColor(context, R.color.socialViewAndroid_iconColorFilterDefault));
        if(null != mTextColor) countTextView.setTextColor(mTextColor);
        else countTextView.setTextColor(ContextCompat.getColor(context, R.color.socialViewAndroid_textColorDefault));
        ViewGroup.LayoutParams params = iconImageView.getLayoutParams();
        params.width = params.height = (int)(IMAGE_VIEW_SIZE[mLikeViewSize] * displayScale);
        iconImageView.setLayoutParams(params);
        countTextView.setMinWidth((int)(COUNT_TEXT_VIEW_WIDTH[mLikeViewSize][mCountFormat] * displayScale));
        countTextView.setMaxWidth((int) (COUNT_TEXT_VIEW_WIDTH[mLikeViewSize][mCountFormat] * displayScale));
        countTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, COUNT_TEXT_SIZE[mLikeViewSize]);

        // Set callback
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickToggle) LikeViewVertical.this.toggle();
                if (null != mListener) mListener.onClicked(LikeViewVertical.this);
            }
        });

        // Initializing like state. Default value is false
        isLiked = false;

        // Setting count in countTextView
        countTextView.setText(formattedCount());

        // Initializing like-dislike animation
        animation = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setStartOffset(100);
        animation.setZAdjustment(Animation.ZORDER_TOP);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    public void like() {
        // If already liked do nothing
        if(isLiked) return;
        isLiked = true;
        if(mLikedIconSrc != -1) iconImageView.setImageResource(mLikedIconSrc);
        else iconImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.social_view_android_ic_liked_default));
        iconImageView.startAnimation(animation);
    }

    public void unlike() {
        // If already not liked do nothing
        if(!isLiked) return;
        isLiked = false;
        if(mIconSrc != -1) iconImageView.setImageResource(mIconSrc);
        else iconImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.social_view_android_ic_like_default));
        iconImageView.startAnimation(animation);
    }

    public void toggle() {
        if(isLiked) unlike();
        else like();
    }

    public void increaseCount() {
        mCount++;
        countTextView.setText(formattedCount());
    }

    public void decreaseCount() {
        mCount--;
        countTextView.setText(formattedCount());
    }

    public void setListener(OnClickListener mListener) {
        this.mListener = mListener;
    }

    public void setLikeStatus(boolean isLiked) {
        this.isLiked = isLiked;
        if(isLiked) {
            if (mLikedIconSrc != -1) iconImageView.setImageResource(mLikedIconSrc);
            else iconImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.social_view_android_ic_liked_default));
        } else {
            if(mIconSrc != -1) iconImageView.setImageResource(mIconSrc);
            else iconImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.social_view_android_ic_like_default));
        }
    }

    public boolean getLikeStatus() {
        return this.isLiked;
    }

    public LikeViewVertical setCount(long count) {
        this.mCount = count;
        countTextView.setText(formattedCount());
        return this;
    }

    public long getCount() {
        return this.mCount;
    }

    private String formattedCount() {
        if(mCountFormat == COUNT_FORMAT_SHORT) return countFormatShort();
        if(mCountFormat == COUNT_FORMAT_LONG) return countFormatLong();
        return "";
    }

    private String countFormatLong() {
        if(mCount > BILLION) {
            if(mCount >= 1010000000)
                return String.format("%.2f B", (double)mCount / BILLION);
            else return "1 B";
        } else if(mCount == BILLION) {
            return "1 B";
        } else if(mCount >= 100*MILLION) {
            return mCount / MILLION + " M";
        } else if(mCount > MILLION) {
            if(mCount >= 1010000) {
                if(mCount % MILLION == 0)
                    return String.format("%.0f M", (double)mCount / MILLION);
                else return String.format("%.2f M", (double)mCount / MILLION);
            }
            else return "1 M";
        } else if(mCount == MILLION) {
            return "1 M";
        } else if(mCount >= THOUSAND) {
            return String.valueOf(mCount/THOUSAND) + "," + String.format("%03d", mCount%THOUSAND);
        } else
            return String.valueOf(mCount);
    }

    private String countFormatShort() {
        if(mCount >= BILLION) {
            if(mCount % BILLION == 0)
                return mCount / BILLION + " B";
            else return String.format("%.2f B", (double)mCount / BILLION - EPS);
        } else if(mCount >= 100*MILLION) {
            return mCount / MILLION + " M";
        } else if(mCount >= 1010000) {
            if(mCount % MILLION == 0)
                return mCount / MILLION + " M";
            else return String.format("%.2f M", (double)mCount / MILLION - EPS);
        } else if(mCount >= MILLION) {
            return mCount / MILLION + " M";
        } else if(mCount >= THOUSAND) {
            return mCount / THOUSAND + " K";
        } else
            return String.valueOf(mCount);
    }
}
