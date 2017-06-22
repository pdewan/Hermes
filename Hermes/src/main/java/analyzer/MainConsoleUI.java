package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import difficultyPrediction.Mediator;
import difficultyPrediction.eventAggregation.AnEventAggregator;
import fluorite.commands.EHICommand;
import fluorite.util.EHLogReader;

public class MainConsoleUI {

	public static Hashtable<String, String> participants = new Hashtable<String, String>();

//	public static final String PARTICIPANT_INFORMATION_DIRECTORY = "//Users//jasoncarter//Documents//Barrier_User_Study_Logs//";
	public static final String PARTICIPANT_INFORMATION_DIRECTORY = "data/ExperimentalData/";

	public static final String PARTICIPANT_INFORMATION_FILE = "Participant_Info.csv";
	public static final String DEFAULT_NUMBER_OF_SEGMENTS = "50";
	public static final String ALL_PARTICIPANTS = "All";
	public static EHLogReader reader;

	public static void main(String[] args) {
		// read in information from file
		reader = new EHLogReader();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					PARTICIPANT_INFORMATION_DIRECTORY
							+ PARTICIPANT_INFORMATION_FILE));
			String word = null;
			while ((word = br.readLine()) != null) {
				String[] userInfo = word.split(",");
				participants.put(userInfo[0], userInfo[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (participants.size() > 0) {
			System.out.println("User Ids:");
			Enumeration<String> participantIds = participants.keys();
			while (participantIds.hasMoreElements()) {
				System.out.print(participantIds.nextElement() + "\t");
			}

			System.out.println(ALL_PARTICIPANTS);
			System.out.println();
			System.out.println("Please choose a user id");

			String participantId;
			String numberOfSegments;
			List<List<EHICommand>> commandsList;
			Scanner scanIn = new Scanner(System.in);
			participantId = scanIn.nextLine();

			System.out
					.println("Please enter the number of segments to split the log by (default is 50)");
			numberOfSegments = scanIn.nextLine();
			
			if(participantId.equalsIgnoreCase(""))
				participantId = ALL_PARTICIPANTS;
			
			if(numberOfSegments.equalsIgnoreCase(""))
				numberOfSegments = DEFAULT_NUMBER_OF_SEGMENTS;
			
			//todo need to ask for discrete chunks or sliding window
			//may d for discrete and s for sliding window

			scanIn.close();

			System.out.println("Processing logs for: " + participantId);
			if (participantId.equals(ALL_PARTICIPANTS)) {
				while (participantIds.hasMoreElements()) {
					
					String aParticipantId = participantIds
							.nextElement();
					commandsList = convertXMLLogToObjects(aParticipantId);
					processCommands(PARTICIPANT_INFORMATION_DIRECTORY, commandsList, numberOfSegments,aParticipantId);
				}
			} else {
				commandsList = convertXMLLogToObjects(participantId);
				processCommands(PARTICIPANT_INFORMATION_DIRECTORY, commandsList, numberOfSegments, participantId);
			}

		} else {
			System.out.println("No users in file something went wrong :(.");
		}

	}

	public static void processCommands(String aFolder, List<List<EHICommand>> commandsList,
			String numberOfSegments, String participantId) {
		
		
		Mediator mediator = new AnalyzerMediator(aFolder, participantId);
		AnEventAggregator eventAggregator = new AnEventAggregator(mediator);
		eventAggregator.setEventAggregationStrategy(new DiscreteChunksAnalyzer(numberOfSegments));
		
		long startTimeStamp = 0;
		for (int index = 0; index < commandsList.size(); index++) {
			List<EHICommand> commands = commandsList.get(index);
			for (int i = 0; i < commands.size(); i++) {
				if ((commands.get(i).getTimestamp() == 0)
						&& (commands.get(i).getTimestamp2() > 0)) {
					// this is the starttimestamp
					startTimeStamp = commands.get(i).getTimestamp2();
				} else {
					eventAggregator.setStartTimeStamp(startTimeStamp);
					//((DiscreteChunksAnalyzer)eventAggregator.eventAggregationStrategy).setStartTimeStamp(startTimeStamp);
					
					
//					eventAggregator.eventAggregationStrategy.performAggregation(commands.get(i), eventAggregator);
					eventAggregator.getEventAggregationStrategy().performAggregation(commands.get(i), eventAggregator);

					
					// try to see if you can use the event aggreator to split
					// the events
					// then try to use the code you have to compute the metrics
					// (may need to reuse some of the code, but create new code)
					
					
					//what are the metrics
					//insertion ratio
					//deletion ratio
					//navigation ratio
					//debug ratio
					//exception ratio
					//# of seconds spent inserting text/total # of seconds (do this for all events)
					// think time - # of seconds where nothing happened/total # of seconds
				}

			}
		}
	}

	public static List<List<EHICommand>> convertXMLLogToObjects(
			String participantId) {
		List<List<EHICommand>> listOfListOFcommands = new Vector<List<EHICommand>>();
		String participantDirectory = PARTICIPANT_INFORMATION_DIRECTORY
				+ participantId + "/";
		File folder = new File(participantDirectory);
		List<String> participantFiles = getFilesForFolder(folder);
		System.out.println("Particpant " + participantId + " has "
				+ participantFiles.size() + " file(s)");
		System.out.println();
		for (int i = 0; i < participantFiles.size(); i++) {
			String aFileName = participantDirectory
					+ participantFiles.get(i);
//			List<ICommand> commands = reader.readAll(participantDirectory
//					+ participantFiles.get(i));
			System.out.println("Reading " + aFileName);
//			List<ICommand> commands;
			try {
			List<EHICommand> commands = reader.readAll(aFileName);
			listOfListOFcommands.add(commands);

			} catch (Exception e) {
				System.out.println("Could not read file" + aFileName + e);
				
			}
			
//			listOfListOFcommands.add(commands);
		}

		return listOfListOFcommands;
	}

	public static List<String> getFilesForFolder(final File folder) {
		List<String> files = new Vector<String>();
		for (final File fileEntry : folder.listFiles()) {
			files.add(fileEntry.getName());
		}
		return files;
	}

}
