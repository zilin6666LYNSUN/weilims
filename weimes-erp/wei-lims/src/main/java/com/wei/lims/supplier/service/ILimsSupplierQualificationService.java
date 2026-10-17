package com.wei.lims.supplier.service;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierQualification;

public interface ILimsSupplierQualificationService {
    public LimsSupplierQualification selectLimsSupplierQualificationByQualificationId(Long qualificationId);
    public List<LimsSupplierQualification> selectLimsSupplierQualificationList(LimsSupplierQualification LimsSupplierQualification);
    public int insertLimsSupplierQualification(LimsSupplierQualification limssupplierqualification);
    public int updateLimsSupplierQualification(LimsSupplierQualification limssupplierqualification);
    public int deleteLimsSupplierQualificationByQualificationId(Long[] qualificationIds);
}