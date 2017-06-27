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
package org.eclipse.reddeer.junit.test.annotation.resources;

public class SimpleTestClassWithMorePossibilities1 {
	
	@CustomMethodAnnotation
	public static String getValue() {
		return "value";
	}
	
	public SimpleTestClassWithMorePossibilities1() {
		
	}

}
