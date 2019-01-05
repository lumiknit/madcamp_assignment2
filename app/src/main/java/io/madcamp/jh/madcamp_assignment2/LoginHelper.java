package io.madcamp.jh.madcamp_assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.facebook.AccessToken;

public class LoginHelper {
    public static AccessToken getCurrentToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public static AlertDialog dialog;

    public static boolean checkRegistered(Context context) {
        removeDialog();
        if(getCurrentToken() != null) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Facebook에 로그인이 되어있지 않습니다.")
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
            dialog.show();
            return false;
        }
    }

    public static void removeDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}
