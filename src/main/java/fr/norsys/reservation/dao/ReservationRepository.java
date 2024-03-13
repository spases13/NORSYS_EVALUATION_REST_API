package fr.norsys.reservation.dao;

import fr.norsys.reservation.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findByMeetingRoomIdAndStartTimeBetweenOrEndTimeBetween(Long roomId,
      LocalDateTime startTime1, LocalDateTime endTime1,
      LocalDateTime startTime2, LocalDateTime endTime2);
}
