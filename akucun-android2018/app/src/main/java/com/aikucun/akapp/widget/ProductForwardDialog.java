package com.aikucun.akapp.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ProductForwardSettingActivity;


/**
 * 自定义dialog
 */
public class ProductForwardDialog extends Dialog {

    public ProductForwardDialog(Context context) {
        super(context);
    }

    public ProductForwardDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private Dialog mDialog;
        private boolean singleImgSelected = false, moreImgSelected = false;
        private boolean isShowSingleImgLayout = false, isShowMoreImgLayout = false;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 是否显示选择单图
         *
         * @param _isShowSingleImgLayout
         * @return
         */
        public Builder setIsShowSignleImgLayout(boolean _isShowSingleImgLayout) {
            isShowSingleImgLayout = _isShowSingleImgLayout;
            return this;
        }

        /**
         * 是否显示选择多图
         *
         * @param _isShowMoreImgLayout
         * @return
         */
        public Builder setIsShowMoreImgLayout(boolean _isShowMoreImgLayout) {
            isShowMoreImgLayout = _isShowMoreImgLayout;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 单图是否选中
         *
         * @return
         */
        public boolean getSingleImgSelected() {
            return singleImgSelected;
        }

        /**
         * 多图是否选中
         *
         * @return
         */
        public boolean getMoreImgSelected() {
            return moreImgSelected;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }


        public Dialog getDialog() {
            return mDialog;
        }

        @SuppressLint("NewApi")
        public ProductForwardDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ProductForwardDialog dialog = new ProductForwardDialog(context, R.style.mydialog);

            FrameLayout layout = (FrameLayout) inflater.inflate(
                    R.layout.dialog_product_forward_layout, null);
            LinearLayout content_layout = layout.findViewById(R.id.content_layout);
//            TextView titleTv = layout.findViewById(R.id.title_tv);
//            if (!TextUtils.isEmpty(title)) {
//                titleTv.setText(title);
//                titleTv.setVisibility(View.VISIBLE);
//            } else titleTv.setVisibility(View.GONE);

            final CheckBox signle_img_checkbox = layout.findViewById(R.id.signle_img_checkbox);
            final CheckBox more_img_checkbox = layout.findViewById(R.id.more_img_checkbox);

            if (!isShowMoreImgLayout) more_img_checkbox.setVisibility(View.GONE);
            if (!isShowSingleImgLayout) signle_img_checkbox.setVisibility(View.GONE);

            //设置默认选中的图片模式
            if (AppContext.get(ProductForwardSettingActivity.FORWARD_IMG_KEY, 1) == 2) {
                if (isShowMoreImgLayout){
                    moreImgSelected = true;
                    more_img_checkbox.setChecked(true);
                }
                signle_img_checkbox.setChecked(false);
            } else if (AppContext.get(ProductForwardSettingActivity.FORWARD_IMG_KEY, 1) == 3) {
                more_img_checkbox.setChecked(false);
                if (isShowSingleImgLayout){
                    singleImgSelected = true;
                    signle_img_checkbox.setChecked(true);
                }

            }

            signle_img_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    singleImgSelected = isChecked;
                    if (isChecked) {
                        more_img_checkbox.setChecked(false);
                    }
                }
            });
            more_img_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    moreImgSelected = isChecked;
                    if (isChecked) {
                        signle_img_checkbox.setChecked(false);
                    }
                }
            });

//            more_img_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    moreImgSelected = true;
//                    more_img_checkbox.setChecked(moreImgSelected);
//                    singleImgSelected = false;
//                    signle_img_checkbox.setChecked(singleImgSelected);
//                }
//            });
//            signle_img_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    moreImgSelected = false;
//                    more_img_checkbox.setChecked(moreImgSelected);
//                    singleImgSelected = true;
//                    signle_img_checkbox.setChecked(singleImgSelected);
//                }
//            });
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title

            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener
                                            .onClick(
                                                    dialog,
                                                    DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
                layout.findViewById(R.id.middle_line).setVisibility(View.GONE);

            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener
                                            .onClick(
                                                    dialog,
                                                    DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
                layout.findViewById(R.id.middle_line).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message))
                        .setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content_layout))
                        .removeAllViews();

                ((LinearLayout) layout.findViewById(R.id.content_layout))
                        .addView(contentView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT));
                ((LinearLayout) layout.findViewById(R.id.content_layout))
                        .setGravity(Gravity.CENTER_HORIZONTAL);

            }
            dialog.setContentView(layout);
            mDialog = dialog;
            return dialog;
        }

    }
}
