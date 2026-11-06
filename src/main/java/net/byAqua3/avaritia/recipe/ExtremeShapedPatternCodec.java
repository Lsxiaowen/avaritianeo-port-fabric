package net.byAqua3.avaritia.recipe;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * The vanilla pattern codec rejects anything larger than 3x3, but the extreme
 * crafting table needs a 9x9 grid, so this mirrors the vanilla schema without
 * the size limits.
 */
final class ExtremeShapedPatternCodec {

	private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
		if (pattern.isEmpty()) {
			return DataResult.error(() -> "Pattern must not be empty");
		}
		if (pattern.size() > 9) {
			return DataResult.error(() -> "Pattern has more than 9 rows");
		}
		int width = pattern.getFirst().length();
		if (width == 0 || width > 9) {
			return DataResult.error(() -> "Pattern width must be between 1 and 9");
		}
		for (String row : pattern) {
			if (row.length() != width) {
				return DataResult.error(() -> "Pattern rows must have equal width");
			}
		}
		return DataResult.success(pattern);
	}, Function.identity());

	private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(symbol -> {
		if (symbol.length() != 1) {
			return DataResult.error(() -> "Recipe symbols must be one character");
		}
		if (" ".equals(symbol)) {
			return DataResult.error(() -> "Space is reserved for empty slots");
		}
		return DataResult.success(symbol.charAt(0));
	}, String::valueOf);

	private static final MapCodec<ShapedRecipePattern.Data> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC)
					.fieldOf("key")
					.forGetter(ShapedRecipePattern.Data::key),
			PATTERN_CODEC.fieldOf("pattern").forGetter(ShapedRecipePattern.Data::pattern))
			.apply(instance, ShapedRecipePattern.Data::new));

	static final MapCodec<ShapedRecipePattern> MAP_CODEC = DATA_CODEC.flatXmap(
			data -> unpack(data.key(), data.pattern()),
			pattern -> DataResult.error(() -> "Encoding an unpacked extreme pattern is unsupported"));

	private ExtremeShapedPatternCodec() {
	}

	private static DataResult<ShapedRecipePattern> unpack(Map<Character, Ingredient> key, List<String> pattern) {
		try {
			return DataResult.success(ShapedRecipePattern.of(key, pattern));
		} catch (IllegalArgumentException exception) {
			return DataResult.error(exception::getMessage);
		}
	}
}
