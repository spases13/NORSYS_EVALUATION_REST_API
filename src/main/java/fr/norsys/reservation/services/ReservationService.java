package fr.norsys.reservation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.norsys.reservation.dao.ReservationRepository;
import fr.norsys.reservation.entities.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        Optional<Reservation> existingReservationOptional = reservationRepository.findById(id);
        if (existingReservationOptional.isPresent()) {
            Reservation existingReservation = existingReservationOptional.get();
            existingReservation.setStartTime(updatedReservation.getStartTime());
            existingReservation.setEndTime(updatedReservation.getEndTime());
            return reservationRepository.save(existingReservation);
        } else {
            return null;
        }
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isMeetingRoomReserved(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> reservations = reservationRepository.findByMeetingRoomIdAndStartTimeBetweenOrEndTimeBetween(roomId, startTime, endTime, startTime, endTime);
        return !reservations.isEmpty();
    }
}