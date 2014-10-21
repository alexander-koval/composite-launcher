/**
 * 
 */
package com.akoval.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author alexander
 *
 */
public class CompositeLaunchConfigurationDelegate extends
		LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		try {
			List<String> list = configuration.getAttribute(
					CompositeLaunchConstants.MEMENTO, new ArrayList<String>());
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				String memento = it.next();
				ILaunchConfiguration config = manager
						.getLaunchConfiguration(memento);
				if (configuration.getMemento().equals(config.getMemento())) {
					IStatus status = new Status(Status.ERROR, "unknown",
							"Infinite loop detected for '"
									+ configuration.getName()
									+ "' configuration");
					throw new CoreException(status);
				} else {
					config.launch(mode, monitor);
				}
			}
		} catch (CoreException e) {
			class AsyncException implements Runnable {
				Exception exception;

				public AsyncException(Exception exception) {
					this.exception = exception;
				}

				@Override
				public void run() {
					MessageDialog.openError(Display.getCurrent()
							.getActiveShell(), "Error", exception.getMessage());
				}

			}
			Display.getDefault().syncExec(new AsyncException(e));
		}
	}
}
