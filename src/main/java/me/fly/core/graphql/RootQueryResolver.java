package me.fly.core.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

public class RootQueryResolver implements GraphQLQueryResolver {

    public String ping() {
        return "pong";
    }
}
