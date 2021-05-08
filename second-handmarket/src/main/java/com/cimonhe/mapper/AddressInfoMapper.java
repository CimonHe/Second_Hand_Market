package com.cimonhe.mapper;

import com.cimonhe.pojo.AddressInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AddressInfoMapper {
    List<AddressInfo> queryAllAddressInfoByUserId(int userId);
    AddressInfo queryDefaultAddressInfo(int userId);
    AddressInfo queryAddressInfById(int id);
    int addAddressInfo(AddressInfo addressInfo);
    int cancelDefault();
    int deleteAddressInfo(int id);
    int setDefault(int id);


}
