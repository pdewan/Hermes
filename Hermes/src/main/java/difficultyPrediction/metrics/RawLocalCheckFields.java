package difficultyPrediction.metrics;

import java.util.Date;

/*
 * #,Time,%Passes,Change,Test,Pass,Partial,Fail,Untested,SessionNumber,SessionRunNumber,IsSuite,SuiteTests,PrerequisiteTests,ExtraCreditTests,
0,Fri Jan 22 10:11:38 EST 2021,7,7,S21Assignment2Suite,A2AccessModifiersMatched+ A2ExpectedSetters+ A2ExpectedSignatures+ ,A2ExpectedCalls+ A2ExpectedGetters+ A2InterfaceAsType+ A2MnemonicNames+ A2NamedConstants+ A2PublicMethodsOverride+ ,A2ConfigurationProvided+ A2NoCheckstyleWarnings+ Barrier+ BarrierClass+ EarlyJoin+ JoinerClass+ LateJoin+ MultiThreadSumBoundedBuffer+ MultiThreadSumBulkPartialReduce+ MultiThreadSumMultiplePartialReduce+ MultiThreadSumMultipleRoundSynchronization+ MultiThreadSumResult+ MultiThreadSumThreads+ MultiThreadTokenCountResult+ PartitionerC
 */
public class RawLocalCheckFields {
	public int number;
	public Date time;
	public int percentPasses;
	public int change;
	public String test;
	public String[] pass;
	public String[] partial;
	public String[] fail;
	public String[] untested;
	public int sessionNumber;
	public int sessionRunNumber;
	public boolean isSuite;
	public String[] suiteTests;
	public String[] preReqTests;
	public String[] extraCreditTests;
	public RawLocalCheckFields(String aCSVRow) {
		try {
		String[] aRowElements = aCSVRow.split(",");
		number = Integer.parseInt(aRowElements[0]);
		time = new Date(aRowElements[1]);
		percentPasses = Integer.parseInt(aRowElements[2]);
		change = Integer.parseInt(aRowElements[3]);
		test = aRowElements[4];
		String[] pass = aRowElements[5].split(" ");
		String[] partial = aRowElements[6].split(" ");
		String[] fail = aRowElements[7].split(" ");
		String[] untested = aRowElements[8].split(" ");
		sessionNumber = Integer.parseInt(aRowElements[9]);
		isSuite = Boolean.parseBoolean(aRowElements[10]);
		String[] suiteTests = aRowElements[11].split(" ");
		String[] preReqTests = aRowElements[12].split(" ");
		String[] extraCreditTests = aRowElements[13].split(" ");
		


		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	

	
	
	

}
