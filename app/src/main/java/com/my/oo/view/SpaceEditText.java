package com.my.oo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import com.my.oo.utils.AppUtils;
import com.my.oo.utils.EditTextUtils;

/**
 * Author：mengyuan
 * Date  : 2017/10/24下午2:13
 * E-Mail:mengyuanzz@126.com
 * Desc  :每4位自动添加在后面添加空格的EditText
 */

public class SpaceEditText extends AppCompatEditText {


    //上次输入框中的内容
    private String lastString;
    //光标的位置
    private int selectPosition;

    //输入框内容改变监听
    private TextChangeListener listener;



    public SpaceEditText(Context context) {
        super(context);
        initView();
    }

    public SpaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public SpaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private void initView() {
        addTextChangedListener(new TextWatcher() {
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
                if (start == 0 && count > 1 && SpaceEditText.this.getSelectionStart() == 0) {
                    return;
                }

                String textTrim = EditTextUtils.getTextTrim(SpaceEditText.this);
                if (TextUtils.isEmpty(textTrim)) {
                    return;
                }
                Log.i("mengyuan", "onTextChanged：s:" + s);
                Log.i("mengyuan", "onTextChanged：start:" + start);
                Log.i("mengyuan", "onTextChanged：before:" + before);
                Log.i("mengyuan", "onTextChanged：count:" + count);
                Log.i("mengyuan", "onTextChanged：getSelectionStart:" + SpaceEditText.this.getSelectionStart());
                Log.i("mengyuan", "onTextChanged：getSelectionEnd:" + SpaceEditText.this.getSelectionEnd());
                Log.i("mengyuan", "------------------------------------------------------------------------------------------");

                //如果 before >0 && count == 0,代表此次操作是删除操作
                if (before >0 && count == 0) {
                    selectPosition = start;
                    if (TextUtils.isEmpty(lastString)) {
                        return;
                    }
                    //将上次的字符串去空格 和 改变之后的字符串去空格 进行比较
                    //如果一致，代表本次操作删除的是空格
                    if (textTrim.equals(lastString.replaceAll(" ", ""))) {
                        //帮助用户删除该删除的字符，而不是空格
                        StringBuilder stringBuilder = new StringBuilder(lastString);
                        stringBuilder.deleteCharAt(start - 1);
                        selectPosition = start - 1;
                        SpaceEditText.this.setText(stringBuilder.toString());
                    }
                } else {
                    //此处代表是添加操作
                    //当光标位于空格之前，添加字符时，需要让光标跳过空格，再按照之前的逻辑计算光标位置
                    if ((start+count) % 5 == 0) {
                        selectPosition = start + count + 1;
                    } else {
                        selectPosition = start + count;
                    }
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                //获取输入框中的内容,不可以去空格
                String etContent = EditTextUtils.getText(SpaceEditText.this);
                if (TextUtils.isEmpty(etContent)) {
                    if (listener != null) {
                        listener.textChange("");
                    }
                    return;
                }
                //重新拼接字符串
                String newContent = AppUtils.addSpaceByCredit(etContent);
                //保存本次字符串数据
                lastString = newContent;

                //如果有改变，则重新填充
                //防止EditText无限setText()产生死循环
                if (!newContent.equals(etContent)) {
                    SpaceEditText.this.setText(newContent);
                    //保证光标的位置
                    SpaceEditText.this.setSelection(selectPosition > newContent.length() ? newContent.length() : selectPosition);
                }
                //触发回调内容
                if (listener != null) {
                    listener.textChange(newContent);
                }

            }
        });
    }


    /**
     * 输入框内容回调，当输入框内容改变时会触发
     */
    public interface TextChangeListener {
        void textChange(String text);
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.listener = listener;

    }
}
