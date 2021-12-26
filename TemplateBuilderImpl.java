package com.replace.replace.api.template;

import com.replace.replace.api.environment.Environment;
import com.replace.replace.configuration.environment.Variable;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Service
public class TemplateBuilderImpl implements TemplateBuilder {

    protected final static String         ENCODING = "UTF-8";
    protected              VelocityEngine velocityEngine;
    protected              Environment    environment;


    public TemplateBuilderImpl(
            final Environment environment
    ) {
        this.environment    = environment;
        this.velocityEngine = new VelocityEngine();
        this.velocityEngine.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        this.velocityEngine.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
        this.velocityEngine.init();
    }


    @Override
    public String build( final String name ) {
        assert name != null && !name.isBlank() : "variable name should not be null or blank";

        return this.build( name, Map.of() );
    }


    @Override
    public String build( final String name, final Map< String, Object > parameters ) {
        assert name != null && !name.isBlank() : "variable name should not be null or blank";

        final Template template = this.velocityEngine.getTemplate( this.getPath( name ), TemplateBuilderImpl.ENCODING );

        final VelocityContext velocityContext = new VelocityContext();

        if ( parameters != null ) {
            parameters.forEach( velocityContext::put );
        }

        return this.getContent( velocityContext, template );
    }


    protected String getContent( final VelocityContext velocityContext, final Template template ) {
        final StringWriter content = new StringWriter();

        template.merge( velocityContext, content );

        return content.toString();
    }


    protected String getPath( final String name ) {

        return this.environment.getEnv( Variable.BASE_TEMPLATE_PATH ) +
                name +
                ".vm";
    }
}
