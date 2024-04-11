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

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
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
          throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the user could not be created!");
      }
  }

    //service call to check if password matches the username given returns the logged-in user if successful
  public User loginUser(User checkUser) throws ResponseStatusException{
    User userByUsername = userRepository.findByUsername(checkUser.getUsername());

    String baseErrorMessage = "Wrong Username or Password";
    if (userByUsername != null && userByUsername.getPassword().equals(checkUser.getPassword())) {
        //login user and set him to be online
        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername; // Password matches, return the user
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password");
    }
  }
}
