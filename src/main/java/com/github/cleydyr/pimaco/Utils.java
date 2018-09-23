package com.github.cleydyr.pimaco;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public interface Utils {

	public static final Unit<Length> CENTIMETER = MetricPrefix.CENTI(Units.METRE);

	public static Function<Double, Quantity<Length>> DOUBLE_TO_CENTIMETER_CONVERTER =
			quantity -> Quantities.getQuantity(new BigDecimal(quantity), CENTIMETER);

}
