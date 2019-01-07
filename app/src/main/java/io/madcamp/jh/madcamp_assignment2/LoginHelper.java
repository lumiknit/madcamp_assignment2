package io.madcamp.jh.madcamp_assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    public interface HttpPostRegisteredService {
        @POST("api/contacts/{id}")
        Call<ResponseBody> getUserRepositories(@Path("id") String _id);
    }

    public static void httpPostRegistered(final Context context) {
        if(!checkRegistered(context)) {
            Log.d("Test@httpPostRegistered", "Failed");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Test@POST", "Built");

        HttpPostRegisteredService service = retrofit.create(HttpPostRegisteredService.class);
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        Log.d("Test@userId", userId);

        Call<ResponseBody> request = service.getUserRepositories(userId);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Test@Retrofit", "Responsed");
                try {
                    Log.d("Test@Retrofit", response.body().string());
                } catch(Exception e) { e.printStackTrace(); }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }

}
