package com.satmatgroup.safalsevakendra.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.satmatgroup.safalsevakendra.MainActivity
import com.satmatgroup.safalsevakendra.R
import com.satmatgroup.safalsevakendra.model.UserModel
import com.satmatgroup.safalsevakendra.network_calls.AppApiCalls
import com.satmatgroup.safalsevakendra.utils.*
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.LOGIN_API
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.MESSAGE
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.RESULT
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.TOKEN
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.TRUE
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.USER_MODEL
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnLogin
import kotlinx.android.synthetic.main.activity_login.progress_bar
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.layout_dialog_confirmotp.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class VerifyOTPActivity : AppCompatActivity() {

    var editText: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.status_bar, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        setContentView(R.layout.activity_otp)

        btnLogin.setOnClickListener {
            if (etOTP.text.toString().isNullOrEmpty()) {
                etOTP.requestFocus()
                etOTP.error = "Invalid OTP"
            } else {

                callServiceGetOTP(etOTP.text.toString())
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
            }

        }


    }




    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }


    private fun callServiceGetOTP( otp: String) {

        //creating a retrofit object.
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //creating the retrofit api service
        val apiService: Api = retrofit.create(Api::class.java)



        //creating a call object from the apiService
        val call: Call<SendOTPResponse?>? = apiService.sendOTPService(otp,otp,"SPI4999")

        //making the call to generate checksum
        call!!.enqueue(object : Callback<SendOTPResponse?> {
            override fun onResponse(call: Call<SendOTPResponse?>, response: Response<SendOTPResponse?>) {
                Toast.makeText(this@VerifyOTPActivity,response.body()!!.getMessage()!!,Toast.LENGTH_LONG).show()

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                //showBannerDailog(response.body().getQrcode().getUrl());
            }

            override fun onFailure(call: Call<SendOTPResponse?>, t: Throwable) {}

        })


        //   presenter.saveForm(files,String.valueOf(patientListResponse.getAddhId()),String.valueOf(formId),EditFormsActivity.this);
    }


}