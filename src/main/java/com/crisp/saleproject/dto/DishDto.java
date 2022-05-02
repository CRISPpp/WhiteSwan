package com.crisp.saleproject.dto;


import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
