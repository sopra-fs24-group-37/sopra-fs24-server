package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.GamePlayer;
import ch.uzh.ifi.hase.soprafs24.repository.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GamePlayerService {
    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    public GamePlayer getGameplayer(UUID gameId, Long userId) {
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_GameIdAndUser_UserId(gameId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found in the game"));

        return gamePlayer;
    }

    public List<GamePlayer> getAllGameplayers() {
        return gamePlayerRepository.findAll();
    }
}
