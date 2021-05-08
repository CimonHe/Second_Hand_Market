package com.cimonhe.service;

import com.cimonhe.mapper.AddressInfoMapper;
import com.cimonhe.pojo.AddressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressInfoServiceImpl implements AddressInfoService{

    @Autowired
    AddressInfoMapper addressInfoMapper;

    @Override
    public int addAddressInfo(AddressInfo addressInfo) {
        return addressInfoMapper.addAddressInfo(addressInfo);
    }

    @Override
    public int setDefault(int id) {
        return addressInfoMapper.setDefault(id);
    }

    @Override
    public int deleteAddressInfo(int id) {
        return addressInfoMapper.deleteAddressInfo(id);
    }

    @Override
    public List<AddressInfo> queryAllAddressInfoByUserId(int userId) {
        return addressInfoMapper.queryAllAddressInfoByUserId(userId);
    }

    @Override
    public AddressInfo queryDefaultAddressInfo(int userId) {
        return addressInfoMapper.queryDefaultAddressInfo(userId);
    }

    @Override
    public AddressInfo queryAddressInfById(int id) {
        return addressInfoMapper.queryAddressInfById(id);
    }

    @Override
    public int cancelDefault() {
        return addressInfoMapper.cancelDefault();
    }



}
