package com.testerhome.nativeandroid.models;

import java.util.List;

/**
 * Created by vclub on 15/10/30.
 */
public class ToutiaoResponse {

    private List<AdsEntity> ads;

    public void setAds(List<AdsEntity> ads) {
        this.ads = ads;
    }

    public List<AdsEntity> getAds() {
        return ads;
    }


}
