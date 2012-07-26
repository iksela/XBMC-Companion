package net.iksela.xbmc.companion;

/**
 * Simple Enum allowing to reference sections in a SectionPager.
 * @author Alexandre "iksela" Mathieu
 *
 */
public enum Page {
	FIRST	(0, R.layout.page1, R.string.title_section1),
	SECOND	(1, R.layout.page2, R.string.title_section2),
	THIRD	(2, R.layout.page3, R.string.title_section3);
	
	private final int index;
	private final int layoutID;
	private final int titleID;
	
	Page (int index, int layoutID, int titleID) {
		this.index = index;
		this.layoutID = layoutID;
		this.titleID = titleID;
	}
	
	/**
	 * Retrieves the corresponding page.
	 * @param index
	 * @return
	 */
	public static Page getByIndex(int index) {
		Page[] pages = Page.values();
		for (Page current : pages) {
			if (current.getIndex() == index) {
				return current;
			}
		}
		return null;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getLayoutID() {
		return this.layoutID;
	}
	
	public int getTitleID() {
		return this.titleID;
	}
}
