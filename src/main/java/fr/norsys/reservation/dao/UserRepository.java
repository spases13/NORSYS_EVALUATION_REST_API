package fr.norsys.reservation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.norsys.reservation.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
