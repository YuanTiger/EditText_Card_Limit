package com.my.oo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

    //上次输入框中的内容
    private String lastString;
    //光标的位置
    private int selectPosition;


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


            /**
             * 当输入框内容改变时的回调
             * @param s  改变后的字符串
             * @param start 改变之后的光标下标
             * @param before 删除了多少个字符
             * @param count 添加了多少个字符
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                //因为重新排序之后setText的存在
                //会导致输入框的内容从0开始输入，这里是为了避免这种情况产生一系列问题
                if (start == 0 && count > 0) {
                    return;
                }
                Log.i("mengyuan", "onTextChanged：s:" + s);
                Log.i("mengyuan", "onTextChanged：start:" + start);
                Log.i("mengyuan", "onTextChanged：before:" + before);
                Log.i("mengyuan", "onTextChanged：count:" + count);
                Log.i("mengyuan", "onTextChanged：getSelectionStart:" + et_credit_number.getSelectionStart());
                Log.i("mengyuan", "onTextChanged：getSelectionEnd:" + et_credit_number.getSelectionEnd());


                String textTrim = EditTextUtils.getTextTrim(et_credit_number);
                if (TextUtils.isEmpty(textTrim)) {
                    return;
                }
                //如果before > 0,代表此次操作是删除操作
                if (before > 0) {
                    selectPosition = start;
                    if (TextUtils.isEmpty(lastString)) {
                        return;
                    }
                    //将上次的字符串去空格 和 改变之后的字符串去空格 进行比较
                    //如果一致，代表本次操作删除的是空格
                    if (textTrim.equals(lastString.replaceAll(" ", ""))) {
                        //帮助用户删除该删除的字符，而不是空格
                        StringBuffer stringBuffer = new StringBuffer(textTrim);
                        stringBuffer.deleteCharAt(start - 1);
                        selectPosition = start - 1;
                        et_credit_number.setText(stringBuffer.toString());
                    }
                } else {
                    //此处代表是添加操作
                    //当光标位于空格之前，添加字符时，需要让光标跳过空格，再按照之前的逻辑计算光标位置
                    //第一次空格出现的位置是4，第二次是4+1(空格)+4=9，第三次是4+1(空格)+4+1(空格)+4=14
                    if (start == 4 || start == 9 || start == 14) {
                        selectPosition = start + count + 1;
                    } else {
                        selectPosition = start + count;
                    }
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
                    et_credit_number.setSelection(selectPosition > newContent.length() ? newContent.length() : selectPosition);
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
