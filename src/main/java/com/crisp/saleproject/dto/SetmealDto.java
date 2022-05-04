package com.crisp.saleproject.dto;

import com.crisp.saleproject.entity.Setmeal;
import com.crisp.saleproject.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
