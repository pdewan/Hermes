package hermes.proxy;

import java.util.LinkedList;
import name.fraser.neil.plaintext.diff_match_patch;

public class Diff_Match_Patch_Proxy {
	public static LinkedList<diff_match_patch.Diff> diff(String oldFile, String currentFile){
		diff_match_patch differ = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diff = differ.diff_main(oldFile, currentFile);
		differ.diff_cleanupSemantic(diff);
		return diff;
//		LinkedList<diff_match_patch.Patch> patchs = differ.patch_make(currentFile, diff);
//		Object[] aRetVal = differ.patch_apply(patchs, currentFile);
	}
	
	public static String diffString(String oldFile, String currentFile){
		diff_match_patch differ = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diff = differ.diff_main(oldFile, currentFile);
		differ.diff_cleanupSemantic(diff);
		return diff.toString();
	}
	
	public static String diff_patch(String oldFile, String currentFile){
		diff_match_patch differ = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diff = differ.diff_main(oldFile, currentFile);
		differ.diff_cleanupSemantic(diff);
		LinkedList<diff_match_patch.Patch> patchs = differ.patch_make(currentFile, diff);
		Object[] aRetVal = differ.patch_apply(patchs, currentFile);
		return aRetVal[0].toString();
	}
}
