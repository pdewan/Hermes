package analyzer.extension.replayView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.extension.ADifficultyPredictionAndStatusPrinter;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import fluorite.commands.CompilationEventCommand;
import fluorite.commands.Delete;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.Insert;
import fluorite.commands.MoveCaretCommand;
import fluorite.commands.Replace;

public class DistillerReplayer extends ADifficultyPredictionAndStatusPrinter{

	public static final String PROJECT = "E:\\Test\\524A6";
	public static final String SEPARATOR = File.separator;
	private Analyzer analyzer;
	List<List<EHICommand>> nestedCommands;
	private String currentFileName;
	private int i, j = 0;
	private int currentSrc = 0;
	private String fileContent;
	static final long DAY = 24 * 3600 * 1000; 
	static FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);

	public static void main(String[] args) {
//		printChangesBetweenCompile();
		printChangesBetweenSave();
//		printChangesByTime();
	}
	
	public static void printChangesByTime() {
		Analyzer analyzer = new AnAnalyzer();
		DistillerReplayer replayer = new DistillerReplayer(analyzer);
		analyzer.addAnalyzerListener(replayer);
		File[] src = replayer.initProject(PROJECT);
		long startTime = replayer.nestedCommands.get(0).get(1).getStartTimestamp();
		long endTime = startTime + DAY;
		for (int i = 0; i < 10; i++, startTime += DAY, endTime += DAY) {
			System.out.println("DAY " + (i+1));
			Map<String, List<SourceCodeChange>> changes = replayer.findChanges(startTime, endTime, src);
			if (changes == null) return;
			for (String key : changes.keySet()) {
				if (changes.get(key).isEmpty()) {
					continue;
				}
				System.out.println(key);
				for (SourceCodeChange change : changes.get(key)) {
					System.out.println(change.getClass().getSimpleName() + "change:" + change.getChangedEntity() + " " + change.getChangeType());
				}
				System.out.println();
			}
		}
		System.exit(0);
	}
	
	public static void printChangesBetweenCompile() {
		Analyzer analyzer = new AnAnalyzer();
		DistillerReplayer replayer = new DistillerReplayer(analyzer);
		analyzer.addAnalyzerListener(replayer);
		int count = 0;
		for (Map<String, List<SourceCodeChange>> changes: replayer.findChangesBetweenCompile(PROJECT, 0, Long.MAX_VALUE)) {
			if (changes == null) return;
			System.out.println("Copmile " + count++ + ":" + System.lineSeparator());
			for (String key : changes.keySet()) {
				if (changes.get(key).isEmpty()) {
					continue;
				}
				System.out.println(key);
				for (SourceCodeChange change : changes.get(key)) {
					System.out.println(change.getClass().getSimpleName() + "change:" + change.getChangedEntity() + " " + change.getChangeType());
				}
				System.out.println();
			}
		}
		System.exit(0);
	}
	
	public static void printChangesBetweenSave() {
		Analyzer analyzer = new AnAnalyzer();
		DistillerReplayer replayer = new DistillerReplayer(analyzer);
		analyzer.addAnalyzerListener(replayer);
		int count = 0;
		for (Map<String, List<SourceCodeChange>> changes: replayer.findChangesBetweenSave(PROJECT, 0, Long.MAX_VALUE)) {
			if (changes == null) return;
			System.out.println("Save " + count++ + ":" + System.lineSeparator());
			for (String key : changes.keySet()) {
				if (changes.get(key).isEmpty()) {
					continue;
				}
				System.out.println(key);
				for (SourceCodeChange change : changes.get(key)) {
					System.out.println(change.getClass().getSimpleName() + "change:" + change.getChangedEntity() + " " + change.getChangeType());
//					System.out.println(change.toString());
				}
				System.out.println();
			}
		}
		System.exit(0);
	}
	
	public List<Map<String, List<SourceCodeChange>>> findChangesBetweenSave(String project, long startTime, long endTime) {
		File[] src = initProject(PROJECT);
		replayTo(src[currentSrc], startTime);
		List<Map<String, List<SourceCodeChange>>> changesBetweenCompile = new ArrayList<>();
		long currentTime = 0;
		for (int i = 0; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			for (int j = 0; j < commands.size(); j++) {
				EHICommand command = commands.get(j);
				if (command instanceof EclipseCommand && ((EclipseCommand) command).getCommandID().equals("org.eclipse.ui.file.save")) {
					try {
						currentTime = command.getStartTimestamp()+command.getTimestamp();
						File src1 = src[currentSrc % 2];
						File src2 = src[(currentSrc + 1) % 2];
						if (src2.exists()) {
							FileUtility.deleteFolder(src2);
						}
						FileUtility.copyDirectory(src1.getPath(), src2.getPath());
						replayTo(src[(currentSrc + 1) % 2], currentTime);
						changesBetweenCompile.add(findProjectChanges(src1, src2));
					} catch (IOException e) {
						e.printStackTrace();
					}
					currentSrc++;
				}
			}
		}
		return changesBetweenCompile;
	}
	
	public List<Map<String, List<SourceCodeChange>>> findChangesBetweenCompile(String project, long startTime, long endTime) {
		File[] src = initProject(PROJECT);
		replayTo(src[currentSrc], startTime);
		List<Map<String, List<SourceCodeChange>>> changesBetweenCompile = new ArrayList<>();
		long currentTime = 0;
		for (int i = 0; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			for (int j = 0; j < commands.size(); j++) {
				EHICommand command = commands.get(j);
				if (command instanceof CompilationEventCommand && command.getDataMap().get("success").equals("true")) {
					try {
						currentTime = command.getStartTimestamp()+command.getTimestamp();
						File src1 = src[currentSrc % 2];
						File src2 = src[(currentSrc + 1) % 2];
						if (src2.exists()) {
							FileUtility.deleteFolder(src2);
						}
						FileUtility.copyDirectory(src1.getPath(), src2.getPath());
						replayTo(src[(currentSrc + 1) % 2], currentTime);
						changesBetweenCompile.add(findProjectChanges(src1, src2));
					} catch (IOException e) {
						e.printStackTrace();
					}
					currentSrc++;
				}
			}
		}
		return changesBetweenCompile;
	}

	public File[] initProject(String projectPath) {
		File project = new File(projectPath);
		File replaySrc0 = new File(project, "replaySrc0");
		File replaySrc1 = new File(project, "replaySrc1");
		File[] src = { replaySrc0, replaySrc1 };
		if (replaySrc0.exists()) {
			FileUtility.deleteFolder(replaySrc0);
		}
		if (replaySrc1.exists()) {
			FileUtility.deleteFolder(replaySrc1);
		}
		replaySrc0.mkdir();
		replaySrc1.mkdir();
		nestedCommands = ReplayUtility.replayLogs(projectPath, analyzer);
		i = 0;
		j = 0;
		currentFileName = findFirstFilePath();
		return src;
	}
	
	public String findFirstFilePath() {
		int[] idx = ReplayUtility.findFirstFile(nestedCommands);
		i = idx[0];
		j = idx[1];
		EHICommand command = nestedCommands.get(i).get(j);
		if (command instanceof FileOpenCommand) {
			j++;
			fileContent = command.getDataMap().get("snapshot");
			return ReplayUtility.getRelativeFilePath(command);
		}
		return "";
	}
	
	public Map<String, List<SourceCodeChange>> findChanges(long startTime, long endTime, File[] src) {
		if (currentSrc == 0) {
			replayTo(src[0], startTime);
		}
		try {
			File src1 = src[currentSrc % 2];
			File src2 = src[(currentSrc + 1) % 2];
			if (src2.exists()) {
				FileUtility.deleteFolder(src2);
			}
			FileUtility.copyDirectory(src1.getPath(), src2.getPath());
			replayTo(src2, endTime);
			currentSrc++;
			return findProjectChanges(src1, src2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, List<SourceCodeChange>> findProjectChanges(File src1, File src2) {
		Map<String, List<SourceCodeChange>> allChanges = new HashMap<>();
		try {
			List<Path> src1Files = FileUtility.listFiles(src1.toPath());
			List<Path> src2Files = FileUtility.listFiles(src2.toPath());
			HashMap<String, Path> src1Map = new HashMap<String, Path>();
			for (Path path : src1Files) {
				if (!path.toFile().isDirectory())
					src1Map.put(path.getFileName().toString(), path);
			}
			for (Path path : src2Files) {
				if (!path.toString().endsWith(".java")) {
					continue;
				}
				if (!path.toFile().isDirectory()) {
					Path path1 = src1Map.get(path.getFileName().toString());
					if (path1 != null) {
						List<SourceCodeChange> changes = findFileChanges(path1.toFile(), path.toFile());
						if (changes != null) {
							List<SourceCodeChange> oldChanges = allChanges.get(path.getFileName().toString());
							if (oldChanges != null) {
								oldChanges.addAll(changes);
							} else {
								allChanges.put(path.getFileName().toString(), changes);
							}
						}
					} else {
						File temp = new File(src1, "temp.java");
						if (temp.exists())
							temp.delete();
						temp.createNewFile();
						try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
							String name = path.getFileName().toString();
							bw.write("public class " + name.substring(0, name.lastIndexOf(".java")) + " {}");
						} catch (IOException e) {
							e.printStackTrace();
						}
						List<SourceCodeChange> changes = findFileChanges(temp, path.toFile());
						if (changes != null) {
							List<SourceCodeChange> oldChanges = allChanges.get(path.getFileName().toString());
							if (oldChanges != null) {
								oldChanges.addAll(changes);
							} else {
								allChanges.put(path.getFileName().toString(), changes);
							}
						}
						temp.delete();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allChanges;
	}

	public List<SourceCodeChange> findFileChanges(File file1, File file2) {
		try {
			distiller.extractClassifiedSourceCodeChanges(file1, file2);
			List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
			return changes;
		} catch (Exception e) {
			System.err.println("Warning: error while change distilling. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void replayTo(File src, long endTime) {
		FileEditor editor;
		if (fileContent == null) {
			editor = FileEditor.getEditor(src, currentFileName);
		} else {
			editor = FileEditor.getEditor(src, currentFileName, fileContent);
			fileContent = null;
		}
		for (; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			for (; j < commands.size(); j++) {
				EHICommand command = commands.get(j);
				long timestamp = command.getStartTimestamp() + command.getTimestamp();
				if (timestamp > endTime) {
					return;
				}
				if (command instanceof FileOpenCommand && !ReplayUtility.isNull(command.getDataMap().get("filePath"))) {
					if (editor != null) {
						editor.writeToDisk();
					}
					currentFileName = ReplayUtility.getRelativeFilePath(command);
					String snapshot = ReplayUtility.getSnapshot(command);
					if (!ReplayUtility.isNull(snapshot)) {
						editor = FileEditor.getEditor(src, currentFileName, snapshot);
					} else {
						editor = FileEditor.getEditor(src, currentFileName);
					}
				}
				if (command instanceof Insert) {
					int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
					String text = command.getDataMap().get("text");
					editor.insert(offset, text);
				}
				if (command instanceof Delete) {
					int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
					String text  = command.getDataMap().get("text");
					editor.delete(offset, text);
				}
				if (command instanceof Replace) {
					int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
					editor.replace(offset, command.getDataMap().get("deletedText"), command.getDataMap().get("insertedText"));
				}
				if (command instanceof MoveCaretCommand) {
					editor.moveCursor(Integer.parseInt(command.getAttributesMap().get("caretOffset")));
				}
			}
			j = 0;
		}
	}

	public DistillerReplayer(Analyzer anAnalyzer) {
		super(anAnalyzer);
		analyzer = anAnalyzer;
	}
}
