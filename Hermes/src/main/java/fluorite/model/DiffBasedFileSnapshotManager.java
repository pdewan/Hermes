package fluorite.model;

import java.util.HashMap;
import java.util.Map;

public class DiffBasedFileSnapshotManager{

	private static DiffBasedFileSnapshotManager instance = null;

	public static DiffBasedFileSnapshotManager getInstance() {
		if (instance == null) {
			instance = new DiffBasedFileSnapshotManager();
		}

		return instance;
	}

	private Map<String, String> mSnapshotMap;

	private DiffBasedFileSnapshotManager() {
		mSnapshotMap = new HashMap<String, String>();
	}

	public boolean isSame(String fullPath, String currentContent) {
		if (fullPath == null) {
			return false;
		}

		if (!mSnapshotMap.containsKey(fullPath)) {
			return false;
		}

		return mSnapshotMap.get(fullPath).equals(currentContent);
	}

	public void updateSnapshot(String fullPath, String currentContent) {
		if (fullPath == null) {
			return;
		}

		mSnapshotMap.put(fullPath, currentContent);
	}
	
	public String getSnapshot(String fullpath){
		return mSnapshotMap.get(fullpath);
	}
}
