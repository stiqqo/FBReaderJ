/*
 * Copyright (C) 2009-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.preferences;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidKeyUtil;

import org.geometerplus.fbreader.fbreader.*;
import org.geometerplus.fbreader.Paths;

public class PreferenceActivity extends ZLPreferenceActivity {
	public PreferenceActivity() {
		super("Preferences");
	}

	/*private static final class ColorProfilePreference extends ZLSimplePreference {
		private final FBReader myFBReader;
		private final Screen myScreen;
		private final String myKey;

		static final String createTitle(ZLResource resource, String resourceKey) {
			final ZLResource r = resource.getResource(resourceKey);
			return r.hasValue() ? r.getValue() : resourceKey;
		}

		ColorProfilePreference(Context context, FBReader fbreader, Screen screen, String key, String title) {
			super(context);
			myFBReader = fbreader;
			myScreen = screen;
			myKey = key;
			setTitle(title);
		}

		@Override
		public void onAccept() {
		}

		@Override
		public void onClick() {
			myScreen.setSummary(getTitle());
			myFBReader.setColorProfileName(myKey);
			myScreen.close();
		}
	}*/

	@Override
	protected void init() {
		final Category libraryCategory = createCategory("Library");
		/*
		libraryCategory.addPreference(new InfoPreference(
			this,
			libraryCategory.Resource.getResource("path").getValue(),
			Constants.BOOKS_DIRECTORY)
		);
		*/
		libraryCategory.addPreference(new ZLStringOptionPreference(
			this,
			Paths.BooksDirectoryOption,
			libraryCategory.Resource,
			"path")
		);
		final Category interactionCategory = createCategory("interaction");
		final Screen keyBindingsScreen = interactionCategory.createPreferenceScreen("keyBindings");
		keyBindingsScreen.setSummary(keyBindingsScreen.Resource.getResource("summary").getValue());
		final Category keyBindingCategory = keyBindingsScreen.createCategory(null);

		ZLResource keysResource = ZLResource.resource("keys");
		ZLResource actionsResource = ZLResource.resource("actions");
		String[] actions = ZLApplication.Instance().getGetSimpleActions();
		int[] keys = {
				KeyEvent.KEYCODE_VOLUME_DOWN,
				KeyEvent.KEYCODE_VOLUME_UP,
				KeyEvent.KEYCODE_DPAD_CENTER,
				KeyEvent.KEYCODE_DPAD_UP,
				KeyEvent.KEYCODE_DPAD_DOWN,
				KeyEvent.KEYCODE_DPAD_RIGHT,
				KeyEvent.KEYCODE_DPAD_LEFT,
				KeyEvent.KEYCODE_BACK,
				KeyEvent.KEYCODE_CAMERA,
		};

		for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
			String key = ZLAndroidKeyUtil.getKeyNameByCode(keys[keyIndex]);
			keyBindingCategory.addPreference(new StringListPreference(
				this, keysResource.getResource(key), actionsResource,
				actions, ZLApplication.Instance().getBindingOption(keys[keyIndex])));
		}

		final Category lookNFeelCategory = createCategory("LookNFeel");

		final Screen appearanceScreen = lookNFeelCategory.createPreferenceScreen("appearanceSettings");
		appearanceScreen.setSummary( appearanceScreen.Resource.getResource("summary").getValue() );
		appearanceScreen.setOnPreferenceClickListener(
				new PreferenceScreen.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						((FBReader) FBReader.Instance()).showOptionsDialog();
						return true;
					}
				}
		);

		final Screen statusLineScreen = lookNFeelCategory.createPreferenceScreen("scrollBar");
		statusLineScreen.setSummary(statusLineScreen.Resource.getResource("summary").getValue());
		final Category statusLineCategory = statusLineScreen.createCategory(null);

		String[] scrollBarTypes = {"hide", "show", "showAsProgress", "showAsFooter"};
		statusLineCategory.addPreference(new StringListPreference(
			this, statusLineCategory.Resource.getResource("scrollbarType"), null,
			scrollBarTypes, ((FBReader)FBReader.Instance()).ScrollbarTypeOption));

		String[] footerSizes = {"small", "normal", "large"};
		statusLineCategory.addPreference(new StringListPreference(
			this, statusLineCategory.Resource.getResource("footerSize"), null,
			footerSizes, ((FBReader)FBReader.Instance()).FooterSizeOption));

		String[] footerLongTaps = {"longTapRevert", "longTapNavigate"};
		statusLineCategory.addPreference(new StringListPreference(
			this, statusLineCategory.Resource.getResource("footerLongTap"), null,
			footerLongTaps, ((FBReader)FBReader.Instance()).FooterLongTap));

		statusLineCategory.addOption(ZLAndroidApplication.Instance().FooterShowClock, "showClock");
		statusLineCategory.addOption(ZLAndroidApplication.Instance().FooterShowBattery, "showBattery");
		statusLineCategory.addOption(ZLAndroidApplication.Instance().FooterShowProgress, "showProgress");

		lookNFeelCategory.addOption(ZLAndroidApplication.Instance().AutoOrientationOption, "autoOrientation");
		if (!ZLAndroidApplication.Instance().isAlwaysShowStatusBar()) {
			lookNFeelCategory.addOption(ZLAndroidApplication.Instance().ShowStatusBarOption, "showStatusBar");
		}
		lookNFeelCategory.addOption(ZLAndroidApplication.Instance().DontTurnScreenOffOption, "dontTurnScreenOff");

		/*
		final FBReader fbreader = (FBReader)FBReader.Instance();
		final Screen colorProfileScreen = lookNFeelCategory.createPreferenceScreen("colorProfile");
		final Category colorProfileCategory = colorProfileScreen.createCategory(null);
		final ZLResource resource = colorProfileCategory.Resource;
		colorProfileScreen.setSummary(ColorProfilePreference.createTitle(resource, fbreader.getColorProfileName()));
		for (String key : ColorProfile.names()) {
			colorProfileCategory.addPreference(new ColorProfilePreference(
				this, fbreader, colorProfileScreen, key, ColorProfilePreference.createTitle(resource, key)
			));
		}
		*/

		final Category scrollingCategory = createCategory("Scrolling");
		final ScrollingPreferences scrollingPreferences = ScrollingPreferences.Instance();
		scrollingCategory.addOption(scrollingPreferences.FlickOption, "flick");
		scrollingCategory.addOption(scrollingPreferences.AnimateOption, "animated");
		scrollingCategory.addOption(scrollingPreferences.HorizontalOption, "horizontal");
	}
}

