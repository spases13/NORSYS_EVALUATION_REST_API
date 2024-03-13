package fr.norsys.reservation.controller;

import fr.norsys.reservation.entities.MeetingRoom;
import fr.norsys.reservation.entities.Reservation;
import fr.norsys.reservation.entities.User;
import fr.norsys.reservation.exceptions.ResourceNotFoundException;
import fr.norsys.reservation.services.MeetingRoomService;
import fr.norsys.reservation.services.ReservationService;
import fr.norsys.reservation.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @GetMapping("/meeting-rooms")
    public ResponseEntity<List<MeetingRoom>> getAllMeetingRooms() {
        List<MeetingRoom> meetingRooms = meetingRoomService.getAllMeetingRooms();
        return new ResponseEntity<>(meetingRooms, HttpStatus.OK);
    }

    @GetMapping("/meeting-rooms/{id}")
    public ResponseEntity<MeetingRoom> getMeetingRoomById(@PathVariable("id") long id) {
        Optional<MeetingRoom> meetingRoomOptional = meetingRoomService.getMeetingRoomById(id);
        return meetingRoomOptional
                .map(meetingRoom -> new ResponseEntity<>(meetingRoom, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Meeting Room not found with ID: " + id));
    }

    @PostMapping("/meeting-rooms")
    public ResponseEntity<MeetingRoom> createMeetingRoom(@Valid @RequestBody MeetingRoom meetingRoom) {
        MeetingRoom createdMeetingRoom = meetingRoomService.createMeetingRoom(meetingRoom);
        return new ResponseEntity<>(createdMeetingRoom, HttpStatus.CREATED);
    }

    @PutMapping("/meeting-rooms/{id}")
    public ResponseEntity<MeetingRoom> updateMeetingRoom(@PathVariable("id") long id,
            @Valid @RequestBody MeetingRoom meetingRoom) {
        MeetingRoom updatedMeetingRoom = meetingRoomService.updateMeetingRoom(id, meetingRoom);
        return updatedMeetingRoom != null ? new ResponseEntity<>(updatedMeetingRoom, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/meeting-rooms/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(@PathVariable("id") long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") long id) {
        Optional<Reservation> reservationOptional = reservationService.getReservationById(id);
        return reservationOptional
                .map(reservation -> new ResponseEntity<>(reservation, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        long roomId = reservation.getMeetingRoom().getId();
        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
    
        if (!isRoomAvailable(roomId, startTime, endTime)) {
            return ResponseEntity.badRequest().body("The room is not available for the given period.");
        }
        if (reservationService.isMeetingRoomReserved(roomId, startTime, endTime)) {
            return ResponseEntity.badRequest().body("The room is already reserved for the given period.");
        }
        Reservation createdReservation = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable("id") long id,
            @Valid @RequestBody Reservation reservation) {
        if (!isRoomAvailable(reservation.getId(), reservation.getStartTime(), reservation.getEndTime())) {
            return ResponseEntity.badRequest().body("The room is not available for the given period.");
        }
        Reservation updatedReservation = reservationService.updateReservation(id, reservation);
        return updatedReservation != null ? ResponseEntity.ok().body(updatedReservation)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        Optional<User> userOptional = userService.getUserById(id);
        return userOptional
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean isRoomAvailable(long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Reservation> reservationOptional = reservationService.getReservationById(roomId);
        return reservationOptional.map(reservation -> {
            LocalDateTime existingStartTime = reservation.getStartTime();
            LocalDateTime existingEndTime = reservation.getEndTime();

            return !(startTime.isAfter(existingEndTime) || endTime.isBefore(existingStartTime));
        }).orElse(true);
    }

}
