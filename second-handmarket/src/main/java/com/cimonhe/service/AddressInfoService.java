package com.cimonhe.service;

import com.cimonhe.pojo.AddressInfo;

import java.util.List;

public interface AddressInfoService {

    List<AddressInfo> queryAllAddressInfoByUserId(int userId);
    AddressInfo queryDefaultAddressInfo(int userId);
    AddressInfo queryAddressInfById(int id);
    int cancelDefault();
    int addAddressInfo(AddressInfo addressInfo);
    int setDefault(int id);
    int deleteAddressInfo(int id);

}
