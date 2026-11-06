package net.byAqua3.avaritia.util;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Bridges block entity menus to Fabric's ExtendedMenuType: the client needs the
 * block position to rebuild the menu, so the provider supplies it as screen data.
 */
public final class MenuHelper {

	private MenuHelper() {
	}

	@FunctionalInterface
	public interface MenuFactory<T extends AbstractContainerMenu> {
		T create(int id, Inventory inventory, BlockPos pos);
	}

	public static <T extends AbstractContainerMenu> void open(Player player, BlockPos pos, Component title, MenuFactory<T> factory) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		ExtendedMenuProvider<BlockPos> provider = new ExtendedMenuProvider<>() {
			@Override
			public BlockPos getScreenOpeningData(ServerPlayer player) {
				return pos;
			}

			@Override
			public Component getDisplayName() {
				return title;
			}

			@Override
			public T createMenu(int id, Inventory inventory, Player ignored) {
				return factory.create(id, inventory, pos);
			}
		};

		serverPlayer.openMenu(provider);
	}

	public static MenuProvider simple(Component title, net.minecraft.world.inventory.MenuType<?> ignored) {
		return new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return title;
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				throw new UnsupportedOperationException();
			}
		};
	}
}
