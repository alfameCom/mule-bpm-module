package com.alfame.esb.utils.flowable;

import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.spring.configurator.DefaultAutoDeploymentStrategy;
import org.springframework.core.io.Resource;

public class TenantAwareDefaultAutoDeploymentStrategy extends DefaultAutoDeploymentStrategy {

	private static final Log logger = LogFactory.getLog( TenantAwareDefaultAutoDeploymentStrategy.class );
	
	private TenantInfoHolder tenantInfoHolder;
	
	public TenantAwareDefaultAutoDeploymentStrategy( TenantInfoHolder tenantInfoHolder ) {
		super();
		
		this.tenantInfoHolder = tenantInfoHolder;
	}
	
	@Override
	public void deployResources(String deploymentNameHint, Resource[] resources, RepositoryService repositoryService ) {
		
		try {
            // Create a single deployment for all resources using the name hint as the literal name
            final DeploymentBuilder deploymentBuilder = repositoryService
            		.createDeployment()
            		.tenantId( tenantInfoHolder.getTenantId() )
            		.enableDuplicateFiltering()
            		.name(deploymentNameHint);

            for (final Resource resource : resources) {
                final String resourceName = determineResourceName(resource);
                if (resourceName.endsWith(".bar") || resourceName.endsWith(".zip") || resourceName.endsWith(".jar")) {
                    deploymentBuilder.addZipInputStream(new ZipInputStream(resource.getInputStream()));
                } else {
                    deploymentBuilder.addInputStream(resourceName, resource.getInputStream());
                }

            }

            deploymentBuilder.deploy();

        } catch (Exception e) {
            // Any exception should not stop the bootup of the engine
            logger.warn("Exception while autodeploying process definitions. "
                + "This exception can be ignored if the root cause indicates a unique constraint violation, "
                + "which is typically caused by two (or more) servers booting up at the exact same time and deploying the same definitions. ", e);
        }
		
	}

	
	
}
