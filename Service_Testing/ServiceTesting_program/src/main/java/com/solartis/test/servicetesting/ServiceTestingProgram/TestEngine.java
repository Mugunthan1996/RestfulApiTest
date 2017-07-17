package com.solartis.test.servicetesting.ServiceTestingProgram;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.PropertiesHandleException;
import com.solartis.test.listener.FireEventAPI;
import com.solartis.test.listener.Listener;
import com.solartis.test.listener.LogListener;
import com.solartis.test.util.common.DatabaseOperation;

public class TestEngine 
{
	static API api=null;
	static FireEventAPI fireEventAPI;
	
	public static void main( String[] args ) throws DatabaseException, PropertiesHandleException, APIException
    {   
		System.setProperty("jsse.enableSNIExtension", "false");
		
		PropertiesHandle config = new PropertiesHandle(System.getProperty("Project"), System.getProperty("Api"), System.getProperty("Env"), System.getProperty("OutputChioce"), System.getProperty("UserName"), System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"), System.getProperty("Priority"));

		DatabaseOperation.ConnectionSetup(config);
		
		String actualchoice = config.getProperty("actual");
		String statuschoice = config.getProperty("status");
		String outputtable = config.getProperty("output_in_same_table");
		String classname = config.getProperty("ClassName");
		
		DatabaseOperation input = new DatabaseOperation();
		
		input.GetDataObjects(config.getProperty("input_query"));
		
		DatabaseOperation output = new DatabaseOperation();
		
		output.GetDataObjects(config.getProperty("output_query"));
		
		try 
		{
			Class<?> cl = Class.forName("com.solartis.test.apiPackage."+classname);
			Constructor<?> cons = cl.getConstructor(com.solartis.test.Configuration.PropertiesHandle.class);
		    api = (API) cons.newInstance(config);
		    fireEventAPI = new FireEventAPI(api);
		    Listener listener = new LogListener();
		    fireEventAPI.addListener(listener);
		    
		} 
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			System.out.println("Error in Selecting API");
		}
	    

			try 
			{

				do
				{
					System.out.println("TestData : " + input.ReadData("S.No"));  	
							if(input.ReadData("flag_for_execution").equals("Y"))
							{
							    System.out.println("TestData" + input.ReadData("S.No") + "flag_for_execution = Y" );					 
								
							    fireEventAPI.LoadSampleRequest(input);//LOADING SAMPLE REQUEST
                                
							    fireEventAPI.PumpDataToRequest();//PUMPING TESTDATA TO SAMPLEREQUEST 
							    
							    fireEventAPI.RequestToString();//SHOWING REQUEST IN LOG 
							
							    fireEventAPI.AddHeaders();//ADDING HEADER || TOKENS || EVENTS FOR HITTING REQUEST
								
							    fireEventAPI.SendAndReceiveData();//RECIEVING AND STORING RESPONSE TO THE FILE
								
							    fireEventAPI.ResponseToString();//SHOWING RESPONSE IN LOG 
								
								if(actualchoice.equals("Y"))
								{
									  
									if(outputtable.equals("Y"))//INPUT AND OUT DB TABLE ARE SAME
									{	
								    	input = api.SendResponseDataToFile(input);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
									
								    	input.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA
									}
									else//INPUT AND OUT DB TABLE ARE DIFFERENT
									{	
										output = api.SendResponseDataToFile(output);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
									
										output.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA	
									}
								} 
								
								if(statuschoice.equals("Y"))
								{
									if(outputtable.equals("Y"))
									{
										 input = api.CompareFunction(input);//CALLING COMPARING FUNCTION
									     
										 input.UpdateRow();
									}
									else
									{
										output = api.CompareFunction(output);//CALLING COMPARING FUNCTION
									    
										output.UpdateRow();
									}
								} 
								
								input.WriteData("Flag_for_execution", "Completed");
								input.UpdateRow();//UPDATE DB TABLE ROWS AFTER COMPARSION
								}
							else
							{
								System.out.println("TestData" + input.ReadData("S.No") + "---flag_for_execution N");
							}
					
					if(actualchoice.equals("Y") || statuschoice.equals("Y"))
					{
						output.MoveForward();
					}
				}while(input.MoveForward());
			} 
			catch (Exception e1)
			{
				System.exit(0);	

			}

		DatabaseOperation.CloseConn();
   }
}	
