package ru.mtuci.rbpo_2024_bib.service;

import ru.mtuci.rbpo_2024_bib.model.Demo;

import java.util.List;

public interface DemoService {
    void save(Demo demo);
    List<Demo> findAll();
    Demo findById(long id);
}
