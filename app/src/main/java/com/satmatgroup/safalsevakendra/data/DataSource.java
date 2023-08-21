package com.satmatgroup.safalsevakendra.data;




import com.satmatgroup.safalsevakendra.network.NetworkCall;
import com.satmatgroup.safalsevakendra.network.ServiceCallBack;

import okhttp3.RequestBody;
import retrofit2.http.Part;


public interface DataSource {

    void getCategory(ServiceCallBack myAppointmentPresenter, NetworkCall networkCall);

    void saveRetry(String txnid, ServiceCallBack myAppointmentPresenter, NetworkCall networkCall);

}

