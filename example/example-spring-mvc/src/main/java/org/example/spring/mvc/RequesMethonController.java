package org.example.spring.mvc;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requesMethon")
public class RequesMethonController {

    @GetMapping
    public ReturnObject getMethon(ReturnObject returnObject) {
        return returnObject;
    }

    @PostMapping
    public ReturnObject postMethon(@RequestBody ReturnObject returnObject) {
        return returnObject;
    }

    @PutMapping
    public ReturnObject putMethon(@RequestBody ReturnObject returnObject) {
        return returnObject;
    }

    @DeleteMapping
    public ReturnObject deleteMethon(ReturnObject returnObject) {
        return returnObject;
    }
}
