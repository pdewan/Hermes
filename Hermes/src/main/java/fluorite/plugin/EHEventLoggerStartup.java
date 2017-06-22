package fluorite.plugin;

import java.lang.reflect.Field;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserView;

import util.trace.difficultyPrediction.DifficultyPredictionTraceUtility;
import util.trace.hermes.helpbutton.HelpPluginTraceUtility;
import util.trace.hermes.timetracker.TimeTrackerTraceUtility;
import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;
//import util.trace.messagebus.clients.MessageBusClientsTraceUtility;
import util.trace.plugin.PluginEarlyStarted;
import util.trace.workbench.PartActivated;
import util.trace.workbench.PartOpened;

public class EHEventLoggerStartup implements IStartup {
	public void traceOtherPlugins() {
//		HelpPluginTraceUtility.setTracing();
//		TimeTrackerTraceUtility.setTracing();
//		WorkspaceListenerTraceUtility.setTracing();
//		MessageBusClientsTraceUtility.setTracing();
	}
	public void earlyStartup() {
//		System.out.println(" Early Startup");
		DifficultyPredictionTraceUtility.setTracing();
		
		PluginEarlyStarted.newCase(this);
//		final IPartListener partListener = new IPartListener() {
//		    @Override
//		    public void partOpened(IWorkbenchPart part) {
//		    	PartOpened.newCase(part, this);
//		        if (part instanceof WebBrowserEditor)
//		        {
//		            WebBrowserEditor editor = (WebBrowserEditor) part;
//
//		            try {
//		                Field webBrowser = editor.getClass().getDeclaredField("webBrowser");
//		                webBrowser.setAccessible(true);
//		                BrowserViewer viewer = (BrowserViewer)webBrowser.get(editor);
//
//		                Field browser = viewer.getClass().getDeclaredField("browser");
//		                browser.setAccessible(true);
//		                Browser swtBrowser = (Browser) browser.get(viewer);
//
//		                swtBrowser.addLocationListener(new LocationListener() {
//		                    @Override
//		                    public void changed(LocationEvent event) {
//		                        System.out.println(event.location);
//		                    }
//
//							@Override
//							public void changing(LocationEvent event) {
//								System.out.println("Changing location: " + event.location);
//								
//							}
//		                });
//		            } catch (Exception e) {
//		            }
//		        }
//		        else if (part instanceof WebBrowserView)
//		        {
//		            WebBrowserView view = (WebBrowserView) part;
//
//		            try {
//		                Field webBrowser = view.getClass().getDeclaredField("viewer");
////		                Field webBrowser = editor.getClass().getDeclaredField("viewer");
//
//		                webBrowser.setAccessible(true);
//		                BrowserViewer viewer = (BrowserViewer)webBrowser.get(view);
//
//		                Field browser = viewer.getClass().getDeclaredField("browser");
//		                browser.setAccessible(true);
//		                Browser swtBrowser = (Browser) browser.get(viewer);
//
//		                swtBrowser.addLocationListener(new LocationListener() {
//		                    @Override
//		                    public void changed(LocationEvent event) {
//		                        System.out.println("Changed Location: " + event.location);
//		                    }
//
//							@Override
//							public void changing(LocationEvent event) {
//								System.out.println("Changing location: " + event.location);
//								
//							}
//		                });
//		            } catch (Exception e) {
//		            	e.printStackTrace();
//		            }
//		        }
//		    }
////		    ...
//
//			@Override
//			public void partActivated(IWorkbenchPart part) {
//				PartActivated.newCase(part, this);
////				System.out.println("part activated");
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void partBroughtToTop(IWorkbenchPart part) {
////				System.out.println("part on top");
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void partClosed(IWorkbenchPart part) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void partDeactivated(IWorkbenchPart part) {
//				// TODO Auto-generated method stub
//				
//			}
//		};

//		final IPageListener pageListener = new IPageListener() {
//		    @Override
//		    public void pageOpened(IWorkbenchPage page) {
//		    	System.out.println("Page opened" + page.getLabel());
//		        page.addPartListener(partListener);
//		    }
////		    ...
//
//			@Override
//			public void pageActivated(IWorkbenchPage page) {
//		    	System.out.println("Page activated" + page.getLabel());
//
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void pageClosed(IWorkbenchPage page) {
//				System.out.println("Part closed");
//				
//			}
//		};
//
//		final IWindowListener windowListener = new IWindowListener() {
//		    @Override
//		    public void windowOpened(IWorkbenchWindow window) {
//		    	System.out.println(" Window opened, adding page listener");
//		        window.addPageListener(pageListener);
//		    }
////		    ...
//
//			@Override
//			public void windowActivated(IWorkbenchWindow window) {
//		    	System.out.println(" window activated");
////		        window.addPageListener(pageListener);
//
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void windowDeactivated(IWorkbenchWindow window) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void windowClosed(IWorkbenchWindow window) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//
//
//		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//
//		if (activeWindow != null)
//		{
//		    IWorkbenchPage activePage = activeWindow.getActivePage();
//
//		    if (activePage != null)
//		    {
//		    	System.out.println("active page, addin part listener");
//		        activePage.addPartListener(partListener);
//		    }
//		    else
//		    {
//		    	System.out.println("adding page listener");
//
//		        activeWindow.addPageListener(pageListener);
//		    }
//		}
//		else
//		{
//		    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
//		    {
//		        for (IWorkbenchPage page : window.getPages()) {
//			    	System.out.println("startup page, addin part listener");
//
//		            page.addPartListener(partListener);
//		        }
//		        window.addPageListener(pageListener);
//		    }
//	    	System.out.println(" startup page, addin platform window listener");
//
//
//		    PlatformUI.getWorkbench().addWindowListener(windowListener);
//		}       
//		// TODO Auto-generated method stub
	}
}
