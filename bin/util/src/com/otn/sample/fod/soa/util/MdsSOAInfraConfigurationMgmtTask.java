package com.otn.sample.fod.soa.util;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import oracle.mds.core.MDSInstance;
import oracle.mds.internal.transfer.InternalTUnitResult;
import oracle.mds.transfer.MDSTransfer;
import oracle.mds.transfer.TransferType;
import oracle.mds.transfer.TransferUnit;
import oracle.mds.transfer.TransferUnitList;

import org.apache.tools.ant.BuildException;
/**
 *  Task to transfer files off a remote repository into the local application
 *  one
 *  @author clemens utschig
 */
public class MdsSOAInfraConfigurationMgmtTask extends MdsImportTask 
{
    
    private final String STR_IMPORT = "import";
    private final String STR_EXPORT = "export";
    
    /**
     * Delegate call to the MdsImportTask
     */
    public MdsSOAInfraConfigurationMgmtTask () 
    {
        super();    
    }
    
    public static void main(String[] args) throws Exception 
    {
        MdsSOAInfraConfigurationMgmtTask task = new MdsSOAInfraConfigurationMgmtTask();
        if (args == null || args.length < 4) 
        {
            System.out.println("Input parameter count failed, must be 4 (" +
                "actual: " + (args != null ? args.length : "0") + ")\n"+
                    "<src adf-config.xml location> " + 
                    "<mds store name> <namespace path> " +
                    "<local directory> \n Using defaults > " +
                    " ../.adf/META-INF/adf-config.xml mstore-usage_1 " + 
                               "soa/configuration/default ../../soa-seed");

            // adf config location 
            task.setAdfConfigLocation("../.adf/META-INF/adf-config.xml");
            // store definition in the above
            task.setMdsStoreName("mstore-usage_1");
            // the namespace (which corresponds to the child folder of soa-seed
            task.setNamespace("soa/configuration/default");
            // the source directory to start at
            task.setSourceBaseFile("./soa-seed");
        } else 
        {
            task.setAdfConfigLocation(args[0]);
            task.setMdsStoreName(args[1]);
            task.setNamespace(args[2]);
            task.setSourceBaseFile(args[3]);  
        }

        // execute the task
        task.execute();        
    }
    
    /**
     * Executes the transfer from the mds into the locally configured one 
     * under soa-seed
     * @throws BuildException in case something goes wrong during the transfer
     */
    @Override
    public void execute() throws BuildException 
    {
        System.out.println("Transferring from remote to local mds : " + 
                           getCommand());
        try 
        {
            
            File sourceBaseMdsWithNs = 
                new File (getSourceBaseFile() + File.separator + 
                          getNamespace());
            // if it's not there we go and create it :)
            if (!sourceBaseMdsWithNs.exists()) 
            {
                System.out.println("Created local dir: " + 
                    sourceBaseMdsWithNs.getAbsolutePath() + ", success? " +                                   
                    sourceBaseMdsWithNs.mkdirs());
            }
            
            // get a handle on the remote mds 
            MDSInstance mdsSourceInstance = null;
            if (STR_EXPORT.equals(getCommand()))
                mdsSourceInstance = 
                    getTargetMdsInstanceByAdfConfig(getMdsStoreName());
            else 
                mdsSourceInstance = createSourceMDSInstanceFromFileSystem(
                    new File(getSourceBaseFile()));
            
            System.out.println("Got source (remote) mds-instance: " + 
                               mdsSourceInstance.getName() + " from " + 
                               new File(getAdfConfigLocation()).
                                    getAbsolutePath());
            // setup a transfer                        
            MDSTransfer srcTransfer =
                MDSTransfer.getInstance(mdsSourceInstance);

            // create a list of filepatterns to include
            List<String> srcTransferList = new ArrayList<String>();
            // we only include the ones off the namespace 
            String searchpattern = "/" + getNamespace() + "/" + 
                                   getOptionalConfigFileName();
            setInclusionPattern(searchpattern);
            System.out.println("Search criteria: " + searchpattern);
            srcTransferList.add(getInclusionPattern());
        
            // create a unit transfer list for mds (which is treated as an
            // atomic unit of work
            TransferUnitList srcTxList = 
                TransferUnitList.create(mdsSourceInstance, 
                                        srcTransferList, null, true, true);
        
            java.util.Iterator srcIto = srcTxList.iterator();
            // check if we have found anything on the remote side -
            // if someone tries to run this against the local jdev mds - 
            // the iterator will be empty
            if (srcTxList.isEmpty()) 
            {
                System.out.println("Nothing found on remote mds side, with " + 
                                   "query: " + searchpattern);
                return;
            }

            while (srcIto.hasNext()) 
            {
                System.out.println("Remote item: " + 
                    ((TransferUnit) srcIto.next()).getDocumentName().
                                   getAbsoluteName());
            }        

            // create a local mds connection into the filesystem
            MDSInstance targetMdsInstance = null;
            if (STR_EXPORT.equals(getCommand()))
                targetMdsInstance = createSourceMDSInstanceFromFileSystem(
                    new File(getSourceBaseFile()));
            else
                targetMdsInstance = 
                    getTargetMdsInstanceByAdfConfig(getMdsStoreName());
                    
            System.out.println("Now transferring ..");
            List tgtTxList = srcTransfer.transfer(targetMdsInstance,
                                 srcTxList, TransferType.IMPORT, true);
            if (tgtTxList != null) 
            {
               System.out.println("Transferred size = " + tgtTxList.size());
                for (int i = 0; i < tgtTxList.size(); i++) 
                {
                    TransferUnit item =
                        ((InternalTUnitResult)tgtTxList.get(i)).getTransferUnit();
                    System.out.println(" imported: " + 
                        item.getDocumentName().getAbsoluteName());
                }
            } 
            System.out.println("Remote namespace: " + getNamespace() + 
                               " transferred to: " + 
                               sourceBaseMdsWithNs.getAbsolutePath());
        } catch (Exception mdsTransferEx) 
        {
            mdsTransferEx.printStackTrace();
            BuildException buildEx =
                new BuildException(mdsTransferEx.getMessage());    
            buildEx.initCause(mdsTransferEx);
            throw buildEx;
        }
    }

    public String getOptionalConfigFileName() 
    {
        String filename = System.getProperty("mds.fileName");
        return (filename != null || filename.trim().length() == 0 ? 
            filename : "**");
    }

    public String getCommand() 
    {
        String command = System.getProperty("mds.command");
        return (STR_EXPORT.equals(command) ? STR_EXPORT : STR_IMPORT);
    }

}
