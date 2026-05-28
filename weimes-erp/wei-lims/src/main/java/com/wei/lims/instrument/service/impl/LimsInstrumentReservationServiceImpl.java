package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentReservation;
import com.wei.lims.instrument.mapper.LimsInstrumentReservationMapper;
import com.wei.lims.instrument.service.ILimsInstrumentReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsInstrumentReservationServiceImpl implements ILimsInstrumentReservationService {

    @Autowired
    private LimsInstrumentReservationMapper limsInstrumentReservationMapper;

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
    public int insertLimsInstrumentReservation(LimsInstrumentReservation reservation) {
        return limsInstrumentReservationMapper.insertLimsInstrumentReservation(reservation);
    }

    @Override
    public int updateLimsInstrumentReservation(LimsInstrumentReservation reservation) {
        return limsInstrumentReservationMapper.updateLimsInstrumentReservation(reservation);
    }

    @Override
    public int deleteLimsInstrumentReservationByIds(Long[] reservationIds) {
        return limsInstrumentReservationMapper.deleteLimsInstrumentReservationByIds(reservationIds);
    }
}
