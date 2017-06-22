package config;

public class HelperConfigurationManagerFactory {
	protected static HelperConfigurationManager singleton;

	public static HelperConfigurationManager getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

	public static void setSingleton(HelperConfigurationManager singleton) {
		HelperConfigurationManagerFactory.singleton = singleton;
	}
	
	public static void createSingleton() {
		singleton = new AHelperConfigurationManager();		
	}
	

}
