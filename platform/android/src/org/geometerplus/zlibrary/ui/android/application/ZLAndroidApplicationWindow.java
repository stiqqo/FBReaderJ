package org.geometerplus.zlibrary.ui.android.application;

import java.util.*;

import android.view.Menu;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.application.ZLApplicationWindow;

import org.geometerplus.zlibrary.ui.android.view.ZLAndroidViewWidget;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

public final class ZLAndroidApplicationWindow extends ZLApplicationWindow {
	private final HashMap<Menu.Item,ZLApplication.Menubar.PlainItem> myMenuItemMap =
		new HashMap<Menu.Item,ZLApplication.Menubar.PlainItem>();

	private class MenuBuilder extends ZLApplication.MenuVisitor {
		private int myItemCount = Menu.FIRST;
		private final Stack<Menu> myMenuStack = new Stack<Menu>();

		private MenuBuilder(Menu menu) {
			myMenuStack.push(menu);
		}
		protected void processSubmenuBeforeItems(ZLApplication.Menubar.Submenu submenu) {
			myMenuStack.push(myMenuStack.peek().addSubMenu(0, myItemCount++, submenu.getMenuName()));	
		}
		protected void processSubmenuAfterItems(ZLApplication.Menubar.Submenu submenu) {
			myMenuStack.pop();
		}
		protected void processItem(ZLApplication.Menubar.PlainItem item) {
			Menu.Item menuItem = myMenuStack.peek().add(0, myItemCount++, item.getName());
			menuItem.setClickListener(myMenuListener);
			myMenuItemMap.put(menuItem, item);
		}
		protected void processSepartor(ZLApplication.Menubar.Separator separator) {
			//myMenuStack.peek().addSeparator(0, myItemCount++);
		}
	}

	private final Menu.OnClickListener myMenuListener =
		new Menu.OnClickListener() {
			public boolean onClick(Menu.Item item) {
				getApplication().doAction(myMenuItemMap.get(item).getActionId());
				return true;
			}
		};

	public ZLAndroidApplicationWindow(ZLApplication application) {
		super(application);
	}

	public void buildMenu(Menu menu) {
		new MenuBuilder(menu).processMenu(getApplication());
		refresh();
	}

	protected void refresh() {
		super.refresh();
		for (Map.Entry<Menu.Item,ZLApplication.Menubar.PlainItem> entry : myMenuItemMap.entrySet()) {
			final String actionId = entry.getValue().getActionId();
			final ZLApplication application = getApplication();
			entry.getKey().setShown(application.isActionVisible(actionId) && application.isActionEnabled(actionId));
		}
	}

	public void initMenu() {
		// TODO: implement
	}

	public void setCaption(String caption) {
		// TODO: implement
		//myFrame.setTitle(caption);
	}

	protected ZLAndroidViewWidget createViewWidget() {
		// TODO: implement
		ZLAndroidViewWidget viewWidget =
			new ZLAndroidViewWidget(getApplication().AngleStateOption.getValue());
		return viewWidget;
	}

	public void addToolbarItem(ZLApplication.Toolbar.Item item) {
		// TODO: implement
	}

	public void setToolbarItemState(ZLApplication.Toolbar.Item item, boolean visible, boolean enabled) {
		// TODO: implement
	}

	public void closeInternal() {
		((ZLAndroidLibrary)ZLAndroidLibrary.getInstance()).finish();
	}

	//public void setToggleButtonState(ZLApplication.Toolbar.ButtonItem item) {
		// TODO: implement
	//}

	public void setFullscreen(boolean fullscreen) {
		// TODO: implement
	}

	public boolean isFullscreen() {
		// TODO: implement
		return false;
	}
}