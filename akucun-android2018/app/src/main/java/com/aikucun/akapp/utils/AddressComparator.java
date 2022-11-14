package com.aikucun.akapp.utils;

import com.aikucun.akapp.api.entity.Address;

import java.util.Comparator;

/**
 * Created by micker on 2017/7/16.
 */

public class AddressComparator implements Comparator<Address> {

    @Override
    public int compare(Address o1, Address o2) {
        return o2.getDefaultflag()-o1.getDefaultflag();
    }
}
