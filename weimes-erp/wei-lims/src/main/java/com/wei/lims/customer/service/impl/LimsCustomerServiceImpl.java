package com.wei.lims.customer.service.impl;

import java.util.List;
import com.wei.lims.customer.domain.LimsCustomer;
import com.wei.lims.customer.mapper.LimsCustomerMapper;
import com.wei.lims.customer.service.ILimsCustomerService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsCustomerServiceImpl implements ILimsCustomerService {

    @Autowired
    private LimsCustomerMapper limscustomerMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsCustomer selectLimsCustomerByCustomerId(Long customerId) {
        return limscustomerMapper.selectLimsCustomerByCustomerId(customerId);
    }

    @Override
    public List<LimsCustomer> selectLimsCustomerList(LimsCustomer limscustomer) {
        return limscustomerMapper.selectLimsCustomerList(limscustomer);
    }

    @Override
    @Transactional
    public int insertLimsCustomer(LimsCustomer limscustomer) {
        int rows = limscustomerMapper.insertLimsCustomer(limscustomer);
        limsAuditTrailService.recordFieldChanges("lims_customer", limscustomer.getCustomerId(), "INSERT", null, limscustomer, "新建客户档案");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsCustomer(LimsCustomer limscustomer) {
        LimsCustomer old = limscustomerMapper.selectLimsCustomerByCustomerId(limscustomer.getCustomerId());
        int rows = limscustomerMapper.updateLimsCustomer(limscustomer);
        limsAuditTrailService.recordFieldChanges("lims_customer", limscustomer.getCustomerId(), "UPDATE", old, limscustomer, "更新客户档案");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsCustomerByCustomerId(Long[] customerIds) {
        for (Long customerId : customerIds) {
            LimsCustomer old = limscustomerMapper.selectLimsCustomerByCustomerId(customerId);
            limsAuditTrailService.recordFieldChanges("lims_customer", customerId, "DELETE", old, null, "删除客户档案");
        }
        return limscustomerMapper.deleteLimsCustomerByCustomerId(customerIds);
    }
}