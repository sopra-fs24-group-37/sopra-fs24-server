package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import java.util.List;


public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    Optional<GamePlayer> findByGame_GameIdAndUser_UserId(UUID gameId, Long id);
    List<GamePlayer> findByPlayerId(Long playerId);
}

