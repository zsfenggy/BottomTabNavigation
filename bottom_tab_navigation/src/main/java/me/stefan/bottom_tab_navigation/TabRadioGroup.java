package me.stefan.bottom_tab_navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class TabRadioGroup extends LinearLayout {

    // holds the checked id; the selection is empty by default
    private int mCheckedId = -1;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    // tracks children radio buttons checked state
    private OnClickListener mChildOnClickListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    /**********************************************************************************/
    // 存放当前的TabRadioButton
    private List<TabRadioButton> tabRadioButtons = new ArrayList<TabRadioButton>();

    public TabRadioGroup(Context context) {
        super(context);
    }

    public TabRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context, CharSequence[] textArray, int iconArrayResId, int idArrayResId,
                     ColorStateList colorStateList, float textSize, int checkedId) {
        if (null == textArray || textArray.length == 0) {
            return;
        }
        if (checkedId != View.NO_ID) {
            mCheckedId = checkedId;
        }

        mChildOnClickListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);

        Resources res = context.getResources();
        TypedArray taIcon = null, taId = null;
        if (iconArrayResId != 0) {
            taIcon = res.obtainTypedArray(iconArrayResId);
        }
        if (idArrayResId != 0) {
            taId = res.obtainTypedArray(idArrayResId);
        }
        for (int i = 0; i < textArray.length; i++) {
            TabRadioButton trb = new TabRadioButton(context);
            int iconResId = 0, trbId = 0;
            if (null != taIcon) {
                iconResId = taIcon.getResourceId(i, 0);
            }
            if (null != taId) {
                trbId = taId.getResourceId(i, 0);
            }
            trb.setId(trbId);
            trb.setTextSize(textSize);
            trb.init(textArray[i], colorStateList, iconResId, i == mCheckedId);
            LayoutParams lp = new LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            addView(trb, lp);
        }
        if (null != taIcon) {
            taIcon.recycle();
        }
        if (null != taId) {
            taId.recycle();
        }

        if (mCheckedId != -1) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof TabRadioButton) {
            final TabRadioButton button = (TabRadioButton) child;
            tabRadioButtons.add(button);
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(button.getId());
            }
        }

        super.addView(child, index, params);
    }

    /**
     * <p>
     * Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.
     * </p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedRadioButtonId()
     * @see #clearCheck()
     */
    public void check(int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof TabRadioButton) {
            ((TabRadioButton) checkedView).setChecked(checked);
        }
    }

    /**
     * <p>
     * Returns the identifier of the selected radio button in this group. Upon
     * empty selection, the returned value is -1.
     * </p>
     *
     * @return the unique id of the selected radio button in this group
     * @see #check(int)
     * @see #clearCheck()
     */
    public int getCheckedRadioButtonId() {
        return mCheckedId;
    }

    /**
     * <p>
     * Clears the selection. When the selection is cleared, no radio button in
     * this group is selected and {@link #getCheckedRadioButtonId()} returns
     * null.
     * </p>
     *
     * @see #check(int)
     * @see #getCheckedRadioButtonId()
     */
    public void clearCheck() {
        check(-1);
    }

    /**
     * <p>
     * Register a callback to be invoked when the checked radio button changes
     * in this group.
     * </p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    /**
     * <p>
     * This set of layout parameters defaults the width and the height of the
     * children to {@link #WRAP_CONTENT} when they are not specified in the XML
     * file. Otherwise, this class used the value read from the XML file.
     * </p>
     * <p>
     * See {android.R.styleable#LinearLayout_Layout LinearLayout Attributes}
     * for a list of all child view attributes that this class
     * supports.
     * </p>
     */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * <p>
         * Fixes the child's width to
         * {@link ViewGroup.LayoutParams#WRAP_CONTENT} and the
         * child's height to
         * {@link ViewGroup.LayoutParams#WRAP_CONTENT} when not
         * specified in the XML file.
         * </p>
         *
         * @param a          the styled attributes set
         * @param widthAttr  the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    /**
     * <p>
     * Interface definition for a callback to be invoked when the checked radio
     * button changed in this group.
     * </p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>
         * Called when the checked radio button has changed. When the selection
         * is cleared, checkedId is -1.
         * </p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(TabRadioGroup group, int checkedId);
    }

    private class CheckedStateTracker implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            int id = v.getId();
            if (mCheckedId != -1 && mCheckedId != id) {
                setCheckedStateForView(mCheckedId, false);
                setCheckedStateForView(id, true);
                setCheckedId(id);
            }

            mProtectFromCheckedChange = false;
        }

    }

    /**
     * <p>
     * A pass-through listener acts upon the events and dispatches them to
     * another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.
     * </p>
     */
    private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            if (parent == TabRadioGroup.this && child instanceof TabRadioButton) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = child.hashCode();
                    child.setId(id);
                }
                ((TabRadioButton) child).setOnClickListener(mChildOnClickListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == TabRadioGroup.this && child instanceof TabRadioButton) {
                ((TabRadioButton) child).setOnCheckedChangeWidgetListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    //**********************************************************************************/

    public int getRadioButtonCount() {
        return tabRadioButtons.size();
    }

    public TabRadioButton getCurRadioButton(int position) {
        if (null == tabRadioButtons || tabRadioButtons.size() == 0 || position > tabRadioButtons.size() - 1) {
            return null;
        }
        return tabRadioButtons.get(position);
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