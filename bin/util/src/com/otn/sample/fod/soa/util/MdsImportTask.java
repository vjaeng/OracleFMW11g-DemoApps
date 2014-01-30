package com.otn.sample.fod.soa.util;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import oracle.mds.config.MDSConfig;
import oracle.mds.config.NamespaceConfig;
import oracle.mds.config.PConfig;
import oracle.mds.core.MDSInstance;
import oracle.mds.exception.MDSException;
import oracle.mds.internal.transfer.InternalTUnitResult;
import oracle.mds.naming.Namespace;
import oracle.mds.persistence.stores.file.FileMetadataStore;
import oracle.mds.transfer.MDSTransfer;
import oracle.mds.transfer.TransferType;
import oracle.mds.transfer.TransferUnit;
import oracle.mds.transfer.TransferUnitList;

import org.apache.tools.ant.BuildException;


/**
 * Ant task to import shared (abstract) wsdls and XSDs from the local app
 * $FOD_HOME/bin/soa-seed into the foreign MDS (apps namespace)
 * @author clemens utschig
 */
public class MdsImportTask extends org.apache.tools.ant.Task 
{
    // the location of adf-config.xml in this application
    private String mAdfConfigLocation = null;
    // the name of the mds store
    private String mMdsStoreName      = null;
    // the namespace (aka directory)
    private String mNamespace     = null;
    
    // the source base directory to create an mds on the fly
    private String mSourceBaseFile    = null;
    // inclusion pattern (= everything under the namespace)
    private String mInclusionPattern  = "/**";
    // validation only
    private boolean mValidateOnly = false;
    // do delete instead of transfer?
    private boolean mDoDelete = false;
    
