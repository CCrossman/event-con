package me.fly;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLHttpServlet;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.fly.core.graphql.RootMutationResolver;
import me.fly.core.graphql.RootQueryResolver;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class Launcher extends Application<LauncherConfiguration> {
    public static void main(String[] args) throws Exception {
        new Launcher().run(args);
    }

    @Override
    public void initialize(Bootstrap<LauncherConfiguration> bootstrap) {
        // activate Liquibase
        bootstrap.addBundle(new MigrationsBundle<LauncherConfiguration>() {
            public DataSourceFactory getDataSourceFactory(LauncherConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        // expose GraphiQL at root
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "static"));

        // initialize Guice dependency injection
        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .build());
    }

    public void run(LauncherConfiguration configuration, Environment environment) throws Exception {
        // build the GraphQL servlet
        environment.servlets().addServlet("GraphQL", buildGraphQLServlet()).addMapping("/graphql");

        // get around 'multiple servlets map to path /*' error because our assets conflict with Jersey
        environment.jersey().setUrlPattern("/jersey/*");
    }

    private SimpleGraphQLHttpServlet buildGraphQLServlet() {
        return SimpleGraphQLHttpServlet.newBuilder(buildSchema())
                .build();
    }

    private GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                .file("graphql/core.graphql")
                .resolvers(
                        new RootQueryResolver(),
                        new RootMutationResolver()
                )
                .build()
                .makeExecutableSchema();
    }
}
