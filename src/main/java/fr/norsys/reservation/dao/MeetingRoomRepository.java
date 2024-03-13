package fr.norsys.reservation.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import fr.norsys.reservation.entities.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
}
