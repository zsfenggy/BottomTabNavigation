package me.stefan.bottom_tab_navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabRadioButton extends RelativeLayout implements Checkable {

    private boolean mChecked;
    private CheckableTextView checkableTextView;
    private CheckableImageView checkableImageView;
    private ImageView ivMsg;
    private TextView tvMsg;
    private TextView tvNew;
    private int mButtonResource;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    public TabRadioButton(Context context) {
        this(context, null);
    }

    public TabRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.tab_radio_button_layout, this, true);
        checkableTextView = (CheckableTextView) this.findViewById(R.id.tab_item_text);
        checkableImageView = (CheckableImageView) this.findViewById(R.id.tab_item_image);
        ivMsg = (ImageView) this.findViewById(R.id.unchecked_msg_icon);
        tvMsg = (TextView) this.findViewById(R.id.unchecked_msg_num);
        tvNew = (TextView) this.findViewById(R.id.unchecked_msg_new);
    }

    public void init(CharSequence text, ColorStateList colorStateList, int iconResId, boolean checked) {
        checkableTextView.setText(text);
        if (null != colorStateList) {
            checkableTextView.setTextColor(colorStateList);
        }
        if (iconResId != 0) {
            setButtonDrawable(iconResId);
        }
        setChecked(checked);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            checkableTextView.setChecked(mChecked);
            checkableImageView.setChecked(mChecked);

            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(TabRadioButton buttonView, boolean isChecked);
    }

    /**
     * Set the background to a given Drawable, identified by its resource id.
     *
     * @param resId the resource id of the drawable to use as the background
     */
    public void setButtonDrawable(int resId) {
        if (resId != 0 && resId == mButtonResource) {
            return;
        }

        mButtonResource = resId;

        Drawable d = null;
        if (mButtonResource != 0) {
            d = getResources().getDrawable(mButtonResource);
        }
        setButtonDrawable(d);
    }

    /**
     * Set the background to a given Drawable
     *
     * @param d The Drawable to use as the background
     */
    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            checkableImageView.setImageDrawable(d);
        }
    }

    private static class SavedState extends BaseSavedState {
        boolean checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "CompoundButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    // *********************************************************************
    public void setText(CharSequence text) {
        if (checkableTextView != null) {
            checkableTextView.setText(text);
        }
    }

    public void setText(int resId) {
        if (checkableTextView != null) {
            checkableTextView.setText(resId);
        }
    }

    public void setTextSize(float textSize) {
        if (checkableTextView != null) {
            checkableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }

    // ////////////////////////////////RedPoint////////////////////////////////
    public void setMsgPointVisibility(final int visibility) {
        if (ivMsg != null) {
            ivMsg.post(new Runnable() {
                @Override
                public void run() {
                    ivMsg.setVisibility(visibility);
                }
            });
        }
    }

    public boolean isMsgPointVisible() {
        return ivMsg != null && ivMsg.getVisibility() == View.VISIBLE;
    }

    // ////////////////////////////////NumPoint////////////////////////////////
    public void setMsgNum(final int num) {
        if (tvMsg != null) {
            tvMsg.post(new Runnable() {
                @Override
                public void run() {
                    if (num <= 0) {
                        tvMsg.setText("");
                        tvMsg.setVisibility(View.GONE);
                    } else if (0 < num && num < 99) {
                        tvMsg.setText(String.valueOf(num));
                        tvMsg.setVisibility(View.VISIBLE);
                    } else {
                        tvMsg.setText(String.format("99%s", "+"));
                        tvMsg.setTag(num);
                        tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
                        tvMsg.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public int getMsgNum() {
        int num = 0;
        if (tvMsg == null || tvMsg.getVisibility() != View.VISIBLE) {
            return 0;
        }
        String text = tvMsg.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return 0;
        } else if (TextUtils.isDigitsOnly(text)) {
            try {
                num = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (text.endsWith("+")) {
            Object obj = getMsgTag();
            if (obj instanceof Integer) {
                num = (int) obj;
            }
        }
        return num;
    }

    public void setMsgTag(Object tag) {
        tvMsg.setTag(tag);
    }

    public Object getMsgTag() {
        return tvMsg.getTag();
    }

    public boolean isMsgNumVisible() {
        return tvMsg != null && tvMsg.getVisibility() == View.VISIBLE;
    }

    // ////////////////////////////////NewMark/////////////////////////////////
    public void setNewMarkVisibility(int visibility) {
        if (tvNew != null) {
            tvNew.setVisibility(visibility);
        }
    }

    public boolean isNewMarkVisible() {
        return tvNew != null && tvNew.getVisibility() == View.VISIBLE;
    }

}