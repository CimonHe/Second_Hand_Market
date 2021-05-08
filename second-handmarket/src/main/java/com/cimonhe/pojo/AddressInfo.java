package com.cimonhe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
@Validated
public class AddressInfo {

    int id;

    int userId;

    String consigneeName;

    String address;

    int houseNo;

    String phone;

    boolean isDefaultAddress;

    public AddressInfo(int userId, String consigneeName, String address, int houseNo, String phone, boolean isDefaultAddress) {
        this.userId = userId;
        this.consigneeName = consigneeName;
        this.address = address;
        this.houseNo = houseNo;
        this.phone = phone;
        this.isDefaultAddress = isDefaultAddress;
    }
}
