package com.dinninghallapi.foods;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;

public class Foods {

    @Getter
    private static final ArrayList<Foods> menu = new ArrayList<>();

    @Getter
    private final int id;

    @Getter
    private final String name;

    @Getter
    private final int preparation_time;

    @Getter
    private final int complexity;

    @Getter
    private final String cooking_apparatus;

    @JsonCreator
    public Foods(@JsonProperty("id") int id,
                 @JsonProperty("name") String name,
                 @JsonProperty("preparation-time") int preparation_time,
                 @JsonProperty("complexity") int complexity,
                 @JsonProperty("cooking-apparatus") String cooking_apparatus) {
        this.id = id;
        this.name = name;
        this.preparation_time = preparation_time;
        this.complexity = complexity;
        this.cooking_apparatus = cooking_apparatus;

        menu.add(this);

    }

    public static int preparationTime(int id) {
        return menu.get(id - 1).preparation_time;
    }

    @Override
    public String toString() {
        return "Foods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", preparation_time=" + preparation_time +
                ", complexity=" + complexity +
                ", cooking_apparatus='" + cooking_apparatus + '\'' +
                '}';
    }
}