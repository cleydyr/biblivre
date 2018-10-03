package com.github.cleydyr.pimaco;

import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public enum SheetSize {
	A4(21.0, 29.7), LETTER(21.59, 27.94);

	private SheetSize(double sheetWidth, double sheetHeight) {
		Function<Double, Quantity<Length>> converter =
				Utils.DOUBLE_TO_CENTIMETER_CONVERTER;

		this.sheetWidth = converter.apply(sheetWidth);
		this.sheetHeight = converter.apply(sheetHeight);
	}

	public Quantity<Length> getSheetWidth() {
		return sheetWidth;
	}

	public Quantity<Length> getSheetHeight() {
		return sheetHeight;
	}

	private Quantity<Length> sheetWidth;

	private Quantity<Length> sheetHeight;
}