    public static void main(String[] args) throws Exception 
    {
        MdsImportTask task = new MdsImportTask();
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
     * Create an mds connection off the local filesystem (basedir)
     * @param baseDir the directory to setup the local mds instance on
     * @return an MDS instance accessor
     * @throws MDSException
     */
    public final MDSInstance createSourceMDSInstanceFromFileSystem(File baseDir)
        throws MDSException 
    {   
        /*
         * src, being the application directory + source based file +
         * partition
         */
        System.out.println("Creating mds connection to " + 
                            getSourceBaseFile() + "/" + getNamespace());
        
        FileMetadataStore mdsStore =
            new FileMetadataStore(baseDir.getAbsolutePath());
        System.out.println(" --> " + baseDir.getAbsolutePath());
        
        NamespaceConfig srcNamespace =
            new NamespaceConfig(Namespace.create("/" + getNamespace()), 
                                mdsStore);
        
        PConfig pConfig =
            new PConfig(new NamespaceConfig[] { srcNamespace });
        MDSConfig mdsConfigSrc = new MDSConfig(null, pConfig, null);

        return MDSInstance.getOrCreateInstance("deploy_source_" +
                                               UUID.randomUUID(),
                                               mdsConfigSrc);
    }
    
    /**
     * Creates the target mds instance accessor from adf-config.xml
     * @param pStoreName the name of the store
     * @return an mds instance accessor
     * @throws Exception
     */
    public final MDSInstance getTargetMdsInstanceByAdfConfig(String pStoreName) 
        throws Exception
    {
        /*
         * create the target mds instance based on the adf-config.xml
         */
        URL configUrl = new File(getAdfConfigLocation()).toURL();
        MDSConfig mdsConfig = new MDSConfig(configUrl);
        
        return MDSInstance.getOrCreateInstance(pStoreName, mdsConfig);        
    }

    /**
     * Executes the transfer between the local (filesystem, app based) mds and 
     * the remote mds configured in adf-config.xml
     * @throws BuildException in case something goes wrong during the transfer
     */
    @Override
    public void execute() throws BuildException 
    {
        if (isValidateOnly())
            System.out.println("Running mds configuration validation");
        else if (!isDoDelete())
            System.out.println("Starting local filesystem import into mds ..");
        else 
            System.out.println("Deleting shared metadata from remote mds ..");
        
        try 
        {
            MDSInstance mdsTargetInstance = 
                getTargetMdsInstanceByAdfConfig(getMdsStoreName());
            System.out.println("Got target mds-instance: " + 
                               mdsTargetInstance.getName() + " from " + 
                               new File(getAdfConfigLocation()).
                                    getAbsolutePath());
            
            if (isValidateOnly()) 
            {
                System.out.println(" Connection tests to foreign mds succeeded");
                return;
            }
            
            // local application directory source mds
            MDSInstance srcMds = 
                createSourceMDSInstanceFromFileSystem(
                    new File(getSourceBaseFile()));
            
            MDSTransfer srcTransfer = MDSTransfer.getInstance(srcMds);

            // create a list of filepatterns to include
            List<String> srcTransferList = new ArrayList<String>();
            srcTransferList.add(getInclusionPattern());
    
            // create a unit transfer list for mds (which is treated as an
            // atomic unit of work
            TransferUnitList srcTxList = 
                TransferUnitList.create(srcMds, srcTransferList, null, true, true);
    
            java.util.Iterator srcIto = srcTxList.iterator();
            // debug
            while (srcIto.hasNext()) 
            {
                System.out.println("Local item: " + 
                    ((TransferUnit) srcIto.next()).getDocumentName().
                                   getAbsoluteName());
            }        
    
            List tgtTxList = null;
            if (!isDoDelete()) 
            {
                // transfer and get the list of transfered objects back
                System.out.println("Now transferring ..");
                tgtTxList = srcTransfer.transfer(mdsTargetInstance,
                                     srcTxList, TransferType.IMPORT, true);
            } else 
            {
                System.out.println("Now deleting ..");
                MDSTransfer deleteTransfer = 
                    MDSTransfer.getInstance(mdsTargetInstance);
                tgtTxList = deleteTransfer.delete(srcTxList, true);
            }
            if (tgtTxList != null) 
            {
                String command = "Transferred ";
                if (isDoDelete())
                    command = "Deleted ";
                System.out.println(command + " size = " + tgtTxList.size());
                for (int i = 0; i < tgtTxList.size(); i++) 
                {
                    TransferUnit item =
                        ((InternalTUnitResult)tgtTxList.get(i)).getTransferUnit();
                    System.out.println(command + " - " + 
                                       item.getDocumentName().getAbsoluteName());
                }
            } 
            
        } catch (Exception mdsTransferEx) 
        {
            mdsTransferEx.printStackTrace();
            BuildException buildEx =
                new BuildException(mdsTransferEx.getMessage());    
            buildEx.initCause(mdsTransferEx);
            throw buildEx;
        }
    }

/*
 * ANT injected ..
 */
    public final void setAdfConfigLocation(String mAdfConfigLocation) {
        this.mAdfConfigLocation = mAdfConfigLocation;
    }

    public final String getAdfConfigLocation() {
        return mAdfConfigLocation;
    }

    public final void setMdsStoreName(String mMdsStoreName) {
        this.mMdsStoreName = mMdsStoreName;
    }

    public final String getMdsStoreName() {
        return mMdsStoreName;
    }

    public final void setSourceBaseFile(String mSourceBaseFile) {
        this.mSourceBaseFile = mSourceBaseFile;
    }

    public final String getSourceBaseFile() {
        return mSourceBaseFile;
    }

    public final void setInclusionPattern(String mInclusionPattern) {
        this.mInclusionPattern = mInclusionPattern;
    }

    public final String getInclusionPattern() {
        return mInclusionPattern;
    }

    public final void setNamespace(String mNamespace) {
        this.mNamespace = mNamespace;
    }

    public final String getNamespace() {
        return mNamespace;
    }

    public void setValidateOnly(boolean mValidateOnly) {
        this.mValidateOnly = mValidateOnly;
    }

    public boolean isValidateOnly() {
        return mValidateOnly;
    }

    public void setDoDelete(boolean mDoDelete) {
        this.mDoDelete = mDoDelete;
    }

    public boolean isDoDelete() {
        return mDoDelete;
    }
}
