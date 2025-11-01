package com.connexus.ticketing_service.feign;

import com.connexus.ticketing_service.dto.EventFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", url = "${event.service.url}")
public interface EventFeign {

    @GetMapping("/events/{id}")
    EventFeignDTO getEventById(@PathVariable("id") Long id);
}
