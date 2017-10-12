package me.stefan.bottom_tab_navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class BottomTabBar extends LinearLayout {

    private TabRadioGroup trg;

    public BottomTabBar(Context context) {
        this(context, null);
    }

    public BottomTabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (null == attrs) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomTabBar);
        init(context, ta);
        ta.recycle();
    }

    private void init(Context context, TypedArray ta) {
        CharSequence[] textArray = ta.getTextArray(R.styleable.BottomTabBar_btbTextArray);
        if (null == textArray || textArray.length == 0) {
            return;
        }

        LayoutInflater.from(context).inflate(R.layout.bottom_tab_bar_layout, this, true);
        View line = this.findViewById(R.id.line);
        trg = (TabRadioGroup) this.findViewById(R.id.tabRadioGroup);

        int iconArrayResId = ta.getResourceId(R.styleable.BottomTabBar_btbIconArray, 0);
        int idArrayResId = ta.getResourceId(R.styleable.BottomTabBar_btbIdArray, 0);
        ColorStateList colorStateList = ta.getColorStateList(R.styleable.BottomTabBar_btbTextColor);
        float textSize = ta.getInt(R.styleable.BottomTabBar_btbTextSize, 12);
        int checkedId = ta.getResourceId(R.styleable.BottomTabBar_btbCheckedButton, -1);
        final int index = ta.getInt(R.styleable.BottomTabBar_btbOrientation, HORIZONTAL);
        boolean showLine = ta.getBoolean(R.styleable.BottomTabBar_btbShowLine, false);

        if (showLine) {
            int lineColor = ta.getColor(R.styleable.BottomTabBar_btbLineColor, 0);
            line.setBackgroundColor(lineColor);
            if (index == HORIZONTAL) {
                setOrientation(VERTICAL);
            } else if (index == VERTICAL) {
                setOrientation(HORIZONTAL);
                int lineThickness = ta.getInt(R.styleable.BottomTabBar_btbLineThickness, 1);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(lineThickness,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                line.setLayoutParams(lp);
            }
        } else {
            line.setSystemUiVisibility(GONE);
            if (index == HORIZONTAL) {
                setOrientation(HORIZONTAL);
            } else if (index == VERTICAL) {
                setOrientation(VERTICAL);
            }
        }
        trg.init(context, textArray, iconArrayResId, idArrayResId, colorStateList, textSize, checkedId);
    }

    //**********************************************************************************/

    public void setOnCheckedChangeListener(TabRadioGroup.OnCheckedChangeListener listener) {
        if (null != trg) {
            trg.setOnCheckedChangeListener(listener);
        }
    }

    public int getRadioButtonCount() {
        return null != trg ? trg.getRadioButtonCount() : 0;
    }

    public TabRadioButton getCurRadioButton(int position) {
        if (null == trg) {
            return null;
        }
        return trg.getCurRadioButton(position);
    }

    // ////////////////////////////////RedPoint////////////////////////////////
    public void setMsgPointVisibility(int position, int visibility) {
        TabRadioButton trb = getCurRadioButton(position);
        if (null != trb) {
            trb.setMsgPointVisibility(visibility);
        }
    }

    public boolean isMsgPointVisible(int position) {
        TabRadioButton trb = getCurRadioButton(position);
        return null != trb && trb.isMsgPointVisible();
    }

    // ////////////////////////////////NumPoint////////////////////////////////
    public void setMsgNum(int position, int num) {
        TabRadioButton trb = getCurRadioButton(position);
        if (null != trb) {
            trb.setMsgNum(num);
        }
    }

    public int getMsgNum(int position) {
        TabRadioButton trb = getCurRadioButton(position);
        return null != trb ? trb.getMsgNum() : 0;
    }

    public void setMsgTag(int position, Object tag) {
        TabRadioButton trb = getCurRadioButton(position);
        if (null != trb) {
            trb.setMsgTag(tag);
        }
    }

    public Object getMsgTag(int position) {
        TabRadioButton trb = getCurRadioButton(position);
        return null != trb ? trb.getMsgTag() : null;
    }

    public boolean isMsgNumVisible(int position) {
        TabRadioButton trb = getCurRadioButton(position);
        return null != trb && trb.isMsgNumVisible();
    }

    // ////////////////////////////////NewMark/////////////////////////////////
    public void setNewMarkVisibility(int position, int visibility) {
        TabRadioButton trb = getCurRadioButton(position);
        if (null != trb) {
            trb.setNewMarkVisibility(visibility);
        }
    }

    public boolean isNewMarkVisible(int position) {
        TabRadioButton trb = getCurRadioButton(position);
        return null != trb && trb.isNewMarkVisible();
    }
}