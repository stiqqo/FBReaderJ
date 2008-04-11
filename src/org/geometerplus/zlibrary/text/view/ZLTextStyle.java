package org.geometerplus.zlibrary.text.view;

import org.geometerplus.zlibrary.core.util.ZLColor;

public interface ZLTextStyle {
	String getFontFamily();
	int getFontSize();

	ZLColor getColor();

	boolean isBold();
	boolean isItalic();
	int getLeftIndent();
	int getRightIndent();
	int getFirstLineIndentDelta();
	int getLineSpacePercent();
	int getVerticalShift();
	int getSpaceBefore();
	int getSpaceAfter();
	byte getAlignment();

	boolean allowHyphenations();

	ZLTextStyle getBase();
}