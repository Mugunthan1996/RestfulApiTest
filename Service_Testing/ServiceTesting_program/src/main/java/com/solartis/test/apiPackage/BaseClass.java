package com.solartis.test.apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import jxl.read.biff.BiffException;

import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;

import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.util.api.*;
import com.solartis.test.util.common.*;

public class BaseClass 
{
	protected RequestResponse sampleInput = null;
	protected RequestResponse request = null;
	protected RequestResponse response = null;
	protected DatabaseOperation XmlElements = null;
	protected DatabaseOperation jsonElements = null;
	protected PropertiesHandle config = null;
	protected DatabaseOperation input = null;
	protected DatabaseOperation output = null;
	protected HttpHandle http = null;
	protected DBColoumnVerify InputColVerify = null;
	protected DBColoumnVerify OutputColVerify = null;
	protected DBColoumnVerify StatusColVerify = null;
	
//---------------------------------------------------------------LOAD SAMPLE REQUEST--------------------------------------------------------------------	
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException, BiffException, IOException
	{
		this.input = InputData;
		sampleInput = new JsonHandle(config.getProperty("sample_request") + "request.json");
	}

//-----------------------------------------------------------PUMPING TEST DATA TO REQUEST--------------------------------------------------------------- 	
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException, ClassNotFoundException,NumberFormatException, java.text.ParseException, BiffException 
	{
		InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+".json");
		request.StringToFile(sampleInput.FileToString());
		
		do
		{
			if(InputColVerify.DbCol(input) && (InputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
			{
				if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
				{

					request.write(InputColVerify.ReadData(config.getProperty("InputJsonPath")), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
				}
			}	
		}while(InputColVerify.MoveForward());
		
		
	}

//------------------------------------------------------------CONVERTING REQUEST TO STRING--------------------------------------------------------------	
	public String RequestToString() throws IOException, ParseException, DocumentException
	{
		return request.FileToString();
	}
	
//-------------------------------------------------------------ADDING HEADER || TOKENS------------------------------------------------------------------	
	public void AddHeaders() throws IOException
	{
		http = new HttpHandle(config.getProperty("test_url"),"POST");
		http.AddHeader("Content-Type", config.getProperty("content_type"));
		http.AddHeader("Token", config.getProperty("token"));
	}

//------------------------------------------------------------STORING RESPONSE TO FOLDER----------------------------------------------------------------	
	public void SendAndReceiveData() throws SQLException
	{
		String input_data= null;
		try 
		{
			input_data = request.FileToString();
		} 
		catch (IOException | ParseException | DocumentException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			http.SendData(input_data);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		String response_string = null;
		
		try 
		{
			response_string = http.ReceiveData();
		} 
		catch (Exception e) 
		{
			
			e.printStackTrace();
		}
		response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+".json");
		try 
		{
			response.StringToFile(response_string);
		} 
		catch (IOException | DocumentException e) 
		{
			e.printStackTrace();
		}
	}
	
//-------------------------------------------------------------CONVERTING RESPONSE TO STRING------------------------------------------------------------
	public String ResponseToString() throws IOException, ParseException, DocumentException
	{
		return response.FileToString();
	}
	
//-----------------------------------------------------------UPDATING RESPONSE DATA TO DATABASE---------------------------------------------------------	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException, ClassNotFoundException,NumberFormatException, java.text.ParseException
	{
		this.output=output;
		OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
		do 	
		{
		  if(OutputColVerify.DbCol(input) && (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
			{
			try
				{
				System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
				String actual = (response.read(OutputColVerify.ReadData(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
				output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
				System.out.println(actual);
				output.WriteData("flag_for_execution", "Completed");
				}
				catch(PathNotFoundException e)
				{
					output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), "Path not Found");
				}
			}
		}while(OutputColVerify.MoveForward());
	
		
	return output;
	}

//---------------------------------------------------------------COMAPRISION FUNCTION-------------------------------------------------------------------	
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws SQLException, ClassNotFoundException
	{		
		StatusColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
		do 	
		{	
		  if(StatusColVerify.DbCol(input) && (StatusColVerify.ReadData("Comaparision_Flag").equalsIgnoreCase("Y")))
			{
				String ExpectedColumn = StatusColVerify.ReadData(config.getProperty("ExpectedColumn"));
				String ActualColumn = StatusColVerify.ReadData(config.getProperty("OutputColumn"));
				String StatusColumn = StatusColVerify.ReadData(config.getProperty("StatusColumn"));
				if(!(StatusColumn.equals("")) && !(ExpectedColumn.equals("")))
				{
					if(premium_comp(output.ReadData(ExpectedColumn),output.ReadData(ActualColumn)))
					{
						output.WriteData(StatusColumn, "Pass");
					}
					else
					{
						output.WriteData(StatusColumn, "Fail");
					}
				}
				
			}
		 }while(StatusColVerify.MoveForward());

	return output;
	}
	
//-----------------------------------------------------PRIVATE FUNCTION FOR SUPPORTING COMPARISON FUNCTION---------------------------------------------------	
	protected static boolean premium_comp(String expected,String actual)
	{
		
		boolean status = false;
		if(expected == null)
		{
			if(actual.equals("")||actual.equals("0"))
			{
				status = true;
			}
		}
		if(actual == null)
		{
			if((expected.equals("")||expected.equals("0")))
			{
				status = true;
			}
		}
		if(expected!=null && actual!=null)
		{
            expected = expected.replaceAll("\\[\"", "");
    		actual = actual.replaceAll("\\[\"", "");
    		expected = expected.replaceAll("\"\\]", "");
    		actual = actual.replaceAll("\"\\]", "");
    		expected = expected.replaceAll("\\.[0-9]*", "");
    		actual = actual.replaceAll("\\.[0-9]*", "");
    		if(expected.equals(actual))
    		{
    			status = true;
    		}
			status = false;
		}

		return status;	
		
	}
	
}