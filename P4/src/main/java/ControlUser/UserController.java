package ControlUser;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {

        private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //@GetMapping
        public Class<? extends UserService> getAll() {
            return userService.getClass();
        }

       // @PostMapping("/add")
        public void add(@RequestBody User user) {
            userService.wait(user);
        }
    }


