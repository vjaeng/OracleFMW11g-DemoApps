package com.otn.sample.fod.soa.util;

import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete shared (abstract) wsdls and XSDs from the foreign MDS 
 * (apps namespace)
 * @author clemens utschig
 */
public class MdsDeleteTask extends MdsImportTask 
{
    public MdsDeleteTask() 
    {
        super();
    }    
    
    public static void main (String [] args) 
    {
        MdsDeleteTask task = new MdsDeleteTask();
        if (args == null || args.length < 4) 
        {
            System.out.println("Input parameter count failed, must be 4 (" +
                "actual: " + (args != null ? args.length : "0") + ")\n"+
                    "<source adf-config.xml location> " + 
                    "<mds store name> <namespace path> " +
                    "<source directory> \n Using defaults > " +
                    " ../.adf/META-INF/adf-config.xml mstore-usage_1 " + 
                               "apps ../../soa-seed");

            // adf config location 
            task.setAdfConfigLocation("../.adf/META-INF/adf-config.xml");
            // store definition in the above
            task.setMdsStoreName("mstore-usage_1");
            // the namespace (which corresponds to the child folder of soa-seed
            task.setNamespace("apps");
            // the source directory to start at
            task.setSourceBaseFile("./soa-seed");
        } else 
        {
            task.setAdfConfigLocation(args[0]);
            task.setMdsStoreName(args[1]);
            task.setNamespace(args[2]);
            task.setSourceBaseFile(args[3]);  
        }

        if (args.length == 5) {
            task.setValidateOnly(new Boolean(args[4]).booleanValue());   
        }

        // execute the task
        task.execute();        
    }
    
    /**
     * Executes the transfer between the local (filesystem, app based) mds and 
     * the remote mds configured in adf-config.xml
     * @throws BuildException in case something goes wrong during the transfer
     */
    @Override
    public void execute() throws BuildException 
    {
        setDoDelete(true);        
        super.execute();        
    }    
    
}
