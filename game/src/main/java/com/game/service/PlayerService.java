package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;



public interface PlayerService {

    public List<Player> getAllPlayers(
            String name,
            String title,
            Race race,
            Profession profession,
            Long after,
            Long before,
            Boolean banned,
            Integer minExperience,
            Integer maxExperience,
            Integer minLevel,
            Integer maxLevel
    );

    List<Player> getPage(List<Player> ships, Integer pageNumber, Integer pageSize);

    public void addPlayer(Player player);

    Player updatePlayer(Player oldPlayer, Player newPlayer);

    public Player getPlayer(Long id);

    boolean  existsById(Long id);

    public String deletePlayer(Long id);



}
