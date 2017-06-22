package difficultyPrediction.web;

import java.lang.reflect.Field;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserView;

import fluorite.recorders.EHPartRecorder;

public class AWebBrowserAccessor implements WebBrowserAccessor {
	public AWebBrowserAccessor() {
		EHPartRecorder.getInstance().addPartListener(this);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof WebBrowserEditor)
        {
            WebBrowserEditor editor = (WebBrowserEditor) part;

            try {
                Field webBrowser = editor.getClass().getDeclaredField("webBrowser");
                webBrowser.setAccessible(true);
                BrowserViewer viewer = (BrowserViewer)webBrowser.get(editor);

                Field browser = viewer.getClass().getDeclaredField("browser");
                browser.setAccessible(true);
                Browser swtBrowser = (Browser) browser.get(viewer);

                swtBrowser.addLocationListener(new LocationListener() {
                    @Override
                    public void changed(LocationEvent event) {
                        System.out.println(event.location);
                    }

					@Override
					public void changing(LocationEvent event) {
						System.out.println("Changing location: " + event.location);
						
					}
                });
            } catch (Exception e) {
            }
        }
        else if (part instanceof WebBrowserView)
        {
            WebBrowserView view = (WebBrowserView) part;

            try {
                Field webBrowser = view.getClass().getDeclaredField("viewer");
//                Field webBrowser = editor.getClass().getDeclaredField("viewer");

                webBrowser.setAccessible(true);
                BrowserViewer viewer = (BrowserViewer)webBrowser.get(view);

                Field browser = viewer.getClass().getDeclaredField("browser");
                browser.setAccessible(true);
                Browser swtBrowser = (Browser) browser.get(viewer);

                swtBrowser.addLocationListener(new LocationListener() {
                    @Override
                    public void changed(LocationEvent event) {
                        System.out.println("Changed Location: " + event.location);
                    }

					@Override
					public void changing(LocationEvent event) {
						System.out.println("Changing location: " + event.location);
						
					}
                });
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
		
	}

}
