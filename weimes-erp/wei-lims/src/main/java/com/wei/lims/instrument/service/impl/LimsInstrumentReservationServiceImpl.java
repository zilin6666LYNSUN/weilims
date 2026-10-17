package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentReservation;
import com.wei.lims.instrument.mapper.LimsInstrumentReservationMapper;
import com.wei.lims.instrument.service.ILimsInstrumentReservationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsInstrumentReservationServiceImpl implements ILimsInstrumentReservationService {

    @Autowired
    private LimsInstrumentReservationMapper limsInstrumentReservationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsInstrumentReservation selectByReservationId(Long reservationId) {
        return limsInstrumentReservationMapper.selectByReservationId(reservationId);
    }

    @Override
    public List<LimsInstrumentReservation> selectByInstrumentId(Long instrumentId) {
        return limsInstrumentReservationMapper.selectByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrumentReservation> selectLimsInstrumentReservationList(LimsInstrumentReservation reservation) {
        return limsInstrumentReservationMapper.selectLimsInstrumentReservationList(reservation);
    }

    @Override
    @Transactional
    public int insertLimsInstrumentReservation(LimsInstrumentReservation reservation) {
        int rows = limsInstrumentReservationMapper.insertLimsInstrumentReservation(reservation);
        limsAuditTrailService.recordFieldChanges("lims_instrument_reservation", reservation.getReservationId(),
                "INSERT", null, reservation, "新建仪器预约");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsInstrumentReservation(LimsInstrumentReservation reservation) {
        LimsInstrumentReservation oldReservation = limsInstrumentReservationMapper.selectByReservationId(reservation.getReservationId());
        int rows = limsInstrumentReservationMapper.updateLimsInstrumentReservation(reservation);
        limsAuditTrailService.recordFieldChanges("lims_instrument_reservation", reservation.getReservationId(),
                "UPDATE", oldReservation, reservation, "更新仪器预约");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsInstrumentReservationByIds(Long[] reservationIds) {
        for (Long reservationId : reservationIds) {
            limsAuditTrailService.recordFieldChanges("lims_instrument_reservation", reservationId,
                    "DELETE", null, null, "删除仪器预约");
        }
        return limsInstrumentReservationMapper.deleteLimsInstrumentReservationByIds(reservationIds);
    }
}
