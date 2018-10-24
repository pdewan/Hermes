/**
 * @author Nils Persson
 * @date 2018-Mar-29 8:54:43 PM 
 */
package remoteMessaging;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Calendar;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.HermesActivator;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import fluorite.commands.EHICommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EHXMLFormatter;
import hermes.tags.Tags;
import remoteModuleSelection.MediatorModule;
import remoteModuleSelection.SendingModuleFactory;
import util.trace.recorder.LogFileCreated;
import util.trace.recorder.LogHandlerBound;

/**
 * 
 */
public class ARemoteMessageSender implements RemoteMessageSender, RemoteJSON{
	protected static ARemoteMessageSender instance;
	private Logger logger;
	private File outputFile;
	
	public static ARemoteMessageSender getInstance(){
		if(instance == null) {
			return init();
		} else {
			return instance;
		}
	}
	
	public static ARemoteMessageSender init(){
		instance = new ARemoteMessageSender();
		return instance;
	}
	
	public void sendMessage(Object object){
		// create the JSON object to send
		JSONObject messageData = new JSONObject();
		switch(SendingModuleFactory.getSingleton().getModule()){
			case EVENT_AGGREGATOR:
				AnEventAggregatorDetails details = (AnEventAggregatorDetails)object;
				logEvents(details);
				messageData = createJSON(fileToString(), EVENT_AGGREGATOR);
				break;
			case FEATURE_EXTRACTOR:
				break;
			case PREDICTION_MANAGER:
				break;
			case STATUS_AGGREGATOR:
				break;
		}
		
		// make sure connection manager exists AND the JSON object has been filled
		if(ConnectionManager.getInstance() != null && messageData.length() != 0){ 
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}
	
	private JSONObject createJSON(String data, String module){
		JSONObject messageData = new JSONObject();	
		try {
			messageData.put(MODULE, module);
			messageData.put(DATA, data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Tags.putTags(messageData, Tags.DIFFICULTY_PREDICTION);
		return messageData;
	}
	
	private void logEvents(AnEventAggregatorDetails details){
		// setup the logger
		long mStartTimestamp = Calendar.getInstance().getTime().getTime();
		
		logger = Logger.getLogger(ARemoteMessageSender.class.getName());
		logger.setLevel(Level.FINE);
		
		try {
			outputFile = File.createTempFile("hermes-temp-file", "");
			outputFile.deleteOnExit();
			LogFileCreated.newCase(outputFile.getName(), this);
			
			FileHandler handler = new FileHandler(outputFile.getPath());
			handler.setEncoding("UTF-8");
			EHXMLFormatter formatter = new EHXMLFormatter(mStartTimestamp);
			handler.setFormatter(formatter);
			logger.addHandler(handler);
			LogHandlerBound.newCase(handler, this);
			
			// log the details
			for(EHICommand command : details.actions){
				logger.log(Level.FINE, null, command);
			}
			
			// add last line of logger usually handled by shutdown hook
			handler.flush();
			handler.close();
			
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private String fileToString(){
		try {
			byte[] bytes = Files.readAllBytes(outputFile.toPath());
			return new String(Base64.getEncoder().encode(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}   
		return null;
	}
	
	private void delLogger(){
		
	}
}
