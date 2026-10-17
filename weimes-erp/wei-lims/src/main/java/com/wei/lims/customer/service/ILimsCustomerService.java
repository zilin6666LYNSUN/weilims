package com.wei.lims.customer.service;

import java.util.List;
import com.wei.lims.customer.domain.LimsCustomer;

public interface ILimsCustomerService {
    public LimsCustomer selectLimsCustomerByCustomerId(Long customerId);
    public List<LimsCustomer> selectLimsCustomerList(LimsCustomer LimsCustomer);
    public int insertLimsCustomer(LimsCustomer limscustomer);
    public int updateLimsCustomer(LimsCustomer limscustomer);
    public int deleteLimsCustomerByCustomerId(Long[] customerIds);
}