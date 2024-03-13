package fr.norsys.reservation.services;

import fr.norsys.reservation.dao.MeetingRoomRepository;
import fr.norsys.reservation.entities.MeetingRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomRepository.findAll();
    }

    public Optional<MeetingRoom> getMeetingRoomById(Long id) {
        return meetingRoomRepository.findById(id);
    }

    public MeetingRoom createMeetingRoom(MeetingRoom meetingRoom) {
        return meetingRoomRepository.save(meetingRoom);
    }

    public MeetingRoom updateMeetingRoom(Long id, MeetingRoom updatedMeetingRoom) {
        Optional<MeetingRoom> existingMeetingRoomOptional = meetingRoomRepository.findById(id);
        if (existingMeetingRoomOptional.isPresent()) {
            MeetingRoom existingMeetingRoom = existingMeetingRoomOptional.get();
            existingMeetingRoom.setName(updatedMeetingRoom.getName());
            // Update other fields if needed
            return meetingRoomRepository.save(existingMeetingRoom);
        } else {
            return null; // Or throw an exception if you prefer
        }
    }

    public void deleteMeetingRoom(Long id) {
        meetingRoomRepository.deleteById(id);
    }
}