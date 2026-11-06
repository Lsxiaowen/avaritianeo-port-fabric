package net.byAqua3.avaritia.compat;

/**
 * Optional integrations. The class name is kept as a string because the JEI
 * plugin lives in the client source set, which main cannot reference.
 */
public enum AvaritiaCompat {

	JEI("jei", "net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin");

	private final String modId;
	private final String className;

	private AvaritiaCompat(String modId, String className) {
		this.modId = modId;
		this.className = className;
	}

	public String getModId() {
		return this.modId;
	}

	public ICompatInit getModInit() {
		try {
			Object instance = Class.forName(this.className).getDeclaredConstructor().newInstance();
			if (instance instanceof ICompatInit compat) {
				return compat;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
