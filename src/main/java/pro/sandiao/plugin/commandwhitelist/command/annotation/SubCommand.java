package pro.sandiao.plugin.commandwhitelist.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 子命令的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubCommand {
    /**
     * 子命令
     */
    String value() default "";

    /**
     * 权限
     */
    String permission() default "";

    /**
     * 使用方法
     */
    String usage() default "";
}
