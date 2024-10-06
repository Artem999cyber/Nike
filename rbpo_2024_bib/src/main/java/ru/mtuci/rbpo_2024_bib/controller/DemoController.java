package ru.mtuci.rbpo_2024_bib.controller;

import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_bib.model.Demo;
import ru.mtuci.rbpo_2024_bib.service.DemoService;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private DemoService demoService = null;

    public DemoController() {
        this.demoService = demoService;
    }

    @GetMapping
    public List<Demo> findAll() {
        return demoService.findAll();
    }

    @PostMapping("/save")
    public void save(@RequestBody Demo demo) {
        demoService.save(demo);
    }
}