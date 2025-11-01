package com.connexus.ticketing_service.feign;

import com.connexus.ticketing_service.dto.UserFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserFeign {

    @GetMapping("/users/{id}")
    UserFeignDTO getUserById(@PathVariable("id") Long id);
}

