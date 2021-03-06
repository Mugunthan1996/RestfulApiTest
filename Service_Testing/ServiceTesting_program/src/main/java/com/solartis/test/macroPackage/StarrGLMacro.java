package com.solartis.test.macroPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.MacroException;
import com.solartis.test.exception.POIException;
import com.solartis.test.exception.PropertiesHandleException;
import com.solartis.test.util.common.DatabaseOperation;
import com.solartis.test.util.common.ExcelOperationsPOI;

public class StarrGLMacro implements MacroInterface
{
	protected ExcelOperationsPOI sampleexcel=null;
	protected String Targetpath;
	protected StarrGLMacro trans;
	protected String Samplepath;
	protected DatabaseOperation configTable = null;
	protected PropertiesHandle configFile;
	
	
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public enum Alphabet 
	{
	    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ;

	    public static int getNum(String targ) 
	    {
	        return valueOf(targ).ordinal();
	    }

	    public static int getNum(char targ)
	    {
	        return valueOf(String.valueOf(targ)).ordinal();
	    }
	}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	public StarrGLMacro()
	{
		
	}
	public StarrGLMacro(PropertiesHandle configFile) throws MacroException
	{
		configTable = new DatabaseOperation();
		//configFile = new PropertiesHandle("A:/1 Projects/09 StarrGL/Release_24_UAT/RatingTrial/configuration_file/config_json.properties");
		try 
		{
			configTable.GetDataObjects(configFile.getProperty("config_query"));
		}
		catch (DatabaseException e) 
		{
			throw new MacroException("ERROR OCCURS INITILIZE THE OBJECT OF StarrGLMACRO", e);
		}
		
	}
	
	public void LoadSampleRatingmodel(PropertiesHandle configFile,DatabaseOperation inputData) throws MacroException
	{
		try
		{
			String RateingModelName = Lookup("filename",configFile);
			
			Samplepath= configFile.getProperty("Samplepath")+RateingModelName+".xls";
			sampleexcel= new ExcelOperationsPOI(Samplepath);
		}
		catch (POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE LOADING SAMPLE RATING MODEL", e);
		}
	}
	
