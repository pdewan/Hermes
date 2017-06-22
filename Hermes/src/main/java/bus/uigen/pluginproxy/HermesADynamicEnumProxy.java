package bus.uigen.pluginproxy;

import java.util.List;

import util.models.ADynamicEnum;

public class HermesADynamicEnumProxy<ElementType>
	extends ADynamicEnum<ElementType> 
	implements HermesDynamicEnumProxy<ElementType>{
	public HermesADynamicEnumProxy(List<ElementType> theInitialChoices) {
		super(theInitialChoices);
	}
	public HermesADynamicEnumProxy() {
		super();
	}

}
