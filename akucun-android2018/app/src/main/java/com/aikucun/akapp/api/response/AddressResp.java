package com.aikucun.akapp.api.response;

import java.util.List;

/**
 * Created by micker on 2017/7/16.
 */

public class AddressResp {
    private List<AddressResp> addresses;

    public List<AddressResp> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressResp> addresses) {
        this.addresses = addresses;
    }
}
