package servicetesting.ServiceTestingProgram;

import util.common.DatabaseOperation;
import apiPackage.API;
import apiPackage.ChicForms;
import apiPackage.ChicRating;
import apiPackage.DtcCancel;
import apiPackage.DtcFindPolicy;
import apiPackage.DtcGetCustomerDetails;
import apiPackage.DtcGetPolicy;
import apiPackage.DtcNonMonetoryEndorsement;
import apiPackage.DtcPayIssue;
import apiPackage.DtcPreviewPDF;
import apiPackage.DtcRatingService;
import apiPackage.DtcRatingServiceEnhancement;
import apiPackage.DtcSaveDetails1;
import apiPackage.DtcSaveDetails2;
import apiPackage.DtcSaveDetails3;
import apiPackage.DtcSaveDetails4;
import apiPackage.IsoBopCancel;
import apiPackage.IsoBopEndorsement;
import apiPackage.IsoBopEndorsementRate;
import apiPackage.IsoBopInstalllmentPayissue;
import apiPackage.IsoBopPayissue;
import apiPackage.IsoBopPayissueCancel;
import apiPackage.IsoBopQuote;
import apiPackage.IsoBopRateCancel;
import apiPackage.IsoBopissue;
import apiPackage.IsoBoprating;
import apiPackage.LDWCRating;
import apiPackage.SolartisIsoBopRating;
import apiPackage.StarrSearchRescueIssueCertificate;

import java.io.IOException;
import java.sql.SQLException;

import jxl.read.biff.BiffException;

import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;

