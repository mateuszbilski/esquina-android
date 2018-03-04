package com.squeezedlemon.esquina.client.android.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.squeezedlemon.esquina.client.android.R;

public class LoaderFragment extends DialogFragment {

    private CancelListener listener;

    public interface CancelListener {
        public void canceled();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_loader, null);

        final Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);

        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setContentView(view);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        return dialog;
    }

    public void addCancelListener(CancelListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d("LoaderDialog", "Canceled");
        super.onCancel(dialog);
        if (listener != null) {
            listener.canceled();
        }
    }
}
