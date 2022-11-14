package cn.sucang.widget.spinkit;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jarry on 16/12/5.
 */

public class SKProgressDialog extends Dialog
{
    private static SKProgressDialog progressDialog = null;

    public SKProgressDialog(Context context)
    {
        super(context);
    }

    public SKProgressDialog(Context context, int theme)
    {
        super(context, theme);
    }

    public static SKProgressDialog createDialog(Context context)
    {
        progressDialog = new SKProgressDialog(context, R.style.ProgressDialog);
        progressDialog.setContentView(R.layout.spinkit_progress_dialog);
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }

    public SKProgressDialog setTitleMessage(String strMessage)
    {
        TextView tvMsg = (TextView) progressDialog.findViewById(R.id.progress_text);

        if (tvMsg != null)
        {
            if (strMessage == null || strMessage.length() == 0)
            {
                tvMsg.setVisibility(View.GONE);
            }
            else
            {
                tvMsg.setText(strMessage);
            }
        }

        return progressDialog;
    }

    /**
     * 设置进度
     * @param progress
     * @return
     */
    public SKProgressDialog setProgress(String progress)
    {
        TextView upload_progress_tv = progressDialog.findViewById(R.id.upload_progress_tv);

        if (upload_progress_tv != null)
        {
            if (TextUtils.isEmpty(progress))
            {
                upload_progress_tv.setVisibility(View.INVISIBLE);
            }
            else
            {
                upload_progress_tv.setText(progress);
                upload_progress_tv.setVisibility(View.VISIBLE);
            }
        }

        return progressDialog;
    }



}
