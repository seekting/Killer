package com.seekting.killer.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

public class IpAddressTextView extends AppCompatEditText implements View.OnFocusChangeListener {
    private IPTextChangedListener mIPTextChangedListener;

    public void setPort(boolean port) {
        isPort = port;
    }

    private boolean isPort;

    public IpAddressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIPTextChangedListener = new IPTextChangedListener();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.addTextChangedListener(mIPTextChangedListener);
        setOnFocusChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeTextChangedListener(mIPTextChangedListener);
        setOnFocusChangeListener(null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            EditText editText = (EditText) v;
            if (TextUtils.isEmpty(editText.getText().toString())) {
                editText.getText().append("0");
            }
        }
    }


    class IPTextChangedListener implements TextWatcher {

        public IPTextChangedListener() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!hasFocus()) {
                if (TextUtils.isEmpty(s.toString())) {
                    s.append("0");
                    return;

                }
            }
            if (TextUtils.isEmpty(s.toString())) {
//                s.append("0");
                return;
            }
            int value = Integer.parseInt(s.toString());
            if (value < 0) {
                s.clear();
                s.append("0");
            }
            if (!isPort) {
                if (value > 255) {
                    s.clear();
                    s.append("255");
                }


            } else {
                if (value > 65534) {
                    s.clear();
                    s.append("65534");
                }
            }
        }


    }
}
