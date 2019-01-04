package io.madcamp.jh.madcamp_assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.facebook.AccessToken;

public class LoginHelper {
    public static AccessToken getCurrentToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public static boolean checkRegistered(Context context) {
        if(getCurrentToken() != null) return true;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Facebook에 로그인이 되어있지 않습니다.")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        return false;
    }
}
