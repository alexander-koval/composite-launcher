package com.akoval.launcher.tabs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

class LaunchConfigurationContentProvider implements
		IStructuredContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		ILaunchConfiguration[] configurations = null;
		try {
			configurations = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations();
		} catch (CoreException e) {
			return new ILaunchConfiguration[0];
		}
		return configurations;
	}

	@Override
	public void dispose() {
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	
	}
}