package me.fly.core.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootMutationResolver implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(RootMutationResolver.class);

    public String tell(String message) {
        logger.info(message);
        return message;
    }
}
