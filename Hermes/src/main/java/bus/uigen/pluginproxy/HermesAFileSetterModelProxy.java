package bus.uigen.pluginproxy;

import bus.uigen.models.AFileSetterModel;
import bus.uigen.models.FileSetterModel;

public class HermesAFileSetterModelProxy extends AFileSetterModel implements HermesFileSetterModelProxy{

	public HermesAFileSetterModelProxy(int aFilterOption) {
		super(aFilterOption);
	}

}
