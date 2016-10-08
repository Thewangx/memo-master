package com.giot.memo.util;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.giot.memo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * 计算器键盘
 * Created by reed on 16/8/5.
 */
public class KeyboardUtil implements KeyboardView.OnKeyboardActionListener {

    private Activity mActivity;
    private EditText mEditText;
    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;
    private char operator;

    public KeyboardUtil(Activity mActivity, EditText mEditText) {
        this.mActivity = mActivity;
        this.mEditText = mEditText;
        mKeyboard = new Keyboard(mActivity, R.xml.keyboard);
        mKeyboardView = (KeyboardView) mActivity.findViewById(R.id.keyboardView_money);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(this);
        //解决与系统键盘的布局冲突问题
        mKeyboardView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //第一次加载界面时
                if (oldTop == 0 || top == oldTop) {
                    return;
                }
                if (top > oldTop) {
                    showKeyboard();
                } else {
                    hideKeyboard();
                }
            }
        });

    }


    /**
     * 软键盘展示状态
     */
    public boolean isShow() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * 软键盘展示
     */
    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 软键盘隐藏
     */
    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 禁掉系统软键盘
     */
    public void hideSoftInputMethod() {
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }
        if (methodName == null) {
            mEditText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEditText, false);
            } catch (NoSuchMethodException e) {
                mEditText.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException|InvocationTargetException|IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Editable editable = mEditText.getText();
        int start = mEditText.getSelectionStart();
        switch (primaryCode) {
            case -1://清空键
                editable.delete(0, editable.length());
                operator = '\u0000';
                break;
            case -2://删除键
                if (start > 0) {
                    if (!Character.isDigit(editable.charAt(start - 1)) && editable.charAt(start - 1) != '.') {
                        operator = '\u0000';
                        mKeyboard.getKeys().get(19).icon = ContextCompat.getDrawable(mActivity, R.mipmap.confirm);
                        mKeyboardView.setKeyboard(mKeyboard);
                    }
                    editable.delete(start - 1, start);
                }
                break;
            case -3://关闭键(关闭界面)
                mActivity.finish();
                break;
            case -4://确认键
                if (isNotInputOperator()) {//等号按钮功能
                    getResult();
                } else {
                    if (completeListener != null) {
                        completeListener.onComplete();
                    }
                }
                break;
            case -5://空格键(暂时不需要)
                break;
            case 43://加号键
            case 45://减号键
            case 42://乘号键
            case 47://除号键
                if (start > 0 && Character.isDigit(editable.charAt(start - 1))) {
                    if (isNotInputOperator()) {
                        getResult();
                        start = mEditText.getSelectionStart();
                        editable = mEditText.getEditableText();
                    }
                    operator = (char) primaryCode;
                    mKeyboard.getKeys().get(19).icon = ContextCompat.getDrawable(mActivity, R.mipmap.equal);
                    mKeyboardView.setKeyboard(mKeyboard);
                    editable.insert(start, Character.toString((char) primaryCode));
                }
                break;
            case 46://小数点键
                if (isNotInputPoint()) {
                    break;
                }
                if (start == 0 || !Character.isDigit(editable.charAt(start - 1))) {
                    editable.insert(start, "0");
                    start++;
                }
            default://数字键
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    // 计算结果
    private void getResult() {
        // 计算结果
        String str = null;
        double result = 0;
        double d1 = 0;
        double d2 = 0;
        try {
            str = mEditText.getText().toString();
            result = 0;
            int index = str.indexOf(operator);
            if (index == str.length()){
                return;
            }
            d1 = Double.parseDouble(str.substring(0, index));
            d2 = Double.parseDouble(str.substring(index + 1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        switch (operator) {
            case '+':
                result = d1 + d2;
                break;
            case '-':
                result = d1 - d2;
                break;
            case '*':
                result = d1 * d2;
                break;
            case '/':
                if (d2 == 0) {
                    result = 0;
                } else {
                    result = d1 / d2;
                }
                break;
        }
        // 如果不包含小数点则为小数和除法运算
        String value;
        if (!str.contains(".") && operator != '/') {
            value = String.valueOf((int) result);
        } else {
            value = String.valueOf(new DecimalFormat("0.00").format(result));
            if (value.charAt(value.length() - 1) == '0') {//判断末尾是否为0
                value = value.substring(0, value.length() - 1);
                if (value.charAt(value.length() - 1) == '0') {//判断倒数第二位是否为0
                    value = value.substring(0, value.length() - 2);
                }
            }
        }
        mEditText.setText(value);
        mEditText.setSelection(mEditText.getText().length());
        mKeyboard.getKeys().get(19).icon = ContextCompat.getDrawable(mActivity, R.mipmap.confirm);
        mKeyboardView.setKeyboard(mKeyboard);

    }


    // 提交账单时的回调
    public interface Callback {
        void onComplete();
    }

    public Callback completeListener;

    public void setCompleteListener(Callback completeListener) {
        this.completeListener = completeListener;
    }

    /**
     * 判断能否不能继续输入运算符号
     * @return 如果能继续输入, 返回false;否则返回true
     */
    private boolean isNotInputOperator() {
        String temp = mEditText.getText().toString();
        return temp.contains("+") || temp.contains("-") || temp.contains("*") || temp.contains("/");
    }

    /**
     * 判断当前光标位置是否不能输入小数点
     * @return 如果能继续输入, 返回false;如果不能, 返回true
     */
    private boolean isNotInputPoint() {
        String temp = mEditText.getText().toString();
        if (operator != '\u0000') {
            int operatorIndex = temp.indexOf(operator);
            int index = mEditText.getSelectionStart();
            if (index > temp.indexOf(operator)) {
                return temp.substring(operatorIndex + 1).contains(".");
            } else {
                return temp.substring(0, operatorIndex).contains(".");
            }
        } else {
            return temp.contains(".");
        }
    }
}
