package com.example.dwbug.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;
//自定义校验注解器

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Long>
{
    Set<Long> set = new HashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {

        long[] vals = constraintAnnotation.vals();
        for(Long i: vals){
            set.add(i);
        }

    }
    //判断是否校验成功

    /**
     *
     * @param integer     传进来的值 需要校验得值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Long integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);


    }
}
