package com.game.service;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
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
    ) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        final List<Player> list = new ArrayList<>();
        playerRepository.findAll().forEach((player) -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (afterDate != null && player.getBirthday().before(afterDate)) return;
            if (beforeDate != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned().booleanValue() != banned.booleanValue()) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;

            list.add(player);
        });
        return list;
    }

    @Override
    public List<Player> getPage(List<Player> ships, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }



    @Override
    public void addPlayer(Player player) {
        player.setLevel((int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
        playerRepository.saveAndFlush(player);
    }

    @Override
    public Player updatePlayer(Player oldPlayer, Player newPlayer) throws IllegalArgumentException {

        final String name = newPlayer.getName();
        if (name != null) {
            if (isStringNameValid(name)) {
                oldPlayer.setName(name);
            } else {
                throw new IllegalArgumentException();
            }
        }
        final String title = newPlayer.getTitle();
        if (title != null) {
            if (isStringTitleValid(title)) {
                oldPlayer.setTitle(title);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (newPlayer.getRace() != null) {
            oldPlayer.setRace(newPlayer.getRace());
        }
        if (newPlayer.getProfession() != null) {
            oldPlayer.setProfession(newPlayer.getProfession());
        }
        final Date birthday = newPlayer.getBirthday();
        if (birthday != null) {
            if (isProdDateValid(birthday)) {
                oldPlayer.setBirthday(birthday);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (newPlayer.getBanned() != null) {
            oldPlayer.setBanned(newPlayer.getBanned());
        }
        final Integer experience = newPlayer.getExperience();
        if (experience != null) {
            if (isExperienceValid(experience)) {
                oldPlayer.setExperience(experience);
                oldPlayer.setLevel((int) (Math.sqrt(2500 + 200 * oldPlayer.getExperience()) - 50) / 100);
                oldPlayer.setUntilNextLevel(50 * (oldPlayer.getLevel() + 1) * (oldPlayer.getLevel() + 2) - oldPlayer.getExperience());
            } else {
                throw new IllegalArgumentException();
            }
        }

        playerRepository.save(oldPlayer);
        return oldPlayer;
    }

    private boolean isExperienceValid(Integer experience) {
        final double minExperience = 0;
        final double maxExperience = 10000000;
        return experience != null && experience >= minExperience && experience <= maxExperience;
    }

    private boolean isStringNameValid(String value) {
        final int maxStringLength = 12;
        return value != null && !value.isEmpty() && value.length() <= maxStringLength;
    }

    private boolean isStringTitleValid(String value) {
        final int maxStringLength = 30;
        return value != null && !value.isEmpty() && value.length() <= maxStringLength;
    }

    private boolean isProdDateValid(Date prodDate) {
        final Date startProd = getDateForYear(2000);
        final Date endProd = getDateForYear(3000);
        return prodDate != null && prodDate.after(startProd) && prodDate.before(endProd);
    }

    private Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }


    @Override
    public Player getPlayer(Long id) {
        Player player = playerRepository.findById(id).get();
        return player;
    }
    @Override
    public boolean  existsById(Long id) {
        return playerRepository.existsById(id);
    }

    @Override
    public String deletePlayer(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return "200";
        } else {
            return "404";
        }
    }

}
