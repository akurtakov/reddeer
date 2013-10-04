package org.jboss.reddeer.swt.lookup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.jboss.reddeer.junit.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchSite;
import org.hamcrest.Matcher;
import org.jboss.reddeer.swt.exception.SWTLayerException;
import org.jboss.reddeer.swt.lookup.WidgetResolver;
import org.jboss.reddeer.swt.reference.ReferencedComposite;
import org.jboss.reddeer.swt.matcher.AndMatcher;
import org.jboss.reddeer.swt.matcher.ClassMatcher;
import org.jboss.reddeer.swt.matcher.MatcherBuilder;
import org.jboss.reddeer.swt.util.Display;
import org.jboss.reddeer.swt.util.ObjectUtil;
import org.jboss.reddeer.swt.util.ResultRunnable;

/**
 * Widget Lookup methods contains core lookup and resolving widgets
 * @author Jiri Peterka
 * @author Jaroslav Jankovic
 *
 */
public class WidgetLookup {
	
	private static WidgetLookup instance = null;
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private WidgetLookup() {
	}
	
	/**
	 * Returns WidgetLookup instance
	 * @return widgetLookup instance
	 */
	public static WidgetLookup getInstance() {
		if (instance == null) instance = new WidgetLookup();
		return instance;
	}
	
	/**
	 * Checks if widget is enabled
	 * @param widget
	 * @return
	 */
	public boolean isEnabled(Widget widget) {
		boolean ret = true;
		Object o = null;
		try {
			o = ObjectUtil.invokeMethod(widget, "isEnabled");
		} catch (RuntimeException e) {
			return true;
		}
		if (o == null) return ret;
		if (o instanceof Boolean) {
			ret = ((Boolean)o).booleanValue();
		}
		return ret;
	}
	
	/**
	 * Checks if widget is visible
	 * @param widget given widget
	 * @return true if wideget is visible, false otherwise
	 */
	public boolean isVisible(Widget widget) {
		boolean ret = true;
		Object o = null;
		try {
			o = ObjectUtil.invokeMethod(widget, "isVisible");
		} catch (RuntimeException e) {
			throw new SWTLayerException("Runtime error during checking widget visibility");
		}
		if (o == null) return ret;
		if (o instanceof Boolean) {
			ret = ((Boolean)o).booleanValue();
		}
		return ret;
	}
	
	
	/**
	 * Send click notification to a widget
	 * @param widget
	 */
	public void sendClickNotifications(Widget widget) {
		notify(SWT.Selection,widget);
	}

	/**
	 * Notifies widget with given event type
	 * @param eventType given event type
	 * @param widget target widget
	 */
	public void notify(int eventType, Widget widget) {
		Event event = createEvent(widget);
		notify(eventType, event, widget);
		
	}
	
	public void notifyHyperlink(int eventType, Widget widget) {
		Event event = createHyperlinkEvent(widget);
		notify(eventType, event, widget);
		
	}
	
	public void notifyItem(int eventType, int detail, Widget widget, Widget widgetItem) {
		Event event = createEventItem(eventType, detail, widget, widgetItem);
		notify(eventType, event, widget);
		
	}

	private Event createEvent(Widget widget) {
		Event event = new Event();
		event.time = (int) System.currentTimeMillis();
		event.widget = widget;
		event.display = Display.getDisplay();
		return event;
	}
	
	private Event createHyperlinkEvent(Widget widget){
		Event event = new Event();
		event.time = (int) System.currentTimeMillis();
		event.widget = widget;
		event.display = Display.getDisplay();
		event.button=1;
		event.x=0;
		event.y=0;
		return event;
	}
	
	private Event createEventItem(int eventType, int detail, Widget widget, Widget widgetItem) {
		Event event = new Event();
		event.display = Display.getDisplay();
		event.time = (int) System.currentTimeMillis();
		event.item = widgetItem;
		event.widget = widget;
		event.detail = detail;
		event.type = eventType;
		return event;
	}
	
	
	
