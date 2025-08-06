package org.transport.tool;


import jakarta.annotation.Nonnull;

import java.lang.annotation.*;

@Nonnull
@Documented
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ParametersAreNonnullByDefault {
}