import Configuration.PropertiesHandle;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	
	static Logger logError = Logger.getLogger("ERRORlog");
	static Logger logInfo = Logger.getLogger("INFOlog");
	static API api=null;
	
	public static void main( String[] args ) throws ClassNotFoundException, SQLException 
    {   
		System.setProperty("jsse.enableSNIExtension", "false");
		
		PropertiesHandle config = new PropertiesHandle(args[0], args[1], args[2], args[3], args[4], args[5]);
		
		try                                      
		{
			logInfo.info("Connecting DataBase");
			DatabaseOperation.ConnectionSetup(config);
		} 
		catch (ClassNotFoundException e) 
		{
			logError.error("Failed DataBase Connection -- ClassNotFoundError");
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			logError.error("Failed DataBase Connection -- SQLError");
			e.printStackTrace();
		}
		String actualchoice = config.getProperty("actual");
		String statuschoice = config.getProperty("status");
		String outputtable = config.getProperty("output_in_same_table");
		
		DatabaseOperation input = new DatabaseOperation();
		try 
		{
			logInfo.info("Creating Instance for Input");
			input.GetDataObjects(config.getProperty("input_query"));
		}
		catch (SQLException e1) 
		{
			logError.error("Failed Creating Instance for Input -- SQLError");
			e1.printStackTrace();
		}
		
		DatabaseOperation output = new DatabaseOperation();
		try 
		{
			logInfo.info("Creating Instance for Output");
			output.GetDataObjects(config.getProperty("output_query"));	
		} 
		catch (SQLException e1) 
		{
			logError.error("Failed Creating Instance for Output -- SQLError");
			e1.printStackTrace();
		}
		
		String Project = args[0];
		String Api = args[1];
		String ApiType = Project + Api;
		
		logInfo.info("Selecting API");
		try 
		{
		switch(ApiType.toLowerCase())//SELECTING API
        {
      
          case "starr-isoquote":
        	    logInfo.info("IsoBopQuote API Selected");
				api = new IsoBopQuote(config);
    	        break;
      
          case "starr-isorate":
        	    logInfo.info("IsoBopRating API Selected");
    	        api = new IsoBoprating(config);
    	        break;
    	  
          case "starr-isoendorsement":
        	    logInfo.info("IsoBopEndorsement API Selected");
    	        api = new IsoBopEndorsement(config);
    	        break;
    	        
          case "starr-isoendorsementrate":
	      	    logInfo.info("IsoBopEndorsementRate API Selected");
	  	        api = new IsoBopEndorsementRate(config);
	  	        break;      
    	        
          case "starr-isoinstallmentpayissue":
        	    logInfo.info("IsoBopInstallmentPayIssue API Selected");
    	        api = new IsoBopInstalllmentPayissue(config);
    	        break;
    	  
          case "starr-isoissue":
        	    logInfo.info("IsoBopIssue API Selected");
    	        api = new IsoBopissue(config);
    	        break;
    	  
          case "starr-isopayissue":
        	    logInfo.info("IsoBopPayIssue API Selected");
        	    api = new IsoBopPayissue(config);
    	        break;
    	  
          case "starr-isopayissuecancel":
        	    logInfo.info("IsoBopPayIssueCancel API Selected");
        	    api = new IsoBopPayissueCancel(config);
    	        break;
          
          case "starr-isocancel":
        	  	logInfo.info("IsoBopCancel API Selected");
	      	    api = new IsoBopCancel(config);
	  	        break;
  	  
    	        
          case "starr-solartisisoboprating":
        	    logInfo.info("SolartisISORating API Selected");
    	        api = new SolartisIsoBopRating(config);	
    	        break;
    	  
          case "starr-isoratecancel":
        	    logInfo.info("IsoBopRateCancel API Selected");
    	        api = new IsoBopRateCancel(config);	  
    	        break;
    	       
    	        
          case "chicform":
        	    logInfo.info("ChicForm API Selected");
  	            api = new ChicForms(config);
  	            break;
  	        
          case "chicrating":
        	    logInfo.info("ChicRating API Selected");
  	            api = new ChicRating(config);
  	            break;
  	        
          case "starr-dtcfindpolicy":
        	    logInfo.info("DTCFindPolicy API Selected");
  	            api = new DtcFindPolicy(config);
  	            break;
  	            
          case "starr-dtcgetpolicy":
        	    logInfo.info("DTCGetPolicy API Selected");
        	    api = new DtcGetPolicy(config);
	            break;
  	        
          case "starr-dtcgetcustomerdetails":
        	    logInfo.info("DTCGetCustomerDetails API Selected");
  	            api = new DtcGetCustomerDetails(config);
  	            break;
  	        
          case "starr-dtcpayissue":
        	    logInfo.info("DTCPayIssue API Selected");
  	            api = new DtcPayIssue(config);
  	            break;
  	        
          case "starr-dtcpreviewpdf":
        	    logInfo.info("DTCPreviewPDF API Selected");
  	            api = new DtcPreviewPDF(config);
  	            break;
  	        
          case "starr-dtcratingservice":
        	   logInfo.info("DTCRating API Selected");
  	           api = new DtcRatingService(config);
  	           break;
  	              
          case "starr-dtcratingenhancement":               
        	  logInfo.info("DTCRatingEnhancement API Selected");
 	           api = new DtcRatingServiceEnhancement(config);
 	           break;
        	   	  
          case "starr-dtcsavedetails1":
        	    logInfo.info("DTCSave1 API Selected");
  	            api = new DtcSaveDetails1(config);
  	            break;
  	        
          case "starr-dtcsavedetails2":
        	    logInfo.info("DTCSave2 API Selected");
        	    api = new DtcSaveDetails2(config);
  	            break;
  	        
          case "starr-dtcsavedetails3":
        	    logInfo.info("DTCSave3 API Selected");
    	        api = new DtcSaveDetails3(config);
    	        break;
    	        
          case "starr-dtcsavedetails4":
        	    logInfo.info("DTCSave4 API Selected");
    	        api = new DtcSaveDetails4(config);
    	        break;
    	        
          case "starrsearchrescueissuecertificate":
        	    logInfo.info("StarrSearch&RescueIssueCertificate API Selected");
  	            api = new StarrSearchRescueIssueCertificate(config);
  	            break;
  	            
          case "starr-dtccancelpolicy":
      	        logInfo.info("DTCCancelPolicy API Selected");
	            api = new DtcCancel(config);
	            break;
	            
          case "starr-dtcnonmonetoryendorsement":
        	     logInfo.info("DTCNonMonetoryEndorsement API Selected");
	            api = new DtcNonMonetoryEndorsement(config);
	            break;
	            
          case "starr-ldwcrating":
      	    logInfo.info("LDWCRating API Selected");
	            api = new LDWCRating(config);
	            break;
        	  
    	        
          default :
        	     logError.error("API Selected is Wrong");
        	     System.out.println("API is not coded"); 
        	     System.exit(0);
        	     break;
    	  
       }
	   } 
		catch (SQLException e) 
		{
			logError.error("Failed Failed Selecting API -- SQLError");
			e.printStackTrace();
		}

			try 
			{

				do
				{
					logInfo.info("TestData" + input.ReadData("S.No") + "Running" );
					System.out.println("TestData : " + input.ReadData("S.No"));  	
							if(input.ReadData("flag_for_execution").equals("Y"))
							{
							    logInfo.info("TestData" + input.ReadData("S.No") + "flag_for_execution = Y" );	
								try
								{
									logInfo.info("Loading Sample Request for Testdata--" + input.ReadData("S.No"));
									
									api.LoadSampleRequest(input);//LOADING SAMPLE REQUEST
								}
								catch (SQLException| BiffException| IOException e)
								{
									logError.error("Failed to Load Sample; Testdata--" + input.ReadData("S.No") + "To Sample Request---SQLException | BiffException | IOException");
									e.printStackTrace();
								}
								try 
								{
									logInfo.info("Pumping Testdata--" + input.ReadData("S.No") + "To Sample Request");
								      api.PumpDataToRequest();//PUMPING TESTDATA TO SAMPLEREQUEST 
								} 
								catch (IOException | DocumentException | ParseException |NumberFormatException | java.text.ParseException |BiffException e)
								{
									logError.error("Failed Pumping Testdata--" + input.ReadData("S.No") + "To Sample Request---IOException | DocumentException | ParseException");
									e.printStackTrace();
								} 
								
								try 
								{
									logInfo.info("REQUEST For Testdata--" + input.ReadData("S.No"));
									  String req = api.RequestToString();//SHOWING REQUEST IN LOG 
								} 
								catch (IOException | ParseException | DocumentException e1) 
								{
									logError.error("Failed Rquest For Testdata--" + input.ReadData("S.No") + "---IOException | ParseException | DocumentException");
									e1.printStackTrace();
								} 
								
								try 
								{
									logInfo.info("Adding Header For Testdata--" + input.ReadData("S.No"));
									  api.AddHeaders();//ADDING HEADER || TOKENS || EVENTS FOR HITTING REQUEST
								} 
								catch (IOException e) 
								{
									logError.error("Failed Adding Header For Testdata--" + input.ReadData("S.No") + "---IOException");
									e.printStackTrace();
								}
								
									logInfo.info("Respone For Testdata--" + input.ReadData("S.No") + "is Received");
									api.SendAndReceiveData();//RECIEVING AND STORING RESPONSE TO THE FILE
								
									logInfo.info("REQUEST For Testdata--" + input.ReadData("S.No"));
									try 
									{
										logInfo.info("REQUEST For Testdata--" + input.ReadData("S.No"));
										  String res = api.ResponseToString();//SHOWING RESPONSE IN LOG 
									} 
									catch (IOException | ParseException | DocumentException e1) 
									{
										logError.error("Failed Rquest For Testdata--" + input.ReadData("S.No") + "---IOException | ParseException | DocumentException e1");
										e1.printStackTrace();
									}						
								
								if(actualchoice.equals("Y"))
								{
									 logInfo.info("Fetching Data from Response to Store in DB is selected");
									  
								if(outputtable.equals("Y"))//INPUT AND OUT DB TABLE ARE SAME
								{	
							    try 
							    {
										logInfo.info("Storing Response--" + input.ReadData("S.No") + "Data into DB");
										input = api.SendResponseDataToFile(input);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
								} 
								catch (IOException | ParseException | DocumentException | NumberFormatException| java.text.ParseException e) 
								{
										 logError.error("Failed Storing Response--" + input.ReadData("S.No") + "Data into DB---IOException | ParseException | DocumentException e");
										 e.printStackTrace();
								}
									 
									     logInfo.info("Updating DB For Testdata--" + input.ReadData("S.No"));
									     input.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA
								}
								else//INPUT AND OUT DB TABLE ARE DIFFERENT
								{	
								try 
							    {
										logInfo.info("Storing Response--" + input.ReadData("S.No") + "Data into DB");
										output = api.SendResponseDataToFile(output);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
								} 
								catch (IOException | ParseException | DocumentException |NumberFormatException | java.text.ParseException e) 
							    {
										 logError.error("Failed Storing Response--" + input.ReadData("S.No") + "Data into DB---IOException | ParseException | DocumentException e");
										 e.printStackTrace();
								}
									 
									     logInfo.info("Updating DB For Testdata--" + input.ReadData("S.No"));
									     output.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA	
								}
								} 
								
								if(statuschoice.equals("Y"))
								{
									if(outputtable.equals("Y"))
									{
										logInfo.info("Comparison is selected");
										 logInfo.info("Starts Copmaring Expected and atual");
										 input = api.CompareFunction(input);//CALLING COMPARING FUNCTION
									     logInfo.info("Updating DB For Comparison");
									     input.UpdateRow();
									}
									else
									{
										 logInfo.info("Comparison is selected");
										 logInfo.info("Starts Copmaring Expected and atual");
										 output = api.CompareFunction(output);//CALLING COMPARING FUNCTION
									     logInfo.info("Updating DB For Comparison");
										 output.UpdateRow();
									}
								} 
								
								input.WriteData("Flag_for_execution", "Completed");
								  input.UpdateRow();//UPDATE DB TABLE ROWS AFTER COMPARSION
								}
							else
							{
								logError.error("TestData" + input.ReadData("S.No") + "---flag_for_execution N");
								logInfo.info("TestData" + input.ReadData("S.No") + "---flag_for_execution N" );
							}
					logInfo.info("Moving To Next TestData");
					if(actualchoice.equals("Y") || statuschoice.equals("Y"))
					{
						output.MoveForward();
					}
				}while(input.MoveForward());
			} 
			catch (SQLException e1)
			{
				e1.printStackTrace();
				System.exit(0);
				

			}

		try 
		{
			logInfo.info("Closing DB Connection");
			DatabaseOperation.CloseConn();
		}
		catch (SQLException e) 
		{
			logError.error("Failed DataBase Closing -- SQLError");
			e.printStackTrace();
			System.exit(0);
		}
   }
}	
