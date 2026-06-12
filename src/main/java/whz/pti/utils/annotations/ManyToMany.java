package whz.pti.utils.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
    String joinTable();
    String joinColumn();
    String inverseColumn();
    Class<?> repoClass();
}