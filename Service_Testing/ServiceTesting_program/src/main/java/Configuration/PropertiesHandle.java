package Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Properties;
import util.common.DatabaseOperation;

public class PropertiesHandle extends Properties

{
	private static final long serialVersionUID = 1L;
	protected String path = null;
	protected String Project;
	protected String Api;
	protected String Env;
	protected String OutputChioce;
	protected String UserName;
	protected String Platform;

	static DatabaseOperation ConfigQuery = new DatabaseOperation();
			
	    public PropertiesHandle(String Project,String Api, String Env ,String OutputChioce, String Platform, String UserName) throws ClassNotFoundException, SQLException
		{
			this.Project = Project;
			this.Api=Api;
			this.Env=Env;
			this.OutputChioce=OutputChioce;
			this.UserName=UserName;
			this.Platform=Platform;
			
			WriteProperty(UserName);
			
		}
		
		protected void WriteProperty(String UserName) throws ClassNotFoundException, SQLException
		{
			DatabaseOperation.ConnectionSetup(Platform);
			
			this.put("output_in_same_table", this.RdmsValue("OutputInInputTable"));
						
            if(OutputChioce.equalsIgnoreCase("Output_Saved_in_DB"))
            {
				      this.put("actual", "Y");
				      this.put("status", "N");
            }
		    else if(OutputChioce.equalsIgnoreCase("Get_Response_Only"))
		    {
				      this.put("actual", "N");
				      this.put("status", "N");
		    }
			if(OutputChioce.equalsIgnoreCase("Compared_Results"))
			{
				      this.put("actual", "Y");
				      this.put("status", "Y");
			}
			
			this.put("sample_request", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/SampleRequest/SampleRequest" + this.RdmsValue("Version") + "/");
			this.put("request_location", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/Request/");
			this.put("response_location", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/Response/");
			
			this.put("Samplepath", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/SampleRatingModel/SampleRating" + this.RdmsValue("Version") + "/");
			this.put("TargetPath", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api +  "/RatingModelResult/");
			this.put("config_query", this.RdmsQuery("MacroMappingTable"));
			this.put("lookup_query", this.RdmsQuery("MacroTranslationTable"));
			
			this.put("test_url", this.RdmsValue("URL"));
			this.put("type", this.RdmsValue("ServiceType"));
			this.put("content_type", "application/"+this.RdmsValue("ServiceType"));
			this.put("token", this.RdmsValue("Token"));
			this.put("EventName", this.RdmsValue("EventName"));

			this.put("input_query",  this.RdmsQuery("InputTable"));
			this.put("output_query", this.RdmsQuery("OutputTable"));
			
			this.put("InputColQuery",this.RdmsQuery("InputConditonTable"));
			this.put("OutputColQuery",this.RdmsQuery("OutputConditionTable"));
			
			this.put("InputJsonPath", "InputJsonPath");
			this.put("OutputJsonPath", "OutputJsonPath");
			
			this.put("InputColumn", "InputColumn");
		    this.put("OutputColumn", "OutputColumn");
		    
			this.put("InputCondColumn", "InputColumnCondtn");
		    this.put("OutputCondColumn", "OutputColumnCondtn");
		    
		    this.put("ExpectedColumn", "ExpectedColumn");
		    this.put("StatusColumn", "StatusColumn");
		    
		    this.put("jdbc_driver", this.RdmsValue("JDCDriver"));
		    this.put("db_url", this.RdmsValue("DB_URL") + "/" + this.RdmsValue("ProjectDBName") + "_" + Platform + "_" + this.RdmsValue("UserDBName"));
		    this.put("db_username", this.RdmsValue("DB_UserName"));
		    this.put("db_password", this.RdmsValue("DB_Password"));
		    
		    DatabaseOperation.CloseConn();
		    
		}
		
		protected String RdmsQuery(String OutputColoumn) throws SQLException
		{
			ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
			return "SELECT * FROM " + ConfigQuery.ReadData(OutputColoumn);
		}
		
		protected String RdmsValue(String OutputColoumn) throws SQLException
		{
			ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
			return ConfigQuery.ReadData(OutputColoumn);
		}
		
		public PropertiesHandle(String path)
		{
			this.path = path;
			FileInputStream configuration = null;
			try 
			{
				
				configuration = new FileInputStream(path);
			} catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				System.out.println("file not found");
				e.printStackTrace();
			}
			try 
			{
				this.load(configuration);
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void store(String newpath)
		{
			Writer writer = null;
			try {
				 writer = new FileWriter(newpath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				this.store(writer, "File saved");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
		public void store()
		{
			Writer writer = null;
			try 
			{
				 writer = new FileWriter(this.path);
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try 
			{
				this.store(writer, "File saved");
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
}
