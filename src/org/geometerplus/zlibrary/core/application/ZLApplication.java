/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.zlibrary.core.application;

import java.util.*;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.zlibrary.core.filesystem.*;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;
import org.geometerplus.zlibrary.core.xml.ZLXMLReaderAdapter;

public abstract class ZLApplication {
	public static ZLApplication Instance() {
		return ourInstance;
	}

	private static ZLApplication ourInstance;

	//private static final String MouseScrollUpKey = "<MouseScrollDown>";
	//private static final String MouseScrollDownKey = "<MouseScrollUp>";
	public static final String NoAction = "none";

	public final ZLIntegerRangeOption KeyDelayOption =
		new ZLIntegerRangeOption("Options", "KeyDelay", 0, 5000, 250);

	private ZLApplicationWindow myWindow;
	private ZLView myView;

	private final HashMap<String,ZLAction> myIdToActionMap = new HashMap<String,ZLAction>();
	private Menubar myMenubar;
	//private ZLTime myLastKeyActionTime;

	public int myBatteryLevel = 0;

	protected ZLApplication() {
		ourInstance = this;

		new MenubarCreator().read(ZLResourceFile.createResourceFile("data/default/menubar.xml"));
	}

	final Menubar getMenubar() {
		return myMenubar;
	}

	protected final void setView(ZLView view) {
		if (view != null) {
			myView = view;
			repaintView();
			onViewChanged();
		}
	}

	public final ZLView getCurrentView() {
		return myView;
	}

	final void setWindow(ZLApplicationWindow window) {
		myWindow = window;
	}

	public void initWindow() {
		myWindow.init();
		setView(myView);
	}

	public final void repaintView() {
		if (myWindow != null) {
			myWindow.repaintView();
		}
	}

	public final void scrollViewTo(int viewPage, int shift) {
		if (myWindow != null) {
			myWindow.scrollViewTo(viewPage, shift);
		}
	}

	public final void startViewAutoScrolling(int viewPage) {
		if (myWindow != null) {
			myWindow.startViewAutoScrolling(viewPage);
		}
	}

	public final void onRepaintFinished() {
		if (myWindow != null) {
			myWindow.refreshMenu();
		}
		for (ButtonPanel panel : myPanels) {
			panel.updateStates();
		}
	}

	public final void onViewChanged() {
		hideAllPanels();
	}

	protected final void addAction(String actionId, ZLAction action) {
		myIdToActionMap.put(actionId, action);
	}

	public final boolean isActionVisible(String actionId) {
		final ZLAction action = myIdToActionMap.get(actionId);
		return (action != null) && action.isVisible();
	}

	public final boolean isActionEnabled(String actionId) {
		final ZLAction action = myIdToActionMap.get(actionId);
		return (action != null) && action.isEnabled();
	}

	public final void doAction(String actionId) {
		final ZLAction action = myIdToActionMap.get(actionId);
		if (action != null) {
			action.checkAndRun();
		}
	}

	public String[] getGetSimpleActions() {
		String[] actions = {
				ActionCode.PREV_PAGE,
				ActionCode.NEXT_PAGE,
				ActionCode.PREV_LINE,
				ActionCode.NEXT_LINE,
				ActionCode.PREV_LINK,
				ActionCode.NEXT_LINK,
				ActionCode.FOLLOW_HYPERLINK,
				ActionCode.BACK,
				ActionCode.ROTATE,
				ActionCode.SWITCH_PROFILE,
				ActionCode.SHOW_LIBRARY,
				ActionCode.OPEN_FILE,
				ActionCode.SHOW_PREFERENCES,
				ActionCode.SHOW_BOOK_INFO,
				ActionCode.INITIATE_COPY,
				ActionCode.SHOW_CONTENTS,
				ActionCode.SHOW_BOOKMARKS,
				ActionCode.SHOW_NETWORK_LIBRARY,
				ActionCode.SEARCH,
				ActionCode.FIND_PREVIOUS,
				ActionCode.FIND_NEXT,
				ActionCode.SHOW_NAVIGATION,
				ActionCode.INCREASE_FONT,
				ActionCode.DECREASE_FONT,
				ActionCode.TAP_ZONES,
				ActionCode.NOTHING,
				ActionCode.DEFAULT,
		};
		return actions;
	}

	//may be protected
	abstract public ZLKeyBindings keyBindings();

	public final boolean doActionByKey(int keyId) {
		final String actionId = keyBindings().getBinding(keyId);
		if (actionId != null) {
			final ZLAction action = myIdToActionMap.get(actionId);
			return (action != null) && action.checkAndRun();
		}
		return false;
	}

	public final ZLStringOption getBindingOption(int keyId) {
		return keyBindings().getOption(keyId);
	}

	public void navigate() {
		if (myWindow != null) {
			myWindow.navigate();
		}
	}

	public boolean canNavigate() {
		if (myWindow != null) {
			return myWindow.canNavigate();
		}
		return false;
	}

	public void rotateScreen() {
		if (myWindow != null) {
			myWindow.rotate();
		}
	}

	public boolean canRotateScreen() {
		if (myWindow != null) {
			return myWindow.canRotate();
		}
		return false;
	}

	public boolean closeWindow() {
		onWindowClosing();
		if (myWindow != null) {
			myWindow.close();
		}
		return true;
	}

	public void onWindowClosing() {
	}

	public abstract void openFile(ZLFile file);

	//Action
	static abstract public class ZLAction {
		public boolean isVisible() {
			return true;
		}

		public boolean isEnabled() {
			return isVisible();
		}

		public final boolean checkAndRun() {
			if (isEnabled()) {
				run();
				return true;
			}
			return false;
		}

		public boolean useKeyDelay() {
			return true;
		}

		abstract protected void run();
	}

	static public interface ButtonPanel {
		void updateStates();
		void hide();
	}
	private final HashSet<ButtonPanel> myPanels = new HashSet<ButtonPanel>();
	public final void registerButtonPanel(ButtonPanel panel) {
		myPanels.add(panel);
	}
	public final void unregisterButtonPanel(ButtonPanel panel) {
		myPanels.remove(panel);
	}
	public final void hideAllPanels() {
		for (ButtonPanel panel : myPanels) {
			panel.hide();
		}
	}

	//Menu
	static class Menu {
		public interface Item {
		}

		private final ArrayList<Item> myItems = new ArrayList<Item>();
		private final ZLResource myMenuRes;
		private final ZLResource myActionsRes;

		Menu(ZLResource menu, ZLResource actions) {
			myMenuRes = menu;
			myActionsRes = actions;
		}

		ZLResource getMenuRes() {
			return myMenuRes;
		}

		void addItem(String actionId) {
			ZLResource item = myMenuRes.getResource(actionId);
			ZLResource title = item;
			if (!item.hasValue()) {
				title = myActionsRes.getResource(actionId);
			}
			myItems.add(new Menubar.PlainItem(item, title));
		}

		Menubar.Submenu addSubmenu(String key) {
			Menubar.Submenu submenu =
				new Menubar.Submenu(myMenuRes.getResource(key), myActionsRes);
			myItems.add(submenu);
			return submenu;
		}

		int size() {
			return myItems.size();
		}

		Item getItem(int index) {
			return (Item)myItems.get(index);
		}
	}

	//MenuBar
	public static final class Menubar extends Menu {
		public static final class PlainItem implements Item {
			private final ZLResource myItemRes;
			private final ZLResource myTitleRes;

			public PlainItem(ZLResource item, ZLResource title) {
				myItemRes = item;
				myTitleRes = title;
			}

            public String getActionId() {
				return myItemRes.Name;
			}

            public String getTitle() {
				return myTitleRes.getValue();
			}
		};

		public static final class Submenu extends Menu implements Item {
			public Submenu(ZLResource menu, ZLResource actions) {
				super(menu, actions);
			}

			public String getMenuName() {
				return getMenuRes().getValue();
			}
		};

		public Menubar() {
			super(ZLResource.resource("menu"),
				ZLResource.resource("actions"));
		}
	}

	//MenuVisitor
	static public abstract class MenuVisitor {
		public final void processMenu(ZLApplication application) {
			if (application.myMenubar != null) {
				processMenu(application.myMenubar);
			}
		}

		private final void processMenu(Menu menu) {
			final int size = menu.size();
			for (int i = 0; i < size; ++i) {
				final Menu.Item item = menu.getItem(i);
				if (item instanceof Menubar.PlainItem) {
					processItem((Menubar.PlainItem)item);
				} else if (item instanceof Menubar.Submenu) {
					Menubar.Submenu submenu = (Menubar.Submenu)item;
					processSubmenuBeforeItems(submenu);
					processMenu(submenu);
					processSubmenuAfterItems(submenu);
				}
			}
		}

		protected abstract void processSubmenuBeforeItems(Menubar.Submenu submenu);
		protected abstract void processSubmenuAfterItems(Menubar.Submenu submenu);
		protected abstract void processItem(Menubar.PlainItem item);
	}

	private class MenubarCreator extends ZLXMLReaderAdapter {
		private static final String ITEM = "item";
		private static final String SUBMENU = "submenu";

		private final ArrayList<Menubar.Submenu> mySubmenuStack = new ArrayList<Menubar.Submenu>();

		@Override
		public boolean dontCacheAttributeValues() {
			return true;
		}

		@Override
		public boolean startElementHandler(String tag, ZLStringMap attributes) {
			if (myMenubar == null) {
				myMenubar = new Menubar();
			}
			final ArrayList<Menubar.Submenu> stack = mySubmenuStack;
			final Menu menu = stack.isEmpty() ? myMenubar : (Menu)stack.get(stack.size() - 1);
			if (ITEM == tag) {
				final String id = attributes.getValue("id");
				if (id != null) {
					menu.addItem(id);
				}
			} else if (SUBMENU == tag) {
				final String id = attributes.getValue("id");
				if (id != null) {
					stack.add(menu.addSubmenu(id));
				}
			}
			return false;
		}

		@Override
		public boolean endElementHandler(String tag) {
			if (SUBMENU == tag) {
				final ArrayList<Menubar.Submenu> stack = mySubmenuStack;
				if (!stack.isEmpty()) {
					stack.remove(stack.size() - 1);
				}
			}
			return false;
		}
	}
}
