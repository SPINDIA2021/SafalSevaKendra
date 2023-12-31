package com.satmatgroup.safalsevakendra

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.satmatgroup.safalsevakendra.activities_aeps.AepsOnboardActivity
import com.satmatgroup.safalsevakendra.activities_aeps.AepsTransactionActivity
import com.satmatgroup.safalsevakendra.activities_aeps.CashDepositOtpActivity
import com.satmatgroup.safalsevakendra.activities_bbps.BbpsMainActivity
import com.satmatgroup.safalsevakendra.adapters_recyclerview.SliderAdapter
import com.satmatgroup.safalsevakendra.authentication.LoginActivity
import com.satmatgroup.safalsevakendra.category.CategoryActivity
import com.satmatgroup.safalsevakendra.daybook.DayBookTabActivity
import com.satmatgroup.safalsevakendra.dmt.DmtLoginActivity
import com.satmatgroup.safalsevakendra.dmt.SelectDmtActivity
import com.satmatgroup.safalsevakendra.dmt.SelectDmtModel
import com.satmatgroup.safalsevakendra.fund_services.AddMoneyActivity
import com.satmatgroup.safalsevakendra.fund_services.RequestFundsActivity
import com.satmatgroup.safalsevakendra.fund_services.TransferFundsActivity
import com.satmatgroup.safalsevakendra.microatm.MicroAtmActivity
import com.satmatgroup.safalsevakendra.model.BannerModel
import com.satmatgroup.safalsevakendra.model.UserModel
import com.satmatgroup.safalsevakendra.navigation_activities.SupportActivity
import com.satmatgroup.safalsevakendra.network_calls.AppApiCalls
import com.satmatgroup.safalsevakendra.payout.PayoutActivity
import com.satmatgroup.safalsevakendra.recharge_services.DthRechargeActivity
import com.satmatgroup.safalsevakendra.recharge_services.ElectricityRechargeActivity
import com.satmatgroup.safalsevakendra.recharge_services.LandLineRechargeActivity
import com.satmatgroup.safalsevakendra.recharge_services.MobileRechargeActivity
import com.satmatgroup.safalsevakendra.reports.ReportsActivity
import com.satmatgroup.safalsevakendra.services_and_products.BuyProductsActivity
import com.satmatgroup.safalsevakendra.services_and_products.ProductsAndServicesHistory
import com.satmatgroup.safalsevakendra.user_profile.KycVerificationActivity
import com.satmatgroup.safalsevakendra.user_profile.ProfileActivity
import com.satmatgroup.safalsevakendra.users.MyUsersActivity
import com.satmatgroup.safalsevakendra.utils.*
import com.satmatgroup.safalsevakendra.utils.AppCommonMethods.Companion.logoutOnExpiredDialog
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.NEWS
import com.satmatgroup.safalsevakendra.utils.AppConstants.Companion.WALLETBALANCE
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_buy_or_book.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progress_bar
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {
    //#1 Defining a BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    val MICRO_ATMLOGIN = "MICRO_ATMLOGIN"
    val CHECK_KYC_STATUS = "CHECK_KYC_STATUS"

    private val REQUEST_CODE = 1

    //Navigation Variables
    var position = 0
    var menuItemID = 0

    lateinit var userModel: UserModel
    lateinit var bannerModel: BannerModel
    var bannerModelArrayList: ArrayList<BannerModel>? = null
    var Hash_file_maps: HashMap<String, String> = HashMap()
    private val USER_LOGOUT: String = "USER_LOGOUT"
    private val SERVICE_STATUS: String = "SERVICE_STATUS"
    private val BUY_SERVICE: String = "BUY_SERVICE"
    private val SERVICE_AMOUNT: String = "SERVICE_AMOUNT"
    private val OFFER_POPUP: String = "OFFER_POPUP"
    var deviceId: String = ""
    var deviceNameDet: String? = ""
    var service: String? = ""
    var serviceName: String? = ""
    lateinit var amount: String
    var newaepskyc_status = ""
    var aeps_kyc_status = ""


    lateinit var offer_id: String
    lateinit var product_name: String
    lateinit var product_description: String
    lateinit var product_amount: String
    lateinit var product_image: String
    lateinit var product_offer_amount: String
    lateinit var stock: String
    lateinit var available_quantity: String
    lateinit var offer_added_date: String

    var latitudeLabel: String=""
    var longitudeLabel: String=""

    var intent1: Intent? = null
    var gpsStatus = false

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.status_bar, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        setContentView(R.layout.activity_main)
        val getMobileSize: Double = CommonMethods.getDisplaySize(this)!!.toDouble()
        Log.d(getString(R.string.scrern_sixe), getMobileSize.toString())

        checkGpsStatus()

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

        getBalanceApi(userModel.cus_mobile)
        getAepsBalanceApi(userModel.cus_id)
        offerPopup()
        dashboardApi(userModel.cus_mobile)
/*        cvViewBalance.setOnClickListener {
            userModel = gson.fromJson(json, UserModel::class.java)
            getBalanceApi(userModel.cus_mobile)
        }

        cvViewAepsBalance.setOnClickListener {
            userModel = gson.fromJson(json, UserModel::class.java)
            getAepsBalanceApi(userModel.cus_id)
        }*/
        tv_payout_title.setText(resources.getString(R.string.app_name)+" Balance")


        ll_JKBalance.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // TODO Auto-generated method stub
                if (ll_wallet_balance.visibility == GONE && ll_aeps_balance.visibility == GONE) {
                    ll_wallet_balance.visibility = VISIBLE
                    ll_aeps_balance.visibility = VISIBLE
                    downArrow.visibility = GONE
                    upArrow.visibility = VISIBLE
                }
                else if (ll_wallet_balance.visibility == VISIBLE && ll_aeps_balance.visibility == VISIBLE) {
                    ll_wallet_balance.visibility = GONE
                    ll_aeps_balance.visibility = GONE
                    upArrow.visibility = GONE
                    downArrow.visibility = VISIBLE
                }
            }
        })


        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE
        )
        //**************NAVIGATION START*********************//
        ivHamburgerBtn.setOnClickListener { openDrawer() }



        navigation_view.rl_support.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }


        navigation_view.tvCustName.setText(userModel.cus_name)
        navigation_view.tvCustMobile.setText(userModel.cus_mobile)

        navigation_view.rl_logOut.setOnClickListener {
            showLogout()
        }

        //**************NAVIGATION END*********************//


        /* //#2 Initializing the BottomSheetBehavior
         bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
         if (getMobileSize > 5.01) {
             bottomSheetBehavior.peekHeight = 800

         } else {

             bottomSheetBehavior.peekHeight = 250

         }

         //#3 Listening to State Changes of BottomSheet
         bottomSheetBehavior.addBottomSheetCallback(object :
             BottomSheetBehavior.BottomSheetCallback() {
             override fun onSlide(bottomSheet: View, slideOffset: Float) {
                 BottomSheetBehavior.STATE_COLLAPSED

             }

             override fun onStateChanged(bottomSheet: View, newState: Int) {

             }
         })


         //#4 Changing the BottomSheet State on ButtonClick
         custToolbar.setOnClickListener {
             if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                 bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

             }
         }*/
        initView()
        //INTENT CLICK LISTENERS
        activityIntents()


    }


    private fun checkGpsStatus() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {

            getLocation()
        } else {
            gpsStatus()
            toast("GPS is Disabled")
        }
    }

    fun gpsStatus() {
        intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5000, 5f, locationListener)
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            Log.d("TAG", "onLocationChanged: "+"Latitude: " + location.latitude)
            Log.d("TAG", "onLocationChanged: "+"Longitude: " + location.longitude)

            latitudeLabel = location.latitude.toString()
            longitudeLabel = location.longitude.toString()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //API CALL FUNCTION DEFINITION
    private fun dashboardApi(
        cus_id: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.DASHBOARD_API,
                this
            )
            mAPIcall.dashboard(cus_id)

        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun getBalanceApi(
        cus_id: String
    ) {
/*        progress_bar_balance.visibility = View.VISIBLE
        rl_wallet_balance.visibility = GONE
        cvViewBalance.visibility = GONE*/
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.BALANCE_API,
                this
            )
            mAPIcall.getBalance(cus_id)

        } else {
            toast(getString(R.string.error_internet))
        }
    }


    private fun getServiceStatus(
        cusid: String,
        service: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, SERVICE_STATUS, this)
            mAPIcall.getServiceStatus(
                cusid, service
            )
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getServiceAmount(
        service: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, SERVICE_AMOUNT, this)
            mAPIcall.getServiceAmount(
                service
            )
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buyService(
        cus_id: String,
        service: String,
        amount: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, BUY_SERVICE, this)
            mAPIcall.buyService(
                cus_id, service, amount
            )
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAepsBalanceApi(
        cus_id: String
    ) {
/*        progress_bar_aeps.visibility = View.VISIBLE
        rl_aeps_balance.visibility = GONE
        cvViewAepsBalance.visibility = GONE*/
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.AEPS_BALANCE_API,
                this
            )
            mAPIcall.getAepsBalance(cus_id)

        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun userLogout(
        cusid: String,
        deviceId: String,
        deviceNameDet: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, USER_LOGOUT, this)
            mAPIcall.userLogout(
                cusid, deviceId, deviceNameDet, pin,
                pass, cus_mobile, cus_type
            )
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun offerPopup(

    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, OFFER_POPUP, this)
            mAPIcall.offerPopup(
            )
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }


    //************************************************************************************************//
    //INTENT CLICK FUNCTIONS
    fun activityIntents() {
        rl_mobileRecharge.setOnClickListener {
            service = "recharge_service"
            getServiceStatus(userModel.cus_id, "recharge_service")


        }
        rl_dthRecharge.setOnClickListener {
            val intent = Intent(this, DthRechargeActivity::class.java)
            startActivity(intent)

        }
        rl_landlineRecharge.setOnClickListener {
            val intent = Intent(this, LandLineRechargeActivity::class.java)
            startActivity(intent)

        }

        rl_manageusers.setOnClickListener {
            val intent = Intent(this, MyUsersActivity::class.java)
            startActivity(intent)
        }
        tvRedeem.setOnClickListener {
            val intent = Intent(this, PayoutActivity::class.java)
            startActivity(intent)
        }
        rl_billPay.setOnClickListener {
            val intent = Intent(this, BbpsMainActivity::class.java)
            startActivity(intent)
        }

        rl_products.setOnClickListener {
            val intent = Intent(this, BuyProductsActivity::class.java)
            startActivity(intent)
        }

        rl_electricity.setOnClickListener {
            service = "bill_service"
            getServiceStatus(userModel.cus_id, "bill_service")
        }

        rl_CashDepositOtp.setOnClickListener {
            val intent = Intent(this, CashDepositOtpActivity::class.java)
            startActivity(intent)
        }

        rl_aeps.setOnClickListener {
            service = "aeps_service"
            getServiceStatus(userModel.cus_id, "aeps_service")
        }
        rl_dmt.setOnClickListener {
            service = "dmt_service"
            getServiceStatus(userModel.cus_id, "dmt_service")
/*            val intent = Intent(this, SelectDmtActivity::class.java)
            startActivity(intent)*/
        }

        rl_aadharpay.setOnClickListener {
            service = "adharpay_service"
            getServiceStatus(userModel.cus_id, "adharpay_service")
        }

        rl_microatm.setOnClickListener {
            service = "microatm_service"
            getServiceStatus(userModel.cus_id, "microatm_service")

        }
        rl_requestmoney.setOnClickListener {
            val intent = Intent(this, RequestFundsActivity::class.java)
            startActivity(intent)

        }
        rl_addmoneytowallet.setOnClickListener {
            val intent = Intent(this, AddMoneyActivity::class.java)
            startActivity(intent)

        }

        rl_CashDeposit.setOnClickListener {
           service = "cashdeposite_service"
            getServiceStatus(userModel.cus_id, "cashdeposite_service")
        }

        rl_purchaseHistory.setOnClickListener {
            val intent = Intent(this, ProductsAndServicesHistory::class.java)
            startActivity(intent)
        }

        rl_transferMoney.setOnClickListener {
            val intent = Intent(this, TransferFundsActivity::class.java)
            startActivity(intent)

        }

        rl_category.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)

        }

        cvViewReport.setOnClickListener {
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
        }
        cvViewDayBook.setOnClickListener {

            /*          val bundle = Bundle()
                      bundle.putString(
                          "url",
                          "https://edigitalvillage.net/Retailer/cashwithdrawalaeps_print/131"
                      )

                      val intent = Intent(this, InvoiceViewActivity::class.java)
                      intent.putExtras(bundle)
                      startActivity(intent)*/


            val intent = Intent(this, DayBookTabActivity::class.java)
            startActivity(intent)
        }

        custToolbar.ivProfileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }


    //*************NAVIGATION METHODS*************//

    fun openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START)
    }

    private fun initView() {
        setUpDrawerLayout()
        // navigation_view..setText(userModel.cust_name)


//        cvViewBalance.setBackgroundResource(R.drawable.bg_kyc_verified)
//        cvViewAepsBalance.setBackgroundResource(R.drawable.bg_kyc_verified)

//        cvViewDayBook.setBackgroundResource(R.drawable.bg_buttons)
//        cvViewReport.setBackgroundResource(R.drawable.bg_buttons)
        tvNews.isSelected = true
        navigation_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
            this, mDrawerLayout, null,
            R.string.drawerOpen,
            R.string.drawerClose
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun showDialogOffer(
        serviceName: String,
        amount: String
    ) {
        //getServiceAmount(serviceName)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.order_details_layout)

        val tvServiceName = dialog.findViewById(R.id.tvServiceName) as TextView
        val tv_amount = dialog.findViewById(R.id.tvServiceAmount) as TextView
        val tv_pay = dialog.findViewById(R.id.tv_pay) as TextView
        val tv_no = dialog.findViewById(R.id.tv_no) as TextView

        tvServiceName.setText(serviceName)

        tv_amount.setText("₹"+amount)

        tv_pay.setOnClickListener {
            buyService(userModel.cus_id, service.toString(), amount.toString())
            dialog.dismiss()
        }
        tv_no.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showPopupOffer(
        product_name: String,
        product_description: String,
        product_amount: String,
        product_image: String,
        product_offer_amount: String,
        available_quantity: String
    ) {
        //getServiceAmount(serviceName)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_popup_offer)

        val ivOfferImage = dialog.findViewById(R.id.ivOfferImage) as ImageView
        val tvOfferProduct = dialog.findViewById(R.id.tvOfferProduct) as TextView
        val tvOfferDesc = dialog.findViewById(R.id.tvOfferDesc) as TextView
        val tvOfferPrice = dialog.findViewById(R.id.tvOfferPrice) as TextView
        val tvOfferQuantity = dialog.findViewById(R.id.tvOfferQuantity) as TextView
        val btnOk = dialog.findViewById(R.id.btnOk) as TextView
        val btnBuy = dialog.findViewById(R.id.btnBuy) as TextView

        tvOfferProduct.setText(product_name)
        tvOfferDesc.setText(product_description)
        tvOfferPrice.setText(resources.getString(R.string.Rupee).toString()+product_offer_amount+"/- (save "+resources.getString(R.string.Rupee)+(product_amount.toInt()-product_offer_amount.toInt())+")")
        tvOfferQuantity.setText(available_quantity)

        Glide.with(this)
            .load(product_image)
            .into(ivOfferImage)

        btnBuy.setOnClickListener {
            val intent = Intent(this,BuyProductsActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        btnOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    //*************END*************//

    @SuppressLint("SetTextI18n")
    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            bannerModelArrayList = ArrayList()
            progress_bar.visibility = GONE
            Log.e(AppConstants.DASHBOARD_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                /* tvWalletBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(WALLETBALANCE)}"
                 tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/
                tvNews.text = jsonObject.getString(NEWS)

                try{
                    val banners = jsonObject.getJSONArray(AppConstants.BANNER)
                    for (i in 0 until banners.length()) {
                        val notifyObjJson = banners.getJSONObject(i)
                        bannerModel = Gson()
                            .fromJson(notifyObjJson.toString(), BannerModel::class.java)
                        bannerModelArrayList!!.add(bannerModel)

                    }
                }catch (e: Exception)
                {
                    e.printStackTrace()
                }


                val cusData = jsonObject.getJSONArray("cusData")
                for (i in 0 until cusData.length()) {
                    val notifyObjJson = cusData.getJSONObject(i)
                    Log.e("newaepskyc_status:  ", notifyObjJson.getString("newaepskyc_status"))
                    Log.e("aeps_kyc_status:  ",notifyObjJson.getString("aeps_kyc_status"))

                    newaepskyc_status = notifyObjJson.getString("newaepskyc_status")
                    aeps_kyc_status = notifyObjJson.getString("aeps_kyc_status")


                }

                for (product in bannerModelArrayList!!) {
                    Hash_file_maps.put(product.bid, product.image)
                }

                //  val posters = resources.obtainTypedArray(bannerModelArrayList)

                //TODO REMOVED THIS LATER
//                viewPager.adapter = BannerAdapter(bannerModelArrayList!!, this)

// passing this array list inside our adapter class.

                // passing this array list inside our adapter class.
                val adapter = SliderAdapter(bannerModelArrayList!!, this)
                sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_RTL)
                sliderView.setSliderAdapter(adapter)
                sliderView.setScrollTimeInSec(3)

                sliderView.setAutoCycle(true)
                sliderView.startAutoCycle()

                val user = jsonObject.getJSONArray(AppConstants.CUS_DATA)
                for (i in 0 until user.length()) {
                    val notifyObjJson = user.getJSONObject(i)
                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }
                navigation_view.tvCustName.setText(userModel.cus_name)
                navigation_view.tvCustMobile.setText(userModel.cus_email)

                if (userModel.cus_type.equals("retailer", ignoreCase = true)) {
                    rl_manageusers.visibility = GONE

                } else {

                    rl_manageusers.visibility = VISIBLE

                }

            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(AppConstants.BALANCE_API)) {
            //progress_bar_balance.visibility = GONE
            Log.e(AppConstants.BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                /*  rl_wallet_balance.visibility = VISIBLE
                cvViewBalance.visibility = GONE*/
                tvWalletBalance.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString(WALLETBALANCE)}"
                /* tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/


            } else {
/*                progress_bar_balance.visibility = GONE
                rl_wallet_balance.visibility = GONE
                cvViewBalance.visibility = VISIBLE*/
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(AppConstants.AEPS_BALANCE_API)) {
            //progress_bar_aeps.visibility = GONE
            Log.e(AppConstants.AEPS_BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

/*
                rl_aeps_balance.visibility = VISIBLE
                cvViewAepsBalance.visibility = GONE*/
                tvAepsBalance.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString("AEPSBalance")}"
                /* tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/


            } else {
/*                progress_bar_aeps.visibility = GONE
                rl_aeps_balance.visibility = GONE
                cvViewAepsBalance.visibility = VISIBLE*/
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(USER_LOGOUT)) {
            Log.e("USER_LOGOUT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE
                AppPrefs.putStringPref("userModel", "", this)
                AppPrefs.putStringPref("cus_id", "", this)
                AppPrefs.putStringPref("user_id", "", this)
                AppPrefs.putBooleanPref(AppConstants.IS_LOGIN, false, this)

                val intentLogin = Intent(this, LoginActivity::class.java)
                startActivity(intentLogin)
                finish()


            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }

        if (flag.equals(OFFER_POPUP)) {
            Log.e("OFFER_POPUP", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val cast = jsonObject.getJSONArray("result")
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE

                val response = jsonObject.getString("message")
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)

                    offer_id = notifyObjJson.getString("offer_id")
                    product_name = notifyObjJson.getString("product_name")
                    product_description = notifyObjJson.getString("product_description")
                    product_amount = notifyObjJson.getString("product_amount")
                    product_image = notifyObjJson.getString("product_image")
                    product_offer_amount = notifyObjJson.getString("product_offer_amount")
                    stock = notifyObjJson.getString("stock")
                    available_quantity = notifyObjJson.getString("available_quantity")
                    offer_added_date = notifyObjJson.getString("offer_added_date")

                    showPopupOffer(
                        product_name,
                        product_description,
                        product_amount,
                        product_image,
                        product_offer_amount,
                        available_quantity
                    )

                }

                //toast(response)

            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }

        if (flag.equals(SERVICE_AMOUNT)) {
            Log.e("SERVICE_AMOUNT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE

                val cast = jsonObject.getJSONArray("result")

                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    amount = notifyObjJson.getString("amount")
                    Log.e("amount", amount)

                    showDialogOffer(serviceName!!, amount)
                }

            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }

        if (flag.equals(BUY_SERVICE)) {
            Log.e("BUY_SERVICE", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE

                val response = jsonObject.getString("message")
                toast(response)

            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }

        if (flag.equals(SERVICE_STATUS)) {
            Log.e("SERVICE_STATUS", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val message = jsonObject.getString(AppConstants.MESSAGE)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE

                val cast = jsonObject.getJSONArray("result")

                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    //Log.e("TAG",notifyObjJson.getString("name"))
                    if (service.equals("adharpay_service")) {
                        val adharpay_service = notifyObjJson.getString("adharpay_service")
                       /* if (adharpay_service.equals("inactivate")) {
                            serviceName = "Adharpay Service"
                            getServiceAmount(serviceName!!)
                            //Log.e("AMOUNT",amount.toString())
                            //showDialogOffer(serviceName!!, amount.toString())
                        } else {*/
                            //checkKycstatus(userModel.cus_id)
                            if(newaepskyc_status.equals("done")) {
                                val bundle = Bundle()
                                bundle.putString("transactionType","aadharpay")
                                bundle.putString("transaction", "Aadhar Pay")
                                val intent = Intent(this, AepsTransactionActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }else if(aeps_kyc_status.equals("KYC Not Completed") && newaepskyc_status.equals("not-done")){
                                val intent = Intent(this,KycVerificationActivity::class.java)
                                startActivity(intent)
                            }  else if(aeps_kyc_status.equals("KYC Completed") && newaepskyc_status.equals("not-done")){
                                val intent = Intent(this, KycVerificationActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, AepsOnboardActivity::class.java)
                                startActivity(intent)
                            }
                       // }
                    } else if (service.equals("aeps_service")) {
                        val aeps_service = notifyObjJson.getString("aeps_service")
                        if (aeps_service.equals("inactivate")) {
                            serviceName = "Aeps Service"
                            getServiceAmount(serviceName!!)
                            //Log.e("AMOUNT",amount.toString())
                            //showDialogOffer(serviceName!!, amount.toString())
                        } else {
                            //checkKycstatus(userModel.cus_id)
                            if(newaepskyc_status.equals("done")) {
                                val intent = Intent(this, AepsTransactionActivity::class.java)
                                startActivity(intent)
                            } else if(aeps_kyc_status.equals("KYC Not Completed") && newaepskyc_status.equals("not-done")){
                                val intent = Intent(this,KycVerificationActivity::class.java)
                                startActivity(intent)
                            } else if(aeps_kyc_status.equals("KYC Completed") && newaepskyc_status.equals("not-done")){
                                val intent = Intent(this,KycVerificationActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, AepsOnboardActivity::class.java)
                                startActivity(intent)
                            }

                        }
                    } else if (service.equals("dmt_service")) {
                        val dmt_service = notifyObjJson.getString("dmt_service")
                        if (dmt_service.equals("inactivate")) {
                            serviceName = "Dmt Service"
                            getServiceAmount(serviceName!!)
                            //Log.e("AMOUNT",amount.toString())
                            //showDialogOffer(serviceName!!, amount.toString())
                        } else {
                            val intent = Intent(this, SelectDmtActivity::class.java)
                            startActivity(intent)
                        }
                    } else if(service.equals("recharge_service")) {
                        val recharge_service = notifyObjJson.getString("recharge_service")
                        if(recharge_service.equals("inactivate")) {
                            serviceName = "Recharge Service"
                            getServiceAmount(serviceName!!)
                        } else {
                            val intent = Intent(this, MobileRechargeActivity::class.java)
                            startActivity(intent)
                        }
                    } else if(service.equals("bill_service")) {
                        val billpay_service = notifyObjJson.getString("bill_service")
                        if(billpay_service.equals("inactivate")) {
                            serviceName = "Bill Service"
                            getServiceAmount(serviceName!!)
                        } else {
                            val intent = Intent(this,ElectricityRechargeActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (service.equals("microatm_service")) {
                        val microatm_service = notifyObjJson.getString("microatm_service")
                        if (microatm_service.equals("inactivate")) {
                            serviceName = "Micro ATM Service"
                            getServiceAmount(serviceName!!)
                        } else {
                            microAtmLogin(userModel.cus_id)
                        }
                    } else if (service.equals("cashdeposite_service")) {
                        val cashdeposite_service = notifyObjJson.getString("cashdeposite_service")
                        if (cashdeposite_service.equals("inactivate")) {
                            serviceName = "Cashdeposite Service"
                            getServiceAmount(serviceName!!)
                        } else {
                            val bundle = Bundle()
                            bundle.putString("transactionType", "cashdeposit")
                            bundle.putString("transaction", "Cash Deposit")
                            //checkKycstatus(userModel.cus_id)
                            if(newaepskyc_status.equals("done")) {
                                val intent = Intent(this, AepsTransactionActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            } else if(aeps_kyc_status.equals("KYC Completed") && newaepskyc_status.equals("not-done")){
                                val intent = Intent(this, KycVerificationActivity::class.java)
                                startActivity(intent)
                            }else {
                                val intent = Intent(this, AepsOnboardActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }

        if (flag.equals(CHECK_KYC_STATUS)) {
            Log.e("CHECK_KYC_STATUS", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val message = jsonObject.getString("message")
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE
                val cast = jsonObject.getJSONObject("result")
              //  newaepskyc_status = cast.getString("newaepskyc_status")
                Log.e("newaepsstatus",newaepskyc_status)
            }
            else {
                progress_bar.visibility = View.INVISIBLE
             //   newaepskyc_status = "not-done"
                val intent = Intent(this, AepsOnboardActivity::class.java)
                startActivity(intent)
                toast(message)
            }

        }



        if (flag.equals(MICRO_ATMLOGIN)) {
            Log.e("MICRO_ATMLOGIN", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE

                val loginResult = jsonObject.getJSONObject("result")


                val bundle = Bundle()
                bundle.putString("latitude",latitudeLabel)
                bundle.putString("longitude",longitudeLabel)
                bundle.putString("cus_id", userModel.cus_id)
                bundle.putString(
                    "aeps_merchantLoginId",
                    loginResult.getString("aeps_merchantLoginId")
                )
                bundle.putString(
                    "aeps_merchantLoginPin",
                    loginResult.getString("aeps_merchantLoginPin")
                )

                val intent = Intent(this, MicroAtmActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)


            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

        getBalanceApi(userModel.cus_mobile)
        getAepsBalanceApi(userModel.cus_id)
    }

    private fun showLogout() {
        val builder1 =
            AlertDialog.Builder(this)
        builder1.setTitle("Attention!")
        builder1.setMessage("Do you want to Log Out?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val gson = Gson()
            val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
            userModel = gson.fromJson(json, UserModel::class.java)

            userLogout(
                userModel.cus_id, deviceId, deviceNameDet.toString(),
                userModel.cus_pin,
                userModel.cus_pass,
                userModel.cus_mobile, userModel.cus_type
            )

            dialog.cancel()
        }
        builder1.setNegativeButton(
            "CANCEL"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    private fun microAtmLogin(cus_id: String) {
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(this, MICRO_ATMLOGIN, this)
            mAPIcall.microAtmLogin(cus_id)
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkKycstatus(cus_id: String) {
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(this, CHECK_KYC_STATUS, this)
            mAPIcall.checkKycStatus(cus_id)
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

}