	public void GenerateExpected(DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException
	{
		try
		{
			Targetpath =  configFile.getProperty("TargetPath")+inputData.ReadData("testdata")+".xls";
			sampleexcel.Copy(Samplepath, Targetpath);
			sampleexcel.save();
			System.out.println("generate expected rating over");
		}
		catch(DatabaseException | POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE GENERATING THE EXPECTED RATING MODEL", e);
		}
	}
	
	public void PumpinData(DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException
	{
		try
		{
			//DatabaseOperation configTable = new DatabaseOperation();
			configTable.GetDataObjects(configFile.getProperty("config_query"));
			ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
			trans= new StarrGLMacro(configFile);
			do
			{								
				if (configTable.ReadData("flag_for_execution").equalsIgnoreCase("Y"))
				{
					if (configTable.ReadData("Type").equals("input"))
					{
						String Datacolumntowrite = configTable.ReadData("Input_DB_column");
						String CellAddress = configTable.ReadData("Cell_Address");
						
						String  Datatowrite = inputData.ReadData(Datacolumntowrite);
						String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
						int columnNum=Alphabet.getNum(part[0].toUpperCase());
						int rowNum = Integer.parseInt(part[1]);
						System.out.println(columnNum+"----"+rowNum+"-----"+configTable.ReadData("Sheet_Name")+"-----"+Datatowrite);
						excel.getsheets(configTable.ReadData("Sheet_Name"));
						excel.getcell(rowNum, columnNum);
						
						if(configTable.ReadData("Translation_Flag").equals("Y"))
						{
							excel.write_data(rowNum-1, columnNum, trans.Translation1(Datatowrite, configTable, configFile));
						}
						else
						{
							if(trans.isInteger(Datatowrite))
							{
								int datadata =Integer.parseInt(Datatowrite);
								excel.write_data(rowNum-1, columnNum, datadata);	
							}
							else if(trans.isFloat(Datatowrite))
							{
								float floatdata = Float.parseFloat(Datatowrite);
								excel.write_data(rowNum-1, columnNum, floatdata);
							}
							else
							{
								excel.write_data(rowNum-1, columnNum, Datatowrite);
							}
						}
					}
				}
			}while(configTable.MoveForward());
			excel.refresh();
			excel.save();
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMP-IN THE DATA TO RATING MODEL OF StarrGL MACRO", e);
			//e.printStackTrace();
		}
		catch(POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE OPENING AND CLOSING THE RATING MODEL OF StarrGL MACRO", e);
			//e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void PumpoutData(DatabaseOperation outputData,DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException    
	{
		try
		{
		ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
		configTable.GetDataObjects(configFile.getProperty("config_query"));
		excel.refresh();
		do
		{
			
			if (configTable.ReadData("flag_for_execution").equals("Y"))
			{
				if (configTable.ReadData("Type").equals("output"))
				{
					String Datacolumntowrite = configTable.ReadData("Input_DB_column");
					String CellAddress = configTable.ReadData("Cell_Address");
					String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
					int columnNum=Alphabet.getNum(part[0].toUpperCase());
					int rowNum = Integer.parseInt(part[1]);
					excel.getsheets(configTable.ReadData("Sheet_Name"));
					excel.getcell(rowNum-1, columnNum);
					String Datatowrite = excel.read_data(rowNum-1, columnNum);
					System.out.println(Datacolumntowrite+"----------" +Datatowrite+"--------"+rowNum+"-------"+columnNum);
					outputData.WriteData(Datacolumntowrite, Datatowrite);
					//outputData.WriteData(Datacolumntowrite, "poda");
				}
			}
			outputData.UpdateRow();
		}while(configTable.MoveForward());
		excel.save();
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMPOUT THE OUTPUT FROM RATING MODEL OF StarrGL MACRO", e);
		}
		catch (POIException e)
		{
			throw new MacroException("ERROR OCCURS 	WHILE OPENING/CLOSING THE RATING MODEL OF StarrGL MACRO", e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("output over");
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T Translation1(String Datatowrite, DatabaseOperation configTable,  PropertiesHandle configFile) throws   MacroException
	{
		T outputdata = null;
		try
		{
		
			switch(configTable.ReadData("Translation_Function"))
			{
			case "Date": 
				Date DateData = Date(Datatowrite,"yyyy-mm-dd",configTable.ReadData("Translation_Format"));
				outputdata = (T) DateData;
				break;
			case "Lookup":
				String LookupData = Lookup(Datatowrite, configFile);
				outputdata = (T) LookupData;
				break;
			case "String":
				String Stringdata = IntegertoString(Datatowrite);
				outputdata = (T) Stringdata;
				break;
			case "ReplaceComma":
				int replaceddata= ReplaceComma(Datatowrite);
				Integer replacdata = new Integer (replaceddata);
				outputdata = (T) replacdata;
				break;
			case "Version":
				String versiondata=Version(Datatowrite);
				outputdata = (T) versiondata;
				break;
			}
		}
		catch (DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS 	IN TRANSLATION OF StarrGL MACRO", e);
		}
		return outputdata;
		
	}
	
	
	protected  Date Date(String Date,String InputFormat,String ExpectedFormat) throws MacroException
	{
		String value ="";
		Date Date1=null;
		
		
			Pattern p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(InputFormat);
			String InputDelimiter="";
			if(m.find())
			{
				InputDelimiter=String.valueOf(InputFormat.charAt(m.start()));
			}
			
			Matcher m1 = p.matcher(ExpectedFormat);
			String ExpectedDelimiter="";
			if(m1.find())
			{
				ExpectedDelimiter=String.valueOf(ExpectedFormat.charAt(m1.start()));
			}
			
			String[] DateInputFormat = InputFormat.split(InputDelimiter);
			String[] DateOutputFormat = ExpectedFormat.split(ExpectedDelimiter);
			String[] date = Date.split(InputDelimiter); //yyyy-mm-dd
			
			HashMap<String,String> DateMaping = new HashMap<String,String>();
			DateMaping.put(DateInputFormat[0].toLowerCase(), date[0]);
			DateMaping.put(DateInputFormat[1].toLowerCase(), date[1]);
			DateMaping.put(DateInputFormat[2].toLowerCase(), date[2]);
			value =  DateMaping.get(DateOutputFormat[0].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[1].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[2].toLowerCase());     
			DateFormat format = new SimpleDateFormat(ExpectedFormat, Locale.ENGLISH);
			try 
			{
				Date1=format.parse(value);
			} 
			catch (NumberFormatException | ParseException e) 
			{
				// TODO Auto-generated catch block
				throw new MacroException("ERROR OCCURS 	IN DATE FORMAT OF StarrGL MACRO", e);
			}  			
		   // System.out.println(value+"\t"+Date1);  						
		
		
		return Date1;
		
	}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	protected String  Lookup(String Lookup1, PropertiesHandle configFile) throws  MacroException
	{
		
		DatabaseOperation Lookup = new DatabaseOperation();
		try 
		{
			Lookup.GetDataObjects(configFile.getProperty("lookup_query"));
		} 
		catch (DatabaseException e) 
		{
			// TODO Auto-generated catch block
			throw new MacroException("ERROR OCCURS 	IN LOOKUP QUERY OF StarrGL MACRO", e);
		}
		HashMap<String,String> LookupMap = new HashMap<String,String>();
		try 
		{
			do
			{
				LookupMap.put(Lookup.ReadData("LookupData"), Lookup.ReadData("LookupValue"));
				
				
			}while(Lookup.MoveForward());
		} 
		catch (DatabaseException e) 
		{
			// TODO Auto-generated catch block
			throw new MacroException("ERROR OCCURS 	IN LOOKUP TABLE OF StarrGL MACRO", e);
		}
		
		if (LookupMap.get(Lookup1)==null)
		{
			return "Other";
		}
		else
		{
			return LookupMap.get(Lookup1);
		}
	}
	
	protected boolean isInteger(String s) 
	{
	    try 
	    { 
	    	
	        Integer.parseInt(s); 
	    } 
	    catch(NumberFormatException e) 
	    { 
	        return false; 
	    } 
	    catch(NullPointerException e) 
	    {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	protected boolean isFloat(String s)
	{
		try
		{
			Float.parseFloat(s);
		}
		catch(NumberFormatException e) 
	    { 
	        return false; 
	    } 
	    catch(NullPointerException e) 
	    {
	        return false;
	    }
		 return true;
	}
	
	protected String IntegertoString (String s)
	{
		return s;
		
	}
	
	protected int ReplaceComma(String s)
	{
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean b = m.find();
		String res="";
		int num=0;
		if (b)
		{
			 s = s.replaceAll("(?<=\\d),(?=\\d)", "");
			 num= Integer.parseInt(s);
		}
		else
		{
			num=Integer.parseInt(s);
		}

		return num;
		
	}
	
	protected String Version(String s)
	{
		
		s=s.substring(1);
		s=s.replace("-", "_");
		return s;
		
	}
	
	public static void main(String args[]) throws  DatabaseException, MacroException, PropertiesHandleException
	{
	 DatabaseOperation objectInput = new DatabaseOperation();
	 DatabaseOperation objectOutput = new DatabaseOperation();
	 StarrGLMacro sm;
	 PropertiesHandle configFile = new PropertiesHandle("E:/RestFullAPIDeliverable/Devolpement/admin/STARR-GL/Rating/config/config.properties");
	 
	 DatabaseOperation.ConnectionSetup(configFile);
	 objectInput.GetDataObjects(configFile.getProperty("input_query"));
	 objectOutput.GetDataObjects(configFile.getProperty("output_query"));
	 do
	 {
	  System.out.println("TestData : " + objectInput.ReadData("S.No"));   
	    if(objectInput.ReadData("Flag_for_execution").equals("Y"))
	    {
	     sm=new StarrGLMacro(configFile);
	     sm.LoadSampleRatingmodel(configFile, objectInput);
	     sm.GenerateExpected(objectInput, configFile);
	     sm.PumpinData(objectInput, configFile);
	     sm.PumpoutData(objectOutput,objectInput, configFile);
	    }
	    objectInput.WriteData("Flag_for_execution", "Completed"); 
	    objectInput.UpdateRow();
	 }while(objectInput.MoveForward()&&objectOutput.MoveForward());
	}
}