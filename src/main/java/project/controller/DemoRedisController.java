package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.dto.DemoRedisReqDto;
import project.service.DemoRedisService;

@RestController
@RequiredArgsConstructor
public class DemoRedisController {

    private final DemoRedisService demoRedisService;

    @PostMapping("/redis")
    public boolean create(@RequestBody DemoRedisReqDto reqDto) {
        demoRedisService.setRedisValue(reqDto.getKey(), reqDto.getValue());
        return true;
    }

    @GetMapping("/redis")
    public String read(@RequestParam String key) {
        return demoRedisService.getRedisValue(key);
    }

    @PutMapping("/redis")
    public boolean update(@RequestBody DemoRedisReqDto reqDto) {
        demoRedisService.updateRedisValue(reqDto.getKey(), reqDto.getValue());
        return true;
    }

    @DeleteMapping("/redis")
    public boolean delete(@RequestBody DemoRedisReqDto reqDto) {
        demoRedisService.deleteRedisValue(reqDto.getKey());
        return true;
    }
}
