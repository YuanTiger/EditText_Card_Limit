package com.my.oo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.my.oo.utils.EditTextUtils;
import com.my.oo.utils.MatcheUtils;
import com.my.oo.view.SpaceEditText;


public class MainActivity extends AppCompatActivity {

    private SpaceEditText et_credit_number;


    private Button bt_submit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initButton();
        initEditText();
    }

    //Button的初始化
    private void initButton() {
        bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, EditTextUtils.getTextTrim(et_credit_number), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //输入框的初始化
    private void initEditText() {

        et_credit_number = (SpaceEditText) findViewById(R.id.et_credit_number);


        et_credit_number.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(String text) {
                if(TextUtils.isEmpty(text)){
                    bt_submit.setEnabled(false);
                    return;
                }
                //判断是否满足银行卡格式，注意去空格判断
                if (MatcheUtils.isBankNumber(text.replaceAll(" ", ""))) {
                    bt_submit.setEnabled(true);
                    return;
                }
                bt_submit.setEnabled(false);
            }
        });




    }


}
