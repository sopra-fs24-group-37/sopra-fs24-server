package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Round;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoundDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RoundController {
    private final RoundService roundService;

    RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/round/{gameId}/leaderboard")
    public ResponseEntity<RoundDTO> getLeaderboard(@PathVariable UUID gameId) {
        //Get all the relevant objects
        Round round = roundService.getRound(gameId);

        RoundDTO roundDTO = DTOMapper.INSTANCE.convertEntityToRoundDTO(round);

        return ResponseEntity.ok(roundDTO);
    }

}
