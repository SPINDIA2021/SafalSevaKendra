package com.satmatgroup.safalsevakendra.data.remote;




import com.myapp.onlysratchapp.category.CategoryResponse;
import com.satmatgroup.safalsevakendra.data.DataSource;
import com.satmatgroup.safalsevakendra.network.BaseResponse;
import com.satmatgroup.safalsevakendra.network.IApi;
import com.satmatgroup.safalsevakendra.network.NetworkCall;
import com.satmatgroup.safalsevakendra.network.ServiceCallBack;


import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class RemoteDataSource implements DataSource {
    private static RemoteDataSource INSTANCE;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }




    @Override
    public void getCategory(ServiceCallBack myAppointmentPresenter, NetworkCall networkCall) {
        try{

            RequestBody for1 = RequestBody.create(MediaType.parse("text/plain"), "appcategory");
            Call<BaseResponse<ArrayList<CategoryResponse>>> responceCall = networkCall.getRetrofit(true, true).getCategory(for1);
            networkCall.setServiceCallBack(myAppointmentPresenter);
            networkCall.setRequestTag(IApi.COMMON_TAG);
            responceCall.enqueue(networkCall.requestCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRetry(String txnid, ServiceCallBack myAppointmentPresenter, NetworkCall networkCall) {
        try{

            RequestBody txnid1 = RequestBody.create(MediaType.parse("text/plain"), txnid);


            Call<BaseResponse> responceCall = networkCall.getRetrofit(true, true).saveRetry(txnid1);
            networkCall.setServiceCallBack(myAppointmentPresenter);
            networkCall.setRequestTag(IApi.COMMON_TAG1);
            responceCall.enqueue(networkCall.requestCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


