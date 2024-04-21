package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    Optional<GamePlayer> findByGame_GameIdAndUser_UserId(UUID gameId, Long id);
}

