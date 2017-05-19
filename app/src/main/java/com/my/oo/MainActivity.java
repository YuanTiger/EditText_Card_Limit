package com.my.oo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.my.oo.utils.AppUtils;
import com.my.oo.utils.EditTextUtils;
import com.my.oo.utils.MatcheUtils;


public class MainActivity extends AppCompatActivity {

    private EditText et_credit_number;


    private Button bt_submit;


    private String lastString;//上次输入框中的内容
    private int deleteSelect;//删除的光标位置


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

        et_credit_number = (EditText) findViewById(R.id.et_credit_number);


        et_credit_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //因为重新排序之后setText的存在
                //会导致输入框的内容从0开始输入，这里是为了避免这种情况产生一系列问题
                if (start == 0 && count > 0) {
                    return;
                }
                String editTextContent = EditTextUtils.getText(et_credit_number);
                if (TextUtils.isEmpty(editTextContent) || TextUtils.isEmpty(lastString)) {
                    return;
                }

                editTextContent = AppUtils.addSpeaceByCredit(editTextContent);
                //如果最新的长度 < 上次的长度，代表进行了删除
                if (editTextContent.length() <= lastString.length()) {
                    deleteSelect = start;
                } else {
                    deleteSelect = editTextContent.length();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //获取输入框中的内容,不可以去空格
                String etContent = EditTextUtils.getText(et_credit_number);
                if (TextUtils.isEmpty(etContent)) {
                    bt_submit.setEnabled(false);
                    return;
                }
                //重新拼接字符串
                String newContent = AppUtils.addSpeaceByCredit(etContent);
                //保存本次字符串数据
                lastString = newContent;
                //如果有改变，则重新填充
                //防止EditText无限setText()产生死循环
                if (!etContent.equals(newContent)) {
                    et_credit_number.setText(newContent);
                    //保证光标的位置
                    et_credit_number.setSelection(deleteSelect > newContent.length() ? newContent.length() : deleteSelect);
                }
                //判断是否满足信用卡格式，注意去空格判断
                if (MatcheUtils.isCreditNumber(newContent.replaceAll(" ", ""))) {
                    bt_submit.setEnabled(true);
                    return;
                }
                bt_submit.setEnabled(false);
            }
        });

    }


}
