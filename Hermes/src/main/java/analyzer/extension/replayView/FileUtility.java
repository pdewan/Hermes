package analyzer.extension.replayView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import fluorite.commands.EHICommand;

public class FileUtility {
	public static void main(String[] args) {
		try {
			decompress(compress("aaaaaaaaaaaaaaaaaaaaaa"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static File copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException 
	{
		Files.walk(Paths.get(sourceDirectoryLocation))
		.forEach(source -> {
			Path destination = Paths.get(destinationDirectoryLocation, source.toString()
					.substring(sourceDirectoryLocation.length()));
			try {
				Files.copy(source, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return new File(destinationDirectoryLocation);
	}
	
	public static JSONObject readJSON(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			reader.close();
			return new JSONObject(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteFolder(File folder) {
		if (folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();
	}
	
	public static List<Path> listFiles(Path path) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }
	 
	public static StringBuilder readFile(String path) {
		return readFile(new File(path));
	}
	
	public static StringBuilder readFile(File file) {
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String nextLine = "";
			while((nextLine = br.readLine()) != null) {
				content.append(nextLine+System.lineSeparator());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	public static void unzip(String sourceString, String targetString) throws IOException {
		Path source = Paths.get(sourceString);
		Path target = Paths.get(targetString);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }
                Path newPath = zipSlipProtect(zipEntry, target);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    // copy files, nio
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
        throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
    
    public static void zip(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
              .filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                  ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                  try {
                      zs.putNextEntry(zipEntry);
                      Files.copy(path, zs);
                      zs.closeEntry();
                } catch (IOException e) {
                    System.err.println(e);
                }
              });
        }
    }
    
	public static boolean unzip(File submission) {
		File[] files = submission.listFiles((file)->{return file.getName().endsWith(".zip");});
		if (files.length == 0) {
			return false;
		}
		try {
			unzip2(files[0].getPath(), submission.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void unzip2(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName().replace("/", "\\");
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}
	
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		new File(filePath).getParentFile().mkdirs();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static String compress(String input) throws UnsupportedEncodingException {
		Deflater compresser = new Deflater();
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		byte[] outputBytes = new byte[inputBytes.length];
		compresser.setInput(inputBytes);
		compresser.finish();
		int compressedSize = compresser.deflate(outputBytes);
		String result = Base64.getEncoder().encodeToString(Arrays.copyOfRange(outputBytes, 0, compressedSize));
//		String result = new String(outputBytes, 0, compressedSize, StandardCharsets.UTF_8);
		System.out.println(result);
		return result;
	}
	
	public static String decompress(String input) throws UnsupportedEncodingException, DataFormatException {
		Inflater decompresser = new Inflater();
		byte[] inputBytes = Base64.getDecoder().decode(input);
//		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		byte[] outputBytes = new byte[inputBytes.length * 10];
		decompresser.setInput(inputBytes);
		int decompressedSize = decompresser.inflate(outputBytes);
		String result = new String(outputBytes, 0, decompressedSize, StandardCharsets.UTF_8);
		System.out.println(result);
		return result;
	}
	public static File[] listFolders(File aParent) {
		File[] aFolders = aParent.listFiles(
				new FileFilter() {
            public boolean accept(File file) {
             return file.isDirectory();
            }});
//		if (aFolders.length == 0) {
//			return null;
//		}
		return aFolders;
	}
	
	public static File findFirstBFDescendantMatching (File aRoot, String aRegularExpression) {
		if (aRoot.isFile()) {
			return null;
		}
		File[] aFiles = aRoot.listFiles(new FilenameFilter() {
                  public boolean accept(File dir, String name) {
                   return name.matches(aRegularExpression);
                  }});
		if (aFiles.length == 0) {
			File[] aFolders = listFolders(aRoot);
			if (aFolders == null || aFolders.length == 0) {
				return null;
			}
			for (File aFolder:aFolders) {
				File retVal = findFirstBFDescendantMatching(aFolder, aRegularExpression);
				if (retVal != null) {
					return retVal;
				}
			}
			return null;
		}
		return aFiles[0];
	}
	public static File findLogsFolder (String aStudent) {
		File aStudentFile = new File(aStudent);
		return findFirstBFDescendantMatching(aStudentFile, "Logs");
	}
	public static File getCheckStyleLogFile (String aStudent) {
		File aLogsFolder = findLogsFolder(aStudent);
		if (aLogsFolder == null) {
			return null;
		}
		File retVal = new File(aLogsFolder, "LocalChecks/CheckStyle_All.csv");
		if (!retVal.exists())
			return null;
		return retVal;
	}
	public static File[] getLocalChecksRawsLogFiles (String aStudent) {
		File aLogsFolder = findLogsFolder(aStudent);
		if (aLogsFolder == null) {
			return null;
		}
		return getLocalChecksRawsLogFiles(aLogsFolder);
		
	}
	public static File[] getLocalChecksRawsLogFiles (File aLogsFolder) {
		File aLocalChecksFolder = new File (aLogsFolder, "LocalChecks");
		if (!aLocalChecksFolder.exists()) {
			return null;
		}
		File[] aFiles = aLocalChecksFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
             return name.endsWith("FineGrained.csv");
            }});
		if (aFiles.length == 0) {
			return null;
		}
		return aFiles;
		
	}
	public static String toCSVString (String[] aStrings) {
		if (aStrings.length == 0) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(aStrings[0]);
		for (int anArrayIndex = 1; anArrayIndex < aStrings.length; anArrayIndex++) {
			stringBuilder.append(',');
			stringBuilder.append(aStrings[anArrayIndex]);
		}
		return stringBuilder.toString();
	}
	 static List<String> allEvents = new ArrayList();

	public static void getRecentEvents(File aFile, 
			List<String> aRecentEvents, 
			List<String[]> aRecentTuples,
			List <Long> aRecentTimes,
			long aLastReadTime,
			int aDateColumn,
			SimpleDateFormat aDateFormat) {
//		checkStyleFile = getCheckStyleFile();
		if (aFile == null || !aFile.exists()) {
			return;
		}
		long aLastModifed = aFile.lastModified();
		if (aLastReadTime >=  aLastModifed   ) {
			return; 
		}
		allEvents.clear();
		readLines(aFile, allEvents);
		for (int i = allEvents.size() -1 ; i >= 0 ; i--) {
			String anEventRow = allEvents.get(i);
			

			String[] anEvents = anEventRow.split(",");
			if (i == 0 && anEvents[aDateColumn].contains("ime")) {
				continue; //header
			}
			try {
				long aTime = aDateFormat.parse(anEvents[aDateColumn]).getTime();
				if (aLastReadTime < aTime) {
					aRecentEvents.add(0, anEventRow);
					aRecentTuples.add(0, anEvents);
					aRecentTimes.add(0, aTime);
					
				} else {
					break; // previous events occur earlier
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Could not parse  entry " + i + " content:" + anEvents[aDateColumn]  );
				e.printStackTrace();
//				aRecentTimes.add(0, null);

				
			}
//			recentEvents.add(anAllEvents.get(i));
		}

	}
	public static void readCheckstyleEvents(String aStudentProject, List<String> retVal) {
//		String aCheckStyleAllFileName = aStudent + "/Submission attachment(s)/Logs/LocalChecks/CheckStyle_All.csv";
//		File aFile = new File(aCheckStyleAllFileName);
		File aCheckStyleFile = FileUtility.getCheckStyleLogFile(aStudentProject);
		 readLines(aCheckStyleFile, retVal);
		
	}
//	static List<String> allEvents = new ArrayList();

	public static void readLines(File aFile, List<String> anAllEvents) {
//		List<String> retVal = new ArrayList();

		if (aFile == null || !aFile.exists()) {
			return ;
		}

			try {
				BufferedReader br = new BufferedReader(new FileReader(aFile));
			    String line = null;
			    while ((line = br.readLine()) != null) {
			    	anAllEvents.add(line);
			    }
			    br.close();
				return ;
			}
			
				
				
			

		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public static final String XML_FILE_ENDING = "\r\n</Events>"; 

	public static void updateLogFile(File logFile, List<EHICommand> commands) {
		String firstLine = "";
		try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
			firstLine = br.readLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (firstLine.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder(firstLine);
		sb.append("\r\n");
		for (EHICommand command : commands) {
			sb.append(command.persist());
		}
		sb.append(XML_FILE_ENDING);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile))) {
			bw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
