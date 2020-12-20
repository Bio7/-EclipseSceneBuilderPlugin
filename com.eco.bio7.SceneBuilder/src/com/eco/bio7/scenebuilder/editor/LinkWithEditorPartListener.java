/*From: http://murygin.wordpress.com/2012/06/13/link-eclipse-view-to-editor/*/
package com.eco.bio7.scenebuilder.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;

public class LinkWithEditorPartListener implements IPartListener2 {

	private final ILinkedWithEditorView view;

	public LinkWithEditorPartListener(ILinkedWithEditorView view) {

		this.view = view;
	}

	public void partActivated(IWorkbenchPartReference ref) {
		if (ref.getPart(true) instanceof IEditorPart) {
			if (view != null) {
				IViewSite viewSite = view.getViewSite();
				if (viewSite != null) {
					IWorkbenchPage page = viewSite.getPage();
					if (page != null) {
						view.editorActivated(page.getActiveEditor());
					}
				}
			}
		}
	}

	public void partBroughtToTop(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	public void partOpened(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	public void partVisible(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			IEditorPart editor = view.getViewSite().getPage().getActiveEditor();
			if (editor != null) {
				view.editorActivated(editor);
			}
		}
	}

	public void partClosed(IWorkbenchPartReference ref) {
	}

	public void partDeactivated(IWorkbenchPartReference ref) {
	}

	public void partHidden(IWorkbenchPartReference ref) {
	}

	public void partInputChanged(IWorkbenchPartReference ref) {
	}
}