---
title: EditText每4位自动添加空格
date: 2017-05-19 16:32:31
tags:
---
## 基本功能 ##
刚拿到需求，很简单的一个功能，二话不说，很快就出来了：
![](http://7xvzby.com1.z0.glb.clouddn.com/et_1.gif)
完美！顺利上线！
没过几天领导拿着手机过来说：“这一堆数字在一起看着很费劲，像其他App一样，加个空格吧！“
小Kiss，当即就答应了下来。
## 拓展功能 ##
下面就来在基本功能上做拓展：每4位，自动添加空格。
看似很小的功能，在开发的过程中，遇到了非常多的问题与难点：
 - EditText输入框监听死循环
 - 输入框中的空格无法删除（删除又添加）
 - 从中间删除一个数字产生的一系列问题
 - 输入框光标位置的控制问题

之前踩坑的过程就不再赘述了，太心酸....
![](http://7xvzby.com1.z0.glb.clouddn.com/gaoxiao/%E6%83%A8%E4%B8%8D%E5%BF%8D%E7%9D%B9.png)
最后定下来的思路如下：
 1. 当输入框的内容改变时，就将内容取出拆分为一个一个的字符，在每4位的中间添加空格，最后一个4位不能添加。用这种拼接字符的方法是为了解决当用户删除中间的数字，回导致空格位置错位的问题。
 2. 当用户删除中间的字符时，要记录该动作并且记录光标位置，保证重新排序完成后，光标的位置在应该在的位置。

大概就这2步，就可以实现这个功能，下面一步一来,我们先实现空格的添加，保证内容永远满足4位后一个空格：
下面先看EditText的监听：
```
et_credit_number.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        //如果有改变，则重新填充
        //防止EditText无限setText()产生死循环
        if (!etContent.equals(newContent)) {
            et_credit_number.setText(newContent);
            //保证光标在最后，因为每次setText都会导致光标重置
            //这样最基本地解决了光标乱跳的问题
            et_credit_number.setSelection(newContent.length());
        }
        //判断是否满足信用卡格式，注意去空格判断
        if (MatcheUtils.isCreditNumber(newContent.replaceAll(" ", ""))) {
            bt_submit.setEnabled(true);
            return;
        }
        bt_submit.setEnabled(false);
    }
});
```
没有难点，重新拼接字符串我单独封装了出来：
```
public static String addSpeaceByCredit(String content) {
    if (TextUtils.isEmpty(content)) {
        return "";
    }
    //去空格
    content = content.replaceAll(" ", "");
    if (TextUtils.isEmpty(content)) {
        return "";
    }
    //卡号限制为16位
    if (content.length() > 16) {
        content = content.substring(0, 16);
    }
    StringBuilder newString = new StringBuilder();
    for (int i = 1; i <= content.length(); i++) {
        //当为第4位时，并且不是最后一个第4位时
        //拼接字符的同时，拼接一个空格
        //如果在最后一个第四位也拼接，会产生空格无法删除的问题
        //因为一删除，马上触发输入框改变监听，又重新生成了空格
        if (i % 4 == 0 && i != content.length()) {
            newString.append(content.charAt(i - 1) + " ");
        } else {
        //如果不是4位的倍数，则直接拼接字符即可
            newString.append(content.charAt(i - 1));

        }
    }
    return newString.toString();
}
```
这里每一步的含义，我都写了注释，应该问题不大，下面运行一下：
![](http://7xvzby.com1.z0.glb.clouddn.com/et_2.gif)
完美！空格正常添加了！
但是光标乱跳的问题，我特地演示了一下。
用字符排序的方式来做这个功能的原因是这个，当用户从中间删除字符时，我们需要将所有添加的空格位置都进行审查，并重新进行空格的添加，所以我认为重新排序字符是非常恰当的一种做法。当然这仅仅是我的愚见，可能有更优的做法。
现在我们就要进行第二步，当用户删除中间字符时，我们要判断用户本次操作是删除字符，并且保存本次删除的光标位置，在删除完成、排序完成之后，将光标移动到保存的光标位置。
思路有了，下面就看最终代码好了。
## 功能展示 ##
![](http://7xvzby.com1.z0.glb.clouddn.com/et_3.gif)
输入框监听的代码：
```
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
```
 这边主要利用了`onTextChanged()`的监听，判断用户操作时删除操作时，保存光标的位置。

## 小结 ##
项目我已经上传到了[我的GitHub](https://github.com/z593492734/EditText_Card_Limit)，有兴趣的同学可以去参考一下。
这个功能的坑远远超出了我的想象，我才不会说这个项目我就运行了100遍而已！
![](http://7xvzby.com1.z0.glb.clouddn.com/gaoxiao/%E9%80%80%E5%87%BA%E8%A3%85%E9%80%BC%E7%95%8C.jpg)
