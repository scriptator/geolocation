package at.ac.tuwien.mns.mnsgeolocation.service;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceFactory {

    private static MLSLocationService mlsLocationService;

    @NotNull
    public static MLSLocationService getMlsLocationService() {
        if (mlsLocationService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://location.services.mozilla.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            mlsLocationService = retrofit.create(MLSLocationService.class);
        }
        return mlsLocationService;
    }
}
