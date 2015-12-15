package com.atlassian.jira_soapclient;

import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteComponent;
import com.atlassian.jira.rpc.soap.client.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteFilter;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.atlassian.jira.rpc.soap.client.RemotePermissionScheme;
import com.atlassian.jira.rpc.soap.client.RemoteScheme;
import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteProject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class SOAPClient
{
    static final String LOGIN_NAME = "admin";
    static final String LOGIN_PASSWORD = "admin";
    static final String PROJECT_KEY = "TST";
    static final String ISSUE_TYPE_ID = "1";
    static final String SUMMARY_NAME = "An issue created via the JIRA SOAPClient sample : " + new Date();
    static final String PRIORITY_ID = "4";
    static final String NEW_COMMENT_BODY = "This is a new comment";

    public static void main(String[] args) throws Exception
    {
    	CreateFilter();
    	//CreateEpic();
//        String baseUrl = "http://elmerfudd:2990/jira/rpc/soap/jirasoapservice-v2";
//        System.out.println("JIRA SOAP client sample");
//        SOAPSession soapSession = new SOAPSession(new URL(baseUrl));
//        soapSession.connect(LOGIN_NAME, LOGIN_PASSWORD);
//        JiraSoapService jiraSoapService = soapSession.getJiraSoapService();
//        String authToken = soapSession.getAuthenticationToken();
//        RemoteProject project = testCreateProject(jiraSoapService, authToken);
//        RemoteIssue issue = testCreateIssue(jiraSoapService, authToken);
    }

    private static void CreateEpic()
    {
    	try
    	{
    		Client client = Client.create();
    		client.addFilter(new HTTPBasicAuthFilter("dbarringer", "b@$FRt93"));
    		
    		WebResource webSite = client.resource("http://devjra01:8080/rest/api/2/issue");
    		
    		String issueinput = "{\"fields\":{\"project\":{\"key\":\"CRP\"},\"customfield_10005\":\"Created via REST Call\",\"summary\":\"Created via REST Call\",\"description\":\"This is a dummy Epic\",\"reporter\":{\"name\":\"dbarringer\"},\"issuetype\":{\"name\":\"Epic\"}}}";
    		
    		ClientResponse response = webSite.type("application/json").post(ClientResponse.class, issueinput);
    		
    		String output = response.getEntity(String.class);
    		
    		System.out.println("Output from Server ... \n");
    		System.out.println(output);  		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    private static void CreateFilter()
    {
    	try
    	{
    		Client client = Client.create();
    		client.addFilter(new HTTPBasicAuthFilter("dbarringer", "b@$FRt93"));
    		
    		WebResource webSite = client.resource("http://devjra01:8080/rest/api/2/filter");
    		
    		String filterinput = "{\"name\":\"Filter From REST\",\"description\":\"Filter created via REST api call\",\"jql\":\"project = CRP AND issuetype = Epic\",\"favourite\":true}";
    		
    		ClientResponse response = webSite.type("application/json").post(ClientResponse.class, filterinput);
    		
    		String output = response.getEntity(String.class);
    		
    		System.out.println("Output from Server ... \n");
    		System.out.println(output);  		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}    	
    }
    
    private static RemoteIssue testCreateIssue(JiraSoapService jiraSoapService, String token)
            throws java.rmi.RemoteException
    {
        System.out.println("CreateIssue");
            RemoteIssue issue = new RemoteIssue();
            issue.setProject(PROJECT_KEY);
            issue.setType(ISSUE_TYPE_ID);
            issue.setSummary(SUMMARY_NAME);
            issue.setPriority(PRIORITY_ID);
            issue.setDuedate(Calendar.getInstance());
            issue.setAssignee("");

            // Run the create issue code
            RemoteIssue returnedIssue = jiraSoapService.createIssue(token, issue);
            final String issueKey = returnedIssue.getKey();

            System.out.println("\tSuccessfully created issue " + issueKey);
            printIssueDetails(returnedIssue);
            return returnedIssue;
    }

    private static void printIssueDetails(RemoteIssue issue)
    {
        System.out.println("Issue Details : ");
        Method[] declaredMethods = issue.getClass().getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++)
        {
            Method declaredMethod = declaredMethods[i];
            if (declaredMethod.getName().startsWith("get") && declaredMethod.getParameterTypes().length == 0)
            {
                System.out.print("\t Issue." + declaredMethod.getName() + "() -> ");
                try
                {
                    Object obj = declaredMethod.invoke(issue, new Object[] { });
                    if (obj instanceof Object[])
                    {
                        obj = arrayToStr((Object[]) obj);
                    }
                    else
                    {
                    }
                    System.out.println(obj);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static RemoteProject testCreateProject(JiraSoapService jiraSoapService, String token)
            throws java.rmi.RemoteException
    {
        System.out.println("CreateProject");
        RemoteProject project = new RemoteProject();
        project.setKey("ONE");
        project.setName("SOAP2-Test");
        project.setDescription("SOAP2-Test Dscription");
        project.setUrl("");
        project.setLead("soaptester");
        RemotePermissionScheme permission = new RemotePermissionScheme();
        permission.setId(new Long("0"));
        project.setPermissionScheme(permission);
        RemoteScheme notificationScheme = null;
        RemoteScheme issueSecurityScheme = null;
        project.setNotificationScheme(notificationScheme);
        project.setIssueSecurityScheme(issueSecurityScheme);
        
        // Run the create project code
        RemoteProject returnedProject = jiraSoapService.createProjectFromObject(token, project);
        return returnedProject;
    }
    
    private static String arrayToStr(Object[] o)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < o.length; i++)
        {
            sb.append(o[i]).append(" ");
        }
        return sb.toString();
    }

    private static byte[] getBytesFromFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length < Integer.MAX_VALUE)
        {
            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
            {
                offset += numRead;
            }

            if (offset < bytes.length)
            {
                throw new IOException("Could not completely read file " + file.getName());
            }

            is.close();
            return bytes;
        }
        else
        {
            System.out.println("File is too large");
            return null;
        }
    }
}