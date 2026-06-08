package biblivre.core.configurations;

import biblivre.core.AbstractHandler;
import biblivre.core.enums.ActionResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequiresFeatureFlagAspect {
    private final FlagsProvider flagsProvider;

    public RequiresFeatureFlagAspect(FlagsProvider flagsProvider) {
        this.flagsProvider = flagsProvider;
    }

    @Around("@annotation(requiresFeatureFlag)")
    public Object enforceFeatureFlag(
            ProceedingJoinPoint joinPoint, RequiresFeatureFlag requiresFeatureFlag)
            throws Throwable {
        if (!flagsProvider.isFlagEnabled(requiresFeatureFlag.value())) {
            AbstractHandler handler = (AbstractHandler) joinPoint.getTarget();
            handler.setMessage(ActionResult.WARNING, "error.no_permission");

            return null;
        }

        return joinPoint.proceed();
    }
}
