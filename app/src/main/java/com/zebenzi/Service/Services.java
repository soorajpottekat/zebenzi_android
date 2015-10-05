package com.zebenzi.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 */
public  enum Services {
    //TODO: All of this must come from the server

    TILER("Tiler", "Square Meter", "SQM", 50),
    PAINTER("Painter", "Square Meter", "SQM", 30),
    PLASTERER("Plasterer", "Square Meter", "SQM", 75),
    PAVER("Paver", "Square Meter", "SQM", 25),
    CLADDER("Cladder", "Square Meter", "SQM", 120),
    GARDENER("Gardener", "Day", "DAY", 200),
    PLUMBER("Plumber", "Hour", "HOUR", 200);

    private static final Map<String, Services> SERVICES = new HashMap<>();

    private final String name;
    private final String unit;
    private final String unitAbbr;
    private final float unitPrice;

        private Services(String name, String unit, String unitAbbr, float unitPrice) {
            this.name = name;
            this.unit = unit;
            this.unitAbbr = unitAbbr;
            this.unitPrice = unitPrice;
        }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getUnitAbbr() {
        return unitAbbr;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    static {
        for(Services svc : values()) {
                SERVICES.put(svc.getName(), svc);
            }
    }

    public static Services of(String name) {
        Services services = SERVICES.get(name);
        if(services == null) throw new IllegalArgumentException(name + " not a valid service");
        return services;
    }
}

