package pageqwq.colorbmc.compat.continuity;

import me.pepperbell.continuity.api.client.CachingPredicates;
import me.pepperbell.continuity.api.client.CtmLoader;
import me.pepperbell.continuity.api.client.CtmLoaderRegistry;
import me.pepperbell.continuity.api.client.CtmProperties;
import me.pepperbell.continuity.api.client.QuadProcessor;
import me.pepperbell.continuity.client.processor.BaseCachingPredicates;
import me.pepperbell.continuity.client.processor.simple.CtmSpriteProvider;
import me.pepperbell.continuity.client.processor.simple.SimpleQuadProcessor;
import me.pepperbell.continuity.client.properties.BaseCtmProperties;
import me.pepperbell.continuity.client.properties.TileAmountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Optional Continuity integration for colored glass CTM. */
public final class ContinuityGlassCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("ColorfulBlocks-Continuity");

    private ContinuityGlassCompat() {
    }

    public static void register() {
        try {
            CtmProperties.Factory<ColorGlassCtmProperties> propertiesFactory =
                ColorGlassCtmProperties::new;
            CtmProperties.Factory<ColorGlassCtmProperties> wrappedFactory =
                TileAmountValidator.wrapFactory(
                    BaseCtmProperties.wrapFactory(propertiesFactory),
                    new TileAmountValidator.AtLeast<>(47)
                );

            CtmLoader<ColorGlassCtmProperties> loader = new CtmLoader<>() {
                @Override
                public CtmProperties.Factory<ColorGlassCtmProperties> getPropertiesFactory() {
                    return wrappedFactory;
                }

                @Override
                public QuadProcessor.Factory<ColorGlassCtmProperties> getProcessorFactory() {
                    return new SimpleQuadProcessor.Factory<>(new CtmSpriteProvider.Factory());
                }

                @Override
                public CachingPredicates.Factory<ColorGlassCtmProperties> getPredicatesFactory() {
                    return new BaseCachingPredicates.Factory<>(true);
                }
            };

            CtmLoaderRegistry.get().registerLoader("colorblockmc_glass", loader);
            LOGGER.info("Registered colored glass CTM");
        } catch (Throwable throwable) {
            LOGGER.warn("Failed to register colored glass CTM; connected glass disabled", throwable);
        }
    }
}
