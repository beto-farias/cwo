package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

import android.support.annotation.NonNull;
import android.view.animation.Interpolator;
import android.widget.TextView;

/**
 * Created by beto on 21/01/18.
 */

public class AnimateCounter {

    private TextView mView;
    private long mDuration;
    private float mStartValue;
    private float mEndValue;
    private int mPrecision;
    private Interpolator mInterpolator;
    private ValueAnimator mValueAnimator;
    private AnimateCounterListener mListener;

    public AnimateCounter(Builder builder){
        mView = builder.mView;
        mDuration = builder.mDuration;
        mStartValue = builder.mStartValue;
        mEndValue = builder.mEndValue;
        mPrecision = builder.mPrecision;
        mInterpolator = builder.mInterpolator;

    }

    public void execute(){
        mValueAnimator = ValueAnimator.ofFloat(mStartValue,mEndValue);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float current = Float.valueOf(valueAnimator.getAnimatedValue().toString());
                mView.setText(String.format("%." + mPrecision + "f", current));
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mListener != null){
                    mListener.onAnimateCountEnd();
                }
            }
        });

        mValueAnimator.start();
    }

    public void stop(){
        if(mValueAnimator.isRunning()){
            mValueAnimator.cancel();
        }
    }

    public void setAnimateCounterListener(AnimateCounterListener listener){
        mListener = listener;
    }

    public static class Builder{
        private long mDuration = 2000;
        private float mStartValue = 0 ;
        private float mEndValue = 10;
        private int mPrecision = 0 ;
        private Interpolator mInterpolator = null;
        private TextView mView;

        public Builder(@NonNull TextView view){
            if(view == null){
                throw new IllegalArgumentException("El textView no puede ser nulo");
            }

            this.mView = view;
        }

        public Builder setCount(int start, int end){
            if(start == end){
                throw new IllegalArgumentException("El inicio no puede ser igual que el final");
            }

            mStartValue = start;
            mEndValue = end;
            mPrecision = 0;
            return this;
        }

        public Builder setCount(float start, float end, int precision){
            if(Math.abs(start - end) < 0.001 ){
                throw new IllegalArgumentException("El inicio no puede ser igual que el final");
            }

            mStartValue = start;
            mEndValue = end;
            mPrecision = precision;
            return this;
        }

        public Builder setDuration(long duration){
            if(duration<0){
                throw new IllegalArgumentException("La duración no puede ser cero");
            }

            this.mDuration = duration;
            return this;
        }

        public Builder setInterpolator(Interpolator interpolator){
            this.mInterpolator = interpolator;
            return this;
        }

        public AnimateCounter build(){
            return new AnimateCounter(this);
        }
    }

    public interface AnimateCounterListener{

        public void onAnimateCountEnd();
    }

}
