package com.zebenzi.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 */
public  enum Services {

    TILING("Tiling", "Square Meter", "SQM", 50),
    PAINTING("Painting", "Square Meter", "SQM", 30),
    PLASTERING("Painting", "Square Meter", "SQM", 75),
    PAVING("Paving", "Square Meter", "SQM", 25),
    CLADDING("Cladding", "Square Meter", "SQM", 120),
    GARDENING("Gardening", "Day", "DAY", 200);

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

