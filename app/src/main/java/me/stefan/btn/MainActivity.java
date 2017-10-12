package me.stefan.btn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.stefan.bottom_tab_navigation.BottomTabBar;
import me.stefan.bottom_tab_navigation.TabRadioGroup;

public class MainActivity extends AppCompatActivity implements TabRadioGroup.OnCheckedChangeListener {

    private TextView textView;
    private CharSequence[] textArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onCheckedChanged(TabRadioGroup group, int checkedId) {
        int position = 0;
        switch (checkedId) {
            case R.id.wx:
                position = 0;
                break;
            case R.id.contacts:
                position = 1;
                break;
            case R.id.find:
                position = 2;
                break;
            case R.id.me:
                position = 3;
                break;
        }
        textView.setText(textArray[position]);
    }

    private void initView() {
        textArray = getResources().getTextArray(R.array.tab_text);
        textView = findViewById(R.id.text);
        textView.setText(textArray[0]);
        BottomTabBar bar = findViewById(R.id.bottomTabBar);
        bar.setOnCheckedChangeListener(this);
        bar.setMsgNum(0, 10);
        bar.setMsgPointVisibility(1, View.VISIBLE);
        bar.setMsgNum(2, 100);
        bar.setNewMarkVisibility(3, View.VISIBLE);
    }

}