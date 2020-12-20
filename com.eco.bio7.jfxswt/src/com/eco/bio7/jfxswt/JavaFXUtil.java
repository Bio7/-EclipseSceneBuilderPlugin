package com.eco.bio7.jfxswt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;

public class JavaFXUtil {

	public FXCanvas createFXCanvas(Composite com, int style) {

		FXCanvas canvas = new FXCanvas(com, style) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();
				return new Point(width, height);
			}
		};

		return canvas;

	}

}
