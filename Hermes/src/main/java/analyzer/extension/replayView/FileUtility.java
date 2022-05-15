package analyzer.extension.replayView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
}
