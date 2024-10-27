package ControlLicens;

import java.util.List;

public class LicService {
    private static List<Lic> all;

    public static List<Lic> getAll() {
        return all;
    }

    public static void setAll(List<Lic> all) {
        LicService.all = all;
    }
    //public static List<Lic> getAll() {
    }

