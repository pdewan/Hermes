package difficultyPrediction;

public class RobotInformation {
	
	private DifficultyRobot robot;
	private String robotId;
	private String clientId;
	private String clientUserName;
	
	public DifficultyRobot getRobot() {
		return robot;
	}

	public String getRobotId() {
		return robotId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setRobot(DifficultyRobot robot) {
		this.robot = robot;
	}

	public void setRobotId(String robotId) {
		this.robotId = robotId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

	public RobotInformation()
     {
         this.robot = null;
         this.robotId = "";
         this.clientId = "";
         this.clientUserName = "";
     }

     public RobotInformation(DifficultyRobot Robot, String RobotId, String ClientId, String ClientUserName)
     {
         this.robot = Robot;
         this.robotId = RobotId;
         this.clientId = ClientId;
         this.clientUserName = ClientUserName;
     }

    
 }

