package com.InfoSystem.AutoService.ForAuto;

// Вспомогательный класс - enum для вывода соответсвующих к моделям автомобилям их поколений
public enum Brand {
    // Список автомобилей (можно дополнить)
    TOYOTA(new Model[]{
            new Model("Camry", new String[]{"XV50", "XV70"}),
            new Model("Corolla", new String[]{"E140", "E170"})
    }),
    HONDA(new Model[]{
            new Model("Civic", new String[]{"FC", "FK"}),
            new Model("Accord", new String[]{"CR", "CP"})
    });

    private final Model[] models;

    Brand(Model[] models) {
        this.models = models;
    }

    public Model[] getModels() {
        return models;
    }

    public static class Model {
        private final String name;
        private final String[] generations;

        public Model(String name, String[] generations) {
            this.name = name;
            this.generations = generations;
        }

        public String getName() {
            return name;
        }

        public String[] getGenerations() {
            return generations;
        }
    }
    public static Brand parseBrand(String brandName) {
        if (brandName == null || brandName.isEmpty()) {
            System.out.println("null");
            return null;
        }
        try {
            System.out.println(Brand.valueOf(brandName.toUpperCase()));
            return Brand.valueOf(brandName.toUpperCase());
        } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid brand name: " + brandName);
            }
        }
    }