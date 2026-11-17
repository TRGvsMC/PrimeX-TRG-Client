/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * The PRIMEX TRG CLIENT runtime and subsystem coordinator.
 */
package net.aoba;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.aoba.api.IAddon;
import net.aoba.gui.GuiManager;
import net.aoba.gui.font.FontManager;
import net.aoba.managers.CombatManager;
import net.aoba.managers.CommandManager;
import net.aoba.managers.EntityManager;
import net.aoba.managers.EventManager;
import net.aoba.managers.ModuleManager;
import net.aoba.managers.SettingManager;
import net.aoba.managers.altmanager.AltManager;
import net.aoba.managers.macros.MacroManager;
import net.aoba.managers.proxymanager.ProxyManager;
import net.aoba.managers.rotation.RotationManager;
import net.aoba.mixin.interfaces.IMinecraft;
import net.aoba.module.Module;
import net.aoba.rendering.AbstractRenderer;
import net.aoba.rendering.Compositor;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.Renderer3D;
import net.aoba.rendering.shaders.ShaderManager;
import net.aoba.settings.friends.FriendsList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class AobaClient {
	public static final KeyMapping.Category AOBA_CATEGORY =
			new KeyMapping.Category(Identifier.fromNamespaceAndPath("aoba", "category"));

	public static Minecraft MC;
	public static IMinecraft IMC;

	// Systems
	public RotationManager rotationManager;
	public ModuleManager moduleManager;
	public CommandManager commandManager;
	public AltManager altManager;
	public ProxyManager proxyManager;
	public GuiManager guiManager;
	public FontManager fontManager;
	public CombatManager combatManager;
	public SettingManager settingManager;
	public FriendsList friendsList;
	public EventManager eventManager;
	public MacroManager macroManager;
	public EntityManager entityManager;
	public ShaderManager shaderManager;
	public Renderer2D render2D;
	public Renderer3D render3D;
	public Compositor compositor;

	public static List<IAddon> addons = new ArrayList<>();
	public static Logger LOGGER;

	/**
	 * Initializes PRIMEX TRG CLIENT and creates its subsystems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = Minecraft.getInstance();
		IMC = (IMinecraft) MC;
		LOGGER = LogUtils.getLogger();
	}

	/**
	 * Initializes systems and loads any assets.
	 */
	public void loadAssets() {
		LOGGER.info("[PRIMEX TRG] Starting Client");
		eventManager = new EventManager();

		// Register any addons.
		LogUtils.getLogger().info("[PRIMEX TRG] Starting addon initialization");
		for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("aoba",
				IAddon.class)) {
			IAddon addon = entrypoint.getEntrypoint();

			try {
				LOGGER.info("[PRIMEX TRG] Initializing addon: " + addon.getName());
				addon.onInitialize();
				LOGGER.info("[PRIMEX TRG] Addon initialized: " + addon.getName());
			} catch (Throwable e) {
				LOGGER.error("Error initializing addon: " + addon.getName(), e);
			}

			addons.add(addon);
		}

		LOGGER.info("[PRIMEX TRG] Reading Settings");
		settingManager = new SettingManager();

		LOGGER.info("[PRIMEX TRG] Reading Friends List");
		friendsList = new FriendsList();

		LOGGER.info("[PRIMEX TRG] Initializing Commands");
		commandManager = new CommandManager(addons);

		LOGGER.info("[PRIMEX TRG] Initializing Font Manager");
		fontManager = new FontManager();
		fontManager.Initialize();

		LOGGER.info("[PRIMEX TRG] Initializing Combat Manager");
		combatManager = new CombatManager();

		LOGGER.info("[PRIMEX TRG] Initializing Entity Manager");
		entityManager = new EntityManager();

		LOGGER.info("[PRIMEX TRG] Initializing Macro Manager");
		macroManager = new MacroManager();

		LOGGER.info("[PRIMEX TRG] Initializing Shader Effects");
		shaderManager = new ShaderManager();
		
		LOGGER.info("[PRIMEX TRG] Initializing Renderers");
		compositor = new Compositor();
		render2D = new Renderer2D();
		render3D = new Renderer3D();

		LOGGER.info("[PRIMEX TRG] Initializing Rotation Manager");
		rotationManager = new RotationManager();
		
		LOGGER.info("[PRIMEX TRG] Initializing Modules");
		moduleManager = new ModuleManager(addons);
		
		LOGGER.info("[PRIMEX TRG] Initializing GUI");
		guiManager = new GuiManager();
		guiManager.Initialize();

		LOGGER.info("[PRIMEX TRG] Loading Settings");
		SettingManager.loadGlobalSettings();
		SettingManager.loadSettings();

		LOGGER.info("[PRIMEX TRG] Initializing Alt Manager");
		altManager = new AltManager();

		LOGGER.info("[PRIMEX TRG] Initializing Proxy Manager");
		proxyManager = new ProxyManager();

		LOGGER.info("[PRIMEX TRG] Registering Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				endClient();
			} catch (Exception e) {
				LOGGER.error("[PRIMEX TRG] Error during shutdown: ", e);
			}
		}));

		LOGGER.info("[PRIMEX TRG] PRIMEX TRG CLIENT initialized and ready to play!");
	}

	/**
	 * Called when the client is shutting down. Saves persistent data.
	 */
	public void endClient() {
		LOGGER.info("[PRIMEX TRG] Shutting down");
		try {
			SettingManager.saveSettings();
			altManager.saveAlts();
			friendsList.save();
			macroManager.save();
			moduleManager.modules.forEach(Module::onDisable);
		} catch (Exception e) {
			LOGGER.error("[PRIMEX TRG] Error saving data", e);
		}

		try {
			if (render2D != null) render2D.close();
			if (render3D != null) render3D.close();
			if (compositor != null) compositor.close();
			AbstractRenderer.closeSharedResources();
		} catch (Exception e) {
			LOGGER.error("[PRIMEX TRG] Error releasing renderer resources", e);
		}
	}
}