	private void notify(final int eventType, final Event createEvent, final Widget widget) {
		createEvent.type = eventType;
		
		Display.asyncExec(new Runnable() {
			public void run() {
				if ((widget == null) || widget.isDisposed()) {
					return;
				}
								
				widget.notifyListeners(eventType, createEvent);
			}
		});

		// Wait for synchronization
		Display.syncExec(new Runnable() {
			public void run() {
				// do nothing here
			}
		});
	}
	
	
	/**
	 * Mehod looks for active widget matching given criteria like reference composite, class, etc.
	 * @param refComposite reference composite within lookup will be performed
	 * @param clazz given class for a lookup
	 * @param index widget index for a lookup
	 * @param matchers additional matchers
	 * @return returns matching widget
	 */
	@SuppressWarnings({ "rawtypes","unchecked" })
	public <T extends Widget> T activeWidget(ReferencedComposite refComposite, Class<T> clazz, int index, Matcher... matchers) {		
		Widget properWidget = null;
		
		ClassMatcher cm = new ClassMatcher(clazz);
		Matcher[] allMatchers = MatcherBuilder.getInstance().addMatcher(matchers, cm);
		AndMatcher am  = new AndMatcher(allMatchers);
		
		logger.debug("Search for activeWidget of class: " + clazz.getName()
				+  "\n  index: " + index);
		for (int ind = 0 ; ind < matchers.length ; ind++ ){
			logger.debug("Matcher: " + matchers[ind].getClass());
		}
		if (refComposite == null) {
			properWidget = getProperWidget(activeWidgets(null, am), index); 
			return (T)properWidget;
		}
		properWidget = getProperWidget(activeWidgets(refComposite.getControl(), am), index);		
		return (T)properWidget;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<? extends Widget> activeWidgets(Control refComposite, Matcher matcher) {
		if(refComposite == null){
			refComposite = getActiveWidgetParentControl();
		}

		if (refComposite == null){
			logger.warn("Unable to find active control");
		}
		List<? extends Widget> widgets = findControls(refComposite, matcher, true);
		return widgets;
	}
	
	
	/**
	 * Looks for active parent control. Either finds activeWorkbenchReference control or activeShell 
	 * @return active workbench control or active shell
	 */
	public Control getActiveWidgetParentControl() {
		Control control = null;
		
		IWorkbenchPartReference activeWorkbenchReference = WorkbenchLookup.findActiveWorkbenchPart();
		Shell activeWorkbenchParentShell = getShellForActiveWorkbench(activeWorkbenchReference);
		
		Shell activeShell = new ShellLookup().getActiveShell();
		if (activeWorkbenchParentShell == null || activeWorkbenchParentShell != activeShell){
			if (activeShell != null){
				control = activeShell;	
			}
			else{
				// try to find active shell one more time
				control =  ShellLookup.getInstance().getActiveShell();
			}
		}			
		else {
			control = WorkbenchLookup.getWorkbenchControl(activeWorkbenchReference);
		}
		return control;
	}

	private Shell getShellForActiveWorkbench(IWorkbenchPartReference workbenchReference) {
		if (workbenchReference == null) {
			return null;
		}
		IWorkbenchPart wPart = workbenchReference.getPart(true);
		if (wPart == null) {
			return null;
		}
		IWorkbenchSite wSite = wPart.getSite();
		if (wSite == null) {
			return null;
		}
		return wSite.getShell();
	}

	private <T extends Widget> T getProperWidget(List<T> widgets, int index) {
		T widget = null;
		if (widgets.size() > index)
			widget = widgets.get(index);
		else
			throw new SWTLayerException("No matching widget found");
		
		if (widget == null) throw new SWTLayerException("Matching widget was null");
		return widget;
	}
	
	/**
	 * Finds Control for active parent
	 * @param matcher criteria matcher
	 * @param recursive true for recursive lookup
	 * @return
	 */
	public<T extends Widget> List<T> findActiveParentControls(final Matcher<T> matcher, final boolean recursive) {
		List<T> findControls = findControls(getActiveWidgetParentControl(), matcher, recursive);
		return findControls;
	}
	
	/**
	 * Find Controls for parent widget matching
	 * @param parentWidget given parent widget - root for lookup
	 * @param matcher criteria matcher
	 * @param recursive true if search should be recursive
	 * @return list of matching widgets
	 */
	private <T extends Widget> List<T> findControls(final Widget parentWidget, 
			final Matcher<T> matcher, final boolean recursive) {
		List<T> ret = Display.syncExec(new ResultRunnable<List<T>>() {

			@Override
			public List<T> run() {
				 List<T> findControlsUI = findControlsUI(parentWidget, matcher, recursive);
				 return findControlsUI;
			}
		});
		return ret;
	}

	/**
	 * Return control with focus
	 * 
	 * @return control with focus
	 */
	public Control getFocusControl() {
		Control c = Display.syncExec(new ResultRunnable<Control>() {
			@Override
			public Control run() {
				Control focusControl = Display.getDisplay().getFocusControl();
				return focusControl;
			}
		});
		return c;
	}
	
	
	/**
	 * Create lists of widget matching matcher (can be called recursively)
	 * @param parentWidget parent widget
	 * @param matcher
	 * @param recursive
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends Widget> List<T> findControlsUI(final Widget parentWidget, final Matcher<T> matcher, final boolean recursive) {
	
		if ((parentWidget == null) || parentWidget.isDisposed())
			return new ArrayList<T>();
		
		
		if (!visible(parentWidget)) {
			return new ArrayList<T>();
		}
		
		LinkedHashSet<T> controls = new LinkedHashSet<T>();
		
		if (matcher.matches(parentWidget) && !controls.contains(parentWidget))
			try {
				controls.add((T) parentWidget);
			} catch (ClassCastException exception) {
				throw new IllegalArgumentException("The specified matcher should only match against is declared type.", exception);
			}
		if (recursive) {
			List<Widget> children = WidgetResolver.getInstance().getChildren(parentWidget);
			controls.addAll(findControlsUI(children, matcher, recursive));
		}
		return new ArrayList<T>(controls);
	}

	/**
	 * Creates matching list of widgets from given list of widgets matching matcher, can find recursively in each child
	 * Note: Must be used in UI Thread
	 * @param widgets - given list of widgets
	 * @param matcher - given hamcrest matcher
	 * @param recursive - recursive switch for searching in children
	 * @return
	 */
	private <T extends Widget> List<T> findControlsUI(final List<Widget> widgets, final Matcher<T> matcher, final boolean recursive) {
		LinkedHashSet<T> list = new LinkedHashSet<T>();
		for (Widget w : widgets) {
			list.addAll(findControlsUI(w, matcher, recursive));
		}
		return new ArrayList<T>(list);
	}
	
	/**
	 * Returns true if instance is visible
	 * @param w
	 * @return
	 */
	private boolean visible(Widget w) {
		return !((w instanceof Control) && !((Control) w).getVisible());
	}
}
