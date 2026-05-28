package com.wei.lims.instrument.service;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentReservation;

public interface ILimsInstrumentReservationService {
    public LimsInstrumentReservation selectByReservationId(Long reservationId);
    public List<LimsInstrumentReservation> selectByInstrumentId(Long instrumentId);
    public List<LimsInstrumentReservation> selectLimsInstrumentReservationList(LimsInstrumentReservation reservation);
    public int insertLimsInstrumentReservation(LimsInstrumentReservation reservation);
    public int updateLimsInstrumentReservation(LimsInstrumentReservation reservation);
    public int deleteLimsInstrumentReservationByIds(Long[] reservationIds);
}
