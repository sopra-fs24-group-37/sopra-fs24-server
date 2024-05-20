package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.RoundStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoundStatsRepository extends JpaRepository<RoundStats, UUID> {
    Optional<RoundStats> findByGame_GameIdAndGamePlayer_PlayerId(UUID gameId, Long id);
}
