/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.reddeer.gef.lookup;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.util.ResultRunnable;
import org.eclipse.reddeer.gef.GEFLayerException;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;

/**
 * Lookup for {@link org.eclipse.gef.GraphicalViewer}
 * 
 * @author Andrej Podhradsky (andrej.podhradsky@gmail.com)
 *
 */
public class ViewerLookup {

	protected final Logger log = Logger.getLogger(this.getClass());

	private static ViewerLookup instance;

	private ViewerLookup() {

	}

	/**
	 * Gets the single instance of ViewerLookup.
	 *
	 * @return single instance of ViewerLookup
	 */
	public static ViewerLookup getInstance() {
		if (instance == null) {
			instance = new ViewerLookup();
		}
		return instance;
	}

	/**
	 * Finds a graphical viewer in an active editor.
	 * 
	 * @return Graphical viewer
	 */
	public GraphicalViewer findGraphicalViewer() {
		return findGraphicalViewer(new ActiveEditor().getIEditorPart());
	}

	/**
	 * Finds a graphical viewer in a given editor.
	 * 
	 * @param editorPart
	 *            Editor part
	 * @return Graphical viewer
	 */
	public GraphicalViewer findGraphicalViewer(final IEditorPart editorPart) {
		GraphicalViewer viewer = Display.syncExec(new ResultRunnable<GraphicalViewer>() {
			@Override
			public GraphicalViewer run() {
				return (GraphicalViewer) editorPart.getAdapter(GraphicalViewer.class);
			}
		});
		if (viewer == null) {
			throw new GEFLayerException("Cannot find graphical viewer in a given editor part");
		}
		return viewer;
	}

	/**
	 * Helper class for achieving active {@link org.eclipse.ui.IEditorPart}. This class can be removed when
	 * {@link org.eclipse.reddeer.workbench.lookup} is exported.
	 * 
	 * @author Andrej Podhradsky (andrej.podhradsky@gmail.com)
	 *
	 */
	private class ActiveEditor extends DefaultEditor {

		public ActiveEditor() {
			super();
		}

		public IEditorPart getIEditorPart() {
			return getEditorPart();
		}
	}
}
