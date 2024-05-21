package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User findUserbyId(Long userId) {
    User userById = this.userRepository.findById(userId).orElse(null);
    if (userById == null) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "User with user id " + userId + " was not found!");
    }
    return userById;
  }

  public void deleteUserById(Long userId) {
    findUserbyId(userId); // To throw exception if user does not exist
    this.userRepository.deleteById(userId);
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setGamesPlayed(0);
    newUser.setGamesWon(0);
    newUser.setTotalScores(0);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  private void checkIfUserExists(User userToBeCreated) {
      User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
      if (userByUsername != null) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken!");
      }
  }

    //service call to check if password matches the username given returns the logged-in user if successful
  public User loginUser(User checkUser) throws ResponseStatusException{
    User userByUsername = userRepository.findByUsername(checkUser.getUsername());

    if (userByUsername != null && userByUsername.getPassword().equals(checkUser.getPassword())) {
        //login user and set him to be online
        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername; // Password matches, return the user
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Username or Password!");
    }
  }

  public void setUserOffline(Long userId) {
    User user = findUserbyId(userId);
    if (user != null) {
        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
    }
  }

  public User updateUser(User user) {
    Long userIdOpt = user.getUserId();
    findUserbyId(userIdOpt);
    userRepository.save(user);
    return user;
  }

}
