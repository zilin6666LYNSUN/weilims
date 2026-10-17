package com.wei.lims.customer.mapper;

import java.util.List;
import com.wei.lims.customer.domain.LimsCustomer;

public interface LimsCustomerMapper {
    public LimsCustomer selectLimsCustomerByCustomerId(Long customerId);
    public List<LimsCustomer> selectLimsCustomerList(LimsCustomer LimsCustomer);
    public int insertLimsCustomer(LimsCustomer limscustomer);
    public int updateLimsCustomer(LimsCustomer limscustomer);
    public int deleteLimsCustomerByCustomerId(Long[] customerIds);
}