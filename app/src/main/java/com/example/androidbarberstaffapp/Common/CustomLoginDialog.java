package com.example.androidbarberstaffapp.Common;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.androidbarberstaffapp.Interface.IDialogClickListener;
import com.example.androidbarberstaffapp.R;

public class CustomLoginDialog {
    public static CustomLoginDialog mDialog;
    public IDialogClickListener iDialogClickListener;

    public static CustomLoginDialog getInstance() {
        if(mDialog == null) {
            mDialog = new CustomLoginDialog();
        }
        return mDialog;
    }

    public void showLoginDialog(String title,
                                String positiveText,
                                String negativeText,
                                Context context,
                                IDialogClickListener iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login);

        // SET TITLE

    }
}