class StringListPreference extends ZLStringListPreference {
	private ZLOption myOption;
	private String[] myCodes;

	StringListPreference(Context context, ZLResource optResource, ZLResource valResource,
		String [] codes, ZLOption option) {
		super(context, optResource, valResource);
		myCodes = codes;
		myOption = option;

		final String[] texts = new String[myCodes.length];
		valResource = (valResource == null ? optResource : valResource);
		for (int i = 0; i < myCodes.length; ++i) {
			texts[i] = valResource.getResource(myCodes[i]).getValue();
		}

		setLists(myCodes, texts);

		if (myOption instanceof ZLIntegerRangeOption) {
			setInitialValue(myCodes[
				Math.max(0, Math.min(myCodes.length - 1, ((ZLIntegerRangeOption)myOption).getValue()))]);
		}

		if (myOption instanceof ZLStringOption) {
			String initVal = ((ZLStringOption)myOption).getValue();
			for (int i = 0; i < myCodes.length; ++i) {
				if (myCodes[i].equals(initVal)){
					setInitialValue(myCodes[i]);
					break;
				}
			}
		}
	}

	public void onAccept() {
		final String strValue = getValue();
		if (myOption instanceof ZLIntegerRangeOption) {
			int intValue = 0;
			for (int i = 0; i < myCodes.length; ++i) {
				if (strValue == myCodes[i]) {
					intValue = i;
					break;
				}
			}
			((ZLIntegerRangeOption)myOption).setValue(intValue);
		}
		if (myOption instanceof ZLStringOption) {
			((ZLStringOption)myOption).setValue(strValue);
		}
	}
}
