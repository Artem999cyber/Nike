package ControlLicens;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class LicControl {
    private final LicService userService;

    public LicControl() {
        userService = null;
    }

    //@GetMapping
    public List<Lic> getAll() {
        return LicService.getAll();
    }

    // @PostMapping("/add")
    public void add(@RequestBody Lic Lic) {
      //  LicService.add(Lic);
    }
}
