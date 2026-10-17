package pageqwq.colorbmc.util.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ColorActionTrigger extends SimpleCriterionTrigger<ColorActionTrigger.TriggerInstance> {
    public static final String TYPE_DYE = "dye";
    public static final String TYPE_CLEAN = "clean";

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, String type, int color, int wasColor, int count, boolean thrown, boolean changed) {
        this.trigger(player, instance -> instance.matches(type, color, wasColor, count, thrown, changed));
    }

    public record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        Optional<String> type,
        Optional<Integer> color,
        Optional<Integer> wasColor,
        Optional<Integer> minCount,
        Optional<Boolean> thrown,
        Optional<Boolean> changed
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public boolean matches(String firedType, int firedColor, int firedWasColor, int firedCount, boolean firedThrown, boolean firedChanged) {
            if (this.type.isPresent() && !this.type.get().equals(firedType)) return false;
            if (this.wasColor.isPresent() && this.wasColor.get() != (firedWasColor & 0xFFFFFF)) return false;
            if (this.color.isPresent() && !colorMatches(this.color.get(), firedColor)) return false;
            if (this.minCount.isPresent() && firedCount < this.minCount.get()) return false;
            if (this.thrown.isPresent() && this.thrown.get() != firedThrown) return false;
            if (this.changed.isPresent() && this.changed.get() != firedChanged) return false;
            return true;
        }

        private static boolean colorMatches(int expected, int fired) {
            if (expected == (fired & 0xFFFFFF)) return true;
            int fr = (fired >> 16) & 0xFF, fg = (fired >> 8) & 0xFF, fb = fired & 0xFF;
            if (expected == 0xFF0000) return fr > fg && fr > fb && fr != 0;
            if (expected == 0x00FF00) return fg > fr && fg > fb && fg != 0;
            if (expected == 0x0000FF) return fb > fr && fb > fg && fb != 0;
            return false;
        }

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.STRING.optionalFieldOf("type").forGetter(TriggerInstance::type),
            Codec.INT.optionalFieldOf("color").forGetter(TriggerInstance::color),
            Codec.INT.optionalFieldOf("was_color").forGetter(TriggerInstance::wasColor),
            Codec.INT.optionalFieldOf("min_count").forGetter(TriggerInstance::minCount),
            Codec.BOOL.optionalFieldOf("thrown").forGetter(TriggerInstance::thrown),
            Codec.BOOL.optionalFieldOf("changed").forGetter(TriggerInstance::changed)
        ).apply(builder, TriggerInstance::new));
    }
}
