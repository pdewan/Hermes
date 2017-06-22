package bus.uigen.pluginproxy;

import javax.swing.Icon;

import util.models.ALabelBeanModel;

public class HermesALabelBeanModelProxy extends ALabelBeanModel implements HermesLabelBeanModelProxy {
	public HermesALabelBeanModelProxy() {
		super();		
	}
	public HermesALabelBeanModelProxy(String aText) {
		super(aText);		
	}
	public HermesALabelBeanModelProxy(Icon anIcon) {
		super(anIcon);	
	}
	public HermesALabelBeanModelProxy(String aText, Icon anIcon) {
		super(aText, anIcon);
	}
}
