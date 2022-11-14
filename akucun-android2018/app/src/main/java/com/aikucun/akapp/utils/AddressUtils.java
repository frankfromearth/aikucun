package com.aikucun.akapp.utils;

import com.aikucun.akapp.api.entity.Address;

import java.util.List;

/**
 * Created by ak123 on 2018/1/4.
 * 地址管理
 */

public final class AddressUtils {
    //所有地址列表
    private static List<Address> addresses;
    //已选中地址
    private static Address selectedAddress;
    //默认地址
    private static Address defaultAddress;

    public static Address getDefaultAddress() {
        return defaultAddress;
    }

    public static void setDefaultAddress(Address defaultAddress) {
        AddressUtils.defaultAddress = defaultAddress;
    }

    public static List<Address> getAddresses() {
        return addresses;
    }

    public static void setAddresses(List<Address> _addresses) {
        addresses = _addresses;
        if (_addresses != null && _addresses.size() > 0) {
            for (int i = 0, size = _addresses.size(); i < size; i++) {
                if (_addresses.get(i).getDefaultflag() == 1){
                    setDefaultAddress(_addresses.get(i));
                    break;
                }
            }
        }

    }

    public static Address getSelectedAddress() {
        return selectedAddress;
    }

    public static void setSelectedAddress(Address _selectedAddress) {
        selectedAddress = _selectedAddress;
    }


    public static final String orderAddress = "order_address";
    public static final String orderReceiptName = "order_receipt_name";
    public static final String orderAddressPhone = "order_address_phone";
}
