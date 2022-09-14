package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;


@RestController
@RequestMapping("/rest")
public class PlayerController {
    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }


    @GetMapping("/players")
    public @ResponseBody ResponseEntity<List<Player>> showAllPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false) PlayerOrder plaerOrder,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        final List<Player> players = service.getAllPlayers(name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel, maxLevel);


        final List<Player> pagePlayers = service.getPage(players, pageNumber, pageSize);

        return new ResponseEntity<>(pagePlayers, HttpStatus.OK);
    }

    @GetMapping("/players/count")
    public @ResponseBody ResponseEntity<Integer> getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        return new ResponseEntity<>(service.getAllPlayers(name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel, maxLevel).size(), HttpStatus.OK);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id
    ) {
        try {
            if (id <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!service.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(service.getPlayer(id), HttpStatus.OK);
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/players")
    public ResponseEntity<Player> addNewPlayer(@RequestBody Player player) {
        if (player.getBirthday() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(player.getBirthday());
        if (player.getName().length() > 12 || player.getTitle().length() > 30
                || player.getName() == null || player.getTitle() == null
                || player.getRace() == null || player.getProfession() == null
                || player.getExperience() == null
                || player.getName().equals("") || player.getExperience() > 10000000
                || player.getExperience() < 0 || calendar.getWeekYear() < 2000
                || calendar.getWeekYear() > 3000 || calendar.getTimeInMillis() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        service.addPlayer(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

//    @PostMapping("/players/{id}")
//    public Player updatePlayer(@PathVariable Long id) {
//        Player player = service.updatePlayer(id);
//        return player;
//    }

    @PostMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player player
    ) {
        final ResponseEntity<Player> entity = getPlayer(id);
        final Player savedPlayer = entity.getBody();
        if (savedPlayer == null) {
            return entity;
        }

        final Player result;
        try {
            result = service.updatePlayer(savedPlayer, player);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        try {
            String result = service.deletePlayer(id);
            if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (result == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if ("404".equals(result)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NullPointerException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
