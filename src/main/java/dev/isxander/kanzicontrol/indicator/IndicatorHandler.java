package dev.isxander.kanzicontrol.indicator;

@FunctionalInterface
public interface IndicatorHandler {
    boolean handleIndicator(int duration);
}
