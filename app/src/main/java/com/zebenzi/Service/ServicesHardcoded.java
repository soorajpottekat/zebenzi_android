package com.zebenzi.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 */
public  enum ServicesHardcoded {
    //TODO: All of this must come from the server

    //service descr, unit, unit abbr, rate/unit, min units, max units, increment
    TILER("Tiler", "Square Meters", "SQM", 50, 20, 5000, 50),
    PAINTER("Painter", "Square Meters", "SQM", 30, 50, 5000, 50),
    GARDENER("Gardener", "Days", "DAY", 200, 1, 10, 1),
    MAID("Maid", "Day", "DAY", 220, 1, 10, 1),
    PLASTERER("Plasterer", "Square Meters", "SQM", 75, 20, 5000, 50),
    PAVER("Paver", "Square Meters", "SQM", 25, 10, 10000, 50),
    CLADDER("Cladder", "Square Meters", "SQM", 120, 10, 1000, 20),
    BRICKLAYER("Bricklayer", "Bricks", "BRK", 2, 100, 10000, 100),
    PLUMBER("Plumber", "Hours", "HRS", 200, 1, 10, 1);

    private static final Map<String, ServicesHardcoded> SERVICES = new HashMap<>();

    private final String name;
    private final String unit;
    private final String unitAbbr;
    private final float unitPrice;
    private final int minUnits;
    private final int maxUnits;
    private final int increment;

        private ServicesHardcoded(String name, String unit, String unitAbbr, float unitPrice, int minUnits, int maxUnits, int increment) {
            this.name = name;
            this.unit = unit;
            this.unitAbbr = unitAbbr;
            this.unitPrice = unitPrice;
            this.minUnits = minUnits;
            this.maxUnits = maxUnits;
            this.increment = increment;
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

    public int getMinUnits() { return minUnits; }

    public int getMaxUnits() { return maxUnits; }

    public int getIncrement() { return increment; }

    static {
        for(ServicesHardcoded svc : values()) {
                SERVICES.put(svc.getName(), svc);
            }
    }

    public static ServicesHardcoded of(String name) {
        ServicesHardcoded services = SERVICES.get(name);
        if(services == null) throw new IllegalArgumentException(name + " not a valid service");
        return services;
    }





}

