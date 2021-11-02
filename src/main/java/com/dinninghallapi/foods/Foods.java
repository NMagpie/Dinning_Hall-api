package com.dinninghallapi.foods;

import lombok.Getter;

public class Foods {

    @Getter
    private final int id;

    @Getter
    private String name;

    @Getter
    private int preparation_time;

    @Getter
    private int complexity;

    @Getter
    private String cooking_apparatus;

    public Foods(int id) {
        this.id = id;

        switch (id) {
            case (1):
                this.name = "pizza";
                this.preparation_time = 20;
                this.complexity = 2;
                this.cooking_apparatus = "oven";
                break;

            case (2):
                this.name = "salad";
                this.preparation_time = 10;
                this.complexity = 1;
                this.cooking_apparatus = null;
                break;

            case (3):
                this.name = "zeama";
                this.preparation_time = 7;
                this.complexity = 1;
                this.cooking_apparatus = "stove";
                break;

            case (4):
                this.name = "Scallop Sashimi with Meyer Lemon Confit";
                this.preparation_time = 32;
                this.complexity = 3;
                this.cooking_apparatus = null;
                break;

            case (5):
                this.name = "Island Duck with Mulberry Mustard";
                this.preparation_time = 35;
                this.complexity = 3;
                this.cooking_apparatus = "oven";
                break;

            case (6):
                this.name = "Waffles";
                this.preparation_time = 10;
                this.complexity = 1;
                this.cooking_apparatus = "stove";
                break;

            case (7):
                this.name = "Aubergine";
                this.preparation_time = 20;
                this.complexity = 2;
                this.cooking_apparatus = null;
                break;

            case (8):
                this.name = "Lasagna";
                this.preparation_time = 30;
                this.complexity = 2;
                this.cooking_apparatus = "oven";
                break;

            case (9):
                this.name = "Burger";
                this.preparation_time = 15;
                this.complexity = 1;
                this.cooking_apparatus = "oven";
                break;

            case (10):
                this.name = "Gyros";
                this.preparation_time = 15;
                this.complexity = 1;
                this.cooking_apparatus = null;
                break;
            default:
                System.out.println("Wrong id");
                break;
        }

    }

    public static int preparationTime(int id) {
        switch (id) {
            case 1:
            case 7:
                return 20;
            case 2:
            case 6:
                return 10;
            case 3:
                return 7;
            case 4:
                return 32;
            case 5:
                return 35;
            case 8:
                return 30;
            case 9:
            case 10:
                return 15;
        }
        return 0;
    }

}
