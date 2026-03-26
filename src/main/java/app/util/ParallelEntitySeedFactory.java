package app.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@RequiredArgsConstructor
// TODO: add saving to factory with appropriate logging
public class ParallelEntitySeedFactory<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelEntitySeedFactory.class);
    private final Class<T> entityClass;
    private final Function<Integer, T> createNewInstanceFunction;

    public List<T> createEntities(int n) {
        LOGGER.info("Creating {} {} instances...", n, entityClass.getSimpleName());
        List<T> entityList = IntStream.rangeClosed(1, n)
                .parallel()
                .mapToObj(this::getNextEntity)
                .toList();
        LOGGER.info("{} instances created...", entityClass.getSimpleName());
        return entityList;
    }

    private T getNextEntity(int i) {
        return createNewInstanceFunction.apply(i);
    }
}
