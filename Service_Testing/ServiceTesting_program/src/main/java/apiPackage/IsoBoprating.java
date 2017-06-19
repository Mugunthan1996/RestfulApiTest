package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import jxl.read.biff.BiffException;

import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;

import macroPackage.IsoMacro;
import macroPackage.MacroInterface;
import util.api.DBColoumnVerify;
import util.api.JsonHandle;
import util.common.DatabaseOperation;
import Configuration.PropertiesHandle;

public class IsoBoprating extends BaseClass implements API
{
	MacroInterface macro = null;
	public IsoBoprating(PropertiesHandle config) throws SQLException
	{
	
		this.config = config;
		jsonElements = new DatabaseOperation();
	
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		if(config.getProperty("status").equals("Y"))
		{
		macro=new IsoMacro(config);	
		}
		
	}
	
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException, BiffException, IOException
	{
		if(config.getProperty("status").equals("Y"))
		{
		macro.LoadSampleRatingmodel(config, InputData);
		macro.GenerateExpected(InputData, config);
		}
		super.LoadSampleRequest(InputData);
	}
	
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException,ClassNotFoundException, NumberFormatException, java.text.ParseException, BiffException 
	{			
		if(config.getProperty("status").equals("Y"))
		{
		macro.PumpinData(input, config);
		}
		super.PumpDataToRequest();
	}
	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException, ClassNotFoundException, NumberFormatException, java.text.ParseException
	{
		if(config.getProperty("status").equals("Y"))
		{
		macro.PumpoutData(output, input, config);
		}
		super.SendResponseDataToFile(output);
		return output;		
	}
	
}