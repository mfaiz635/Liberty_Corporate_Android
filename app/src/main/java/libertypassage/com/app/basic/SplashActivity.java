package libertypassage.com.app.basic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import libertypassage.com.app.R;
import libertypassage.com.app.models.CountryDetail;
import libertypassage.com.app.models.DetailIndustryProf;
import libertypassage.com.app.models.IndustryProfessions;
import libertypassage.com.app.models.ModelCountryList;
import libertypassage.com.app.utilis.ApiInterface;
import libertypassage.com.app.utilis.ClientInstance;
import libertypassage.com.app.utilis.Constants;
import libertypassage.com.app.utilis.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.Q)
public class SplashActivity extends Activity {

    private Context context;
    private Handler handler = new Handler();
    private String TAG = SplashActivity.class.getSimpleName();
    private List<CountryDetail> countryDetails = new ArrayList<CountryDetail>();

    ArrayList<DetailIndustryProf> ipArrayList = new ArrayList<DetailIndustryProf>();
    private String token;
    private static final int PERMISSION_CODE = 100;
    String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;
        int TIME_OUT = 2000;

        // get Device Id
        String DeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Utility.setSharedPreference(context, Constants.KEY_DEVICEID, DeviceId);
        token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN);


        if (Utility.hasPermissionInManifest(SplashActivity.this, PERMISSION_CODE, permission)) {
            handler.postDelayed(runnable, TIME_OUT);
        }

    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("Splash", "token : " + token);
            if (!token.equals("")) {
                String start = Utility.getSharedPreferences(context, Constants.KEY_START);
                 if (start.equals("1")) {
                    startActivity(new Intent(context, EnrolmentDeclaration.class));
                    finish();
                } else if (start.equals("2")) {
                    startActivity(new Intent(context, Acknowledgement.class));
                    finish();
                } else if (start.equals("3")) {
                    startActivity(new Intent(context, MyTemperature.class));
                    finish();
                } else if (start.equals("4")) {
                    startActivity(new Intent(context, AddAddress.class));
                    finish();
                 } else if (start.equals("6")) {
                     startActivity(new Intent(context, VerifyOtpEmailUpadteProfile.class).putExtra("from", "splash"));
                     finish();
                } else if (start.equals("5")) {
                    startActivity(new Intent(context, HomePage.class));
                    finish();
                } else {
                    startActivity(new Intent(context, HomePage.class));
                    finish();
                }
            } else {
                if (Utility.getSharedPreferences(context, "isVerify").equals("signup")) {
                    Intent i = new Intent(context, VerifyOTPSignUp.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("otpEmail")) {
                    Intent i = new Intent(context, VerifyOtpEmailSignUp.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("forgot")) {
                    Intent i = new Intent(context, VerifyOTPForgotPassword.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else if (Utility.getSharedPreferences(context, "isVerify").equals("forgotEmail")) {
                    Intent i = new Intent(context, VerifyOtpEmailForgotPassword.class);
                    i.putExtra("from", "splash");
                    startActivity(i);
                    finish();
                } else {
                    Utility.setSharedPreference(context, Constants.KEY_FOR_TITLE, "Required LogIn for update your status");
                    if (Utility.isConnectingToInternet(context)) {
                        getCountry();
                    } else {
                        Intent i = new Intent(context, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }

//            Intent intent = new Intent(context, TestingActivity.class);
//            startActivity(intent);
//            finish();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasAllPermissions = false;

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    hasAllPermissions = true;
                } else {
                    hasAllPermissions = false;
                    handler.postDelayed(runnable, 2000);
                }

                if (hasAllPermissions) {
                    handler.postDelayed(runnable, 2000);
                }
                break;

        }
    }

    private void getCountry() {
        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ModelCountryList> call = apiInterface.getCountries(Constants.KEY_BOT);

        call.enqueue(new Callback<ModelCountryList>() {
            @Override
            public void onResponse(Call<ModelCountryList> call, Response<ModelCountryList> response) {
//                Utility.stopProgressDialog(context);
                ModelCountryList model = response.body();
                Log.e("Country", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {

                    countryDetails = model.getDetails();
                    if (countryDetails != null) {
                        Utility.saveCountryList(context, countryDetails);
                    }
                    getIndustryProfessions();



                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    finish();
//                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelCountryList> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
                finish();
//                 Log.e("model", "onFailure    " + t.getMessage());
//                 Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getIndustryProfessions() {
//        Utility.showProgressDialog(context);
        ApiInterface apiInterface = ClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<IndustryProfessions> call = apiInterface.getIndustryProfessions(Constants.KEY_BOT, "1");

        call.enqueue(new Callback<IndustryProfessions>() {
            @Override
            public void onResponse(Call<IndustryProfessions> call, Response<IndustryProfessions> response) {
                Utility.stopProgressDialog(context);
                IndustryProfessions model = response.body();
                Log.e("modelChangeStaus", new Gson().toJson(model));
                if (model != null && model.getError().equals(false)) {
                    List<DetailIndustryProf> ipList = new ArrayList<DetailIndustryProf>();
                    ipList.clear();
                    ipList = model.getDetails();

                    ipArrayList.clear();
                    ipArrayList.add(new DetailIndustryProf(0, 1, "Select Profession"));
                    ipArrayList.addAll(ipList);
                    if (ipArrayList != null) {
                        Utility.saveIndustryProfession(context, ipArrayList);
                    }
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    finish();


                } else if (model != null && model.getError().equals(true)) {
                    Utility.stopProgressDialog(context);
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<IndustryProfessions> call, Throwable t) {
                Utility.stopProgressDialog(context);
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

}