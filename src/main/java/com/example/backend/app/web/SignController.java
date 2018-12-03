package com.example.backend.app.web;

import com.example.backend.sys.secure.User;
import com.example.backend.sys.secure.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SignController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping(path="/api/signIn",consumes = "application/json")
    public Mono<User> signIn(){
        return Mono.empty();
    }

    @PostMapping(path="/api/signUp",consumes = "application/json")
    public Mono<User> signUp(@RequestBody User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userService.save(user);
    }

    @RequestMapping(path="/api/hehe")
    public Mono<String> hehe(){
        return Mono.just("hehe");
    }
}
