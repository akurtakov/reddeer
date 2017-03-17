/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.reddeer.eclipse.test.datatools.ui;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.jboss.reddeer.eclipse.datatools.connectivity.ui.dse.views.DataSourceExplorerView;
import org.jboss.reddeer.eclipse.datatools.connectivity.ui.wizards.NewCPWizard;
import org.jboss.reddeer.eclipse.datatools.ui.FlatFileProfile;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class ConnectionProfileTest {

	@Test
	public void flatFileConnectionProfileTest() {
		String profile = "Test Flat File Profile";

		FlatFileProfile flatProfile = new FlatFileProfile();
		flatProfile.setName(profile);
		flatProfile.setFolder(new File("target").getAbsolutePath());
		flatProfile.setCharset("UTF-8");
		flatProfile.setStyle("CSV");

		NewCPWizard connWizard = new NewCPWizard();
		connWizard.open();
		connWizard.createFlatFileProfile(flatProfile);

		DataSourceExplorerView dataSourceExplorer = new DataSourceExplorerView();
		dataSourceExplorer.open();
		List<String> flatFileSources = dataSourceExplorer.getFlatFileDataSources();
		assertTrue("Profile '" + profile + "' isn't available", flatFileSources.contains(profile));
	}

}