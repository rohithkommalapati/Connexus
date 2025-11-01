package com.connexus.eventservice.feign;

import com.connexus.eventservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserFeignClient {
    @GetMapping("/connexus/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long id);

    @PostMapping("/connexus/users/batch")
    List<UserDTO> getUsersByIds(@RequestBody List<Long> ids);
}
