package net.byAqua3.avaritia.test;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.block.BlockNeutroniumCompressor;
import net.byAqua3.avaritia.config.AvaritiaClientConfig;
import net.byAqua3.avaritia.config.AvaritiaClientConfig.Option;
import net.byAqua3.avaritia.gui.AvaritiaConfigScreen;
import net.byAqua3.avaritia.gui.GuiNeutroniumCompressor;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.byAqua3.avaritia.loader.AvaritiaTabs;
import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.byAqua3.avaritia.util.RecipeUtils;
import net.fabricmc.fabric.api.client.creativetab.v1.FabricCreativeModeInventoryScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;

public final class PortSmokeTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> checkEffectDepth());
		try (var world = context.worldBuilder().create()) {
			var server = world.getServer();
			server.runCommand("gamemode creative @a");
			server.runCommand("fill -4 99 -4 4 99 4 minecraft:white_concrete");
			server.runCommand("tp @a 0 100 0 0 0");
			server.runCommand("time set noon");
			server.runCommand("item replace entity @a weapon.mainhand with avaritia:infinity_sword");
			server.runCommand("item replace entity @a armor.head with avaritia:infinity_helmet");
			server.runCommand("item replace entity @a armor.chest with avaritia:infinity_chestplate");
			server.runCommand("item replace entity @a armor.legs with avaritia:infinity_leggings");
			server.runCommand("item replace entity @a armor.feet with avaritia:infinity_boots");
			context.waitFor(client -> client.player != null && client.player.getMainHandItem().is(AvaritiaItems.INFINITY_SWORD)
					&& client.player.getItemBySlot(EquipmentSlot.FEET).is(AvaritiaItems.INFINITY_BOOTS), 200);
			context.waitTicks(20);
			context.takeScreenshot("26_2_infinity_sword");
			server.runCommand("tp @a 0 108 0 0 0");
			server.runOnServer(instance -> {
				var player = instance.getPlayerList().getPlayers().getFirst();
				player.getAbilities().flying = true;
				player.onUpdateAbilities();
			});
			context.waitFor(client -> client.player.getAbilities().flying && client.player.getY() > 101, 100);
			context.runOnClient(client -> client.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
			context.waitTicks(10);
			context.takeScreenshot("26_2_infinity_armor");
			context.runOnClient(client -> {
				AvaritiaClientConfig.INSTANCE.set(Option.ARMOR_COSMIC, false);
				AvaritiaClientConfig.INSTANCE.set(Option.ARMOR_RAINBOW, false);
			});
			context.waitTicks(5);
			context.takeScreenshot("26_2_infinity_armor_base");
			context.runOnClient(client -> {
				if (!client.player.getAbilities().flying) throw new AssertionError("Armor screenshot must show flight");
				AvaritiaClientConfig.INSTANCE.reset();
			});
			context.setScreen(() -> new InventoryScreen(Minecraft.getInstance().player));
			context.waitTicks(10);
			context.takeScreenshot("26_2_inventory");
			context.setScreen(() -> {
				var client = Minecraft.getInstance();
				return new CreativeModeInventoryScreen(client.player, client.level.enabledFeatures(), true);
			});
			context.runOnClient(client -> {
				var screen = (FabricCreativeModeInventoryScreen) client.gui.screen();
				screen.switchToPage(screen.getPage(AvaritiaTabs.AVARITIA_TAB));
				screen.setSelectedTab(AvaritiaTabs.AVARITIA_TAB);
			});
			context.waitFor(client -> client.gui.screen() instanceof FabricCreativeModeInventoryScreen screen
					&& screen.getSelectedTab() == AvaritiaTabs.AVARITIA_TAB, 100);
			context.getInput().setCursorPos(0, 0);
			context.runOnClient(client -> AvaritiaClientConfig.INSTANCE.set(Option.ITEM_PULSE, false));
			context.waitTicks(10);
			context.takeScreenshot("26_2_creative");
			context.runOnClient(client -> AvaritiaClientConfig.INSTANCE.set(Option.ITEM_HALO, false));
			context.waitTicks(5);
			context.takeScreenshot("26_2_creative_base");
			context.runOnClient(client -> AvaritiaClientConfig.INSTANCE.reset());
			server.runCommand("tp @a 0 100 0 0 0");
			context.setScreen(() -> new AvaritiaConfigScreen(null));
			context.waitForScreen(AvaritiaConfigScreen.class);
			context.runOnClient(client -> client.gui.screen().onClose());
			context.waitFor(client -> client.gui.screen() == null, 100);
			var position = new BlockPos(1, 100, 0);
			server.runOnServer(instance -> {
				var level = instance.overworld();
				if (RecipeUtils.getCompressorRecipes(level).isEmpty()) {
					throw new AssertionError("No Avaritia compressor recipes loaded");
				}
				for (String advancement : new String[] { "endest_pearl", "infinity_bow", "skullfire_sword" }) {
					if (instance.getAdvancements().get(Avaritia.id("recipes/" + advancement)) == null) {
						throw new AssertionError("Missing Avaritia advancement: " + advancement);
					}
				}
				var compressor = AvaritiaBlocks.COMPRESSOR;
				level.setBlockAndUpdate(position, compressor.defaultBlockState());
			});
			context.waitFor(client -> client.level != null && client.level.getBlockEntity(position) instanceof TileNeutroniumCompressor, 200);
			server.runOnServer(instance -> {
				var level = instance.overworld();
				var player = instance.getPlayerList().getPlayers().getFirst();
				var machine = (BlockNeutroniumCompressor) AvaritiaBlocks.COMPRESSOR;
				machine.useWithoutItem(level.getBlockState(position), level, position, player, null);
			});
			context.waitForScreen(GuiNeutroniumCompressor.class);
			context.waitTicks(10);
			context.takeScreenshot("26_2_compressor");
		}
	}

	private static void checkEffectDepth() {
		var parameters = new AvaritiaShaders.Parameters(0, 0, 0, 1, 1);
		var eyes = Avaritia.id("textures/models/armor/infinity_armor_eyes.png");
		var nearOrEqual = DepthStencilState.DEFAULT.depthTest();
		checkDepth(AvaritiaRenderTypes.cosmic(parameters, true), nearOrEqual, false);
		checkDepth(AvaritiaRenderTypes.glow(eyes), nearOrEqual, false);
		checkDepth(AvaritiaRenderTypes.wingGlow(eyes), nearOrEqual, false);
		checkDepth(AvaritiaRenderTypes.halo(TextureAtlas.LOCATION_ITEMS), nearOrEqual, false);
		checkDepth(AvaritiaRenderTypes.cosmic(parameters, false), CompareOp.EQUAL, true);
	}

	private static void checkDepth(RenderType type, CompareOp comparison, boolean writeDepth) {
		var state = type.pipeline().getDepthStencilState();
		if (state.depthTest() != comparison || state.writeDepth() != writeDepth) {
			throw new AssertionError(type + " expected depth " + comparison + ", write=" + writeDepth + "; got " + state);
		}
	}
}
