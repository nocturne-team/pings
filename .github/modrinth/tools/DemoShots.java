package com.github._0x00acab.callouts;

import com.github._0x00acab.callouts.client.PingClient;
import com.github._0x00acab.callouts.client.PingWheelScreen;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.sdl.SDLMouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TEMPORARY: takes the Modrinth page screenshots. Copied in by tools/demo_patch.py, never commit.
public final class DemoShots {
	private static final Logger LOG = LoggerFactory.getLogger("callouts-demo");
	private static final int PINK = 0xFFF13580, BLUE = 0xFF4D8EF0, GOLD = 0xFFE8B83C, GREEN = 0xFF6CC04A;
	private static final UUID KAI = UUID.fromString("a81b5c3d-1e2f-4a6b-8c9d-0e1f2a3b4c5d");
	private static final UUID SAM = UUID.fromString("5d4c3b2a-1f0e-4d9c-8b7a-6f5e4d3c2b1a");

	private static Minecraft mc;
	private static MinecraftServer server;

	public static void start() {
		Thread thread = new Thread(DemoShots::run, "callouts-demo");
		thread.setDaemon(true);
		thread.start();
	}

	private static void run() {
		try {
			mc = Minecraft.getInstance();
			while (mc.level == null || mc.player == null) sleep(500);
			sleep(3000);
			server = mc.getSingleplayerServer();
			BlockPos origin = onServer(() -> {
				var level = server.overworld();
				var found = level.findClosestBiome3d(b -> b.is(Biomes.PLAINS), mc.player.blockPosition(), 6400, 32, 64);
				return found == null ? mc.player.blockPosition() : found.getFirst();
			});
			ox = origin.getX();
			oz = origin.getZ();
			cmd("gamerule advance_time false", "gamerule advance_weather false", "gamerule send_command_feedback false",
				"gamerule spawn_mobs false", "gamerule block_drops false", "gamerule entity_drops false", "difficulty easy", "time set 1000", "weather clear", "gamemode spectator @p",
				"tp @p " + ox + " 200 " + oz);
			sleep(6000);
			yaw = openestYaw();
			y = height(ox, oz) + RAISE;
			LOG.info("DEMO origin {} {} {} yaw {}", ox, y, oz, yaw);

			// Clear a flat patch, then a stone brick wall ahead with a husk, ore and Kai behind it.
			cmd("kill @e[type=!minecraft:player,distance=..80]",
				fill(-14, 0, -18, 14, 9, 16, "air"),
				fill(-14, -RAISE - 4, -18, 14, -2, 16, "dirt replace air"),
				fill(-14, -RAISE - 4, -18, 14, -2, 16, "dirt replace water"),
				fill(-14, -1, -18, 14, -1, 16, "grass_block"),
				fill(-7, 0, 4, 7, 3, 4, "stone_bricks"),
				fill(-7, 4, 4, 7, 4, 4, "stone_brick_slab"),
				fill(4, 0, 8, 4, 0, 8, "iron_ore"),
				fill(5, 0, 8, 5, 0, 8, "coal_ore"),
				"summon husk " + at(-3, 9) + " {NoAI:1b,PersistenceRequired:1b,Silent:1b,Rotation:[" + (yaw + 180) + "f,0f]}",
				mannequin(KAI, "Kai", 2, 12, yaw + 180, "iron_pickaxe"),
				mannequin(SAM, "Sam", -5, 1, yaw + 150, "iron_sword"),
				"tp @p " + at(0, -7) + " " + yaw + " -4");
			sleep(4000);

			int husk = entity("husk");
			int kai = entity("mannequin", "Kai");
			int sam = entity("mannequin", "Sam");
			// 1: through walls, plus an off-screen ping behind you
			client(() -> {
				reset();
				PingClient.TRACKER.add(Vec3.ZERO, husk, PingCategory.DANGER, GOLD);
				PingClient.TRACKER.add(Vec3.ZERO, kai, PingCategory.COME_HERE, BLUE);
				PingClient.TRACKER.add(pos(4, 8, 1), PingRequestPayload.NO_ENTITY, PingCategory.MINING, BLUE);
				PingClient.TRACKER.add(pos(-6, -20, 0.5), PingRequestPayload.NO_ENTITY, PingCategory.LOOK_HERE, GREEN);
			});
			sleep(1500);
			shot("walls");

			// 2: wall gone, open field ahead with pings at different distances
			cmd(fill(-7, 0, 4, 7, 4, 8, "air"), "kill @e[type=minecraft:husk]",
				"tp @e[type=minecraft:mannequin,name=Sam] " + at(5, 8) + " " + (yaw + 160) + " 0",
				"kill @e[type=minecraft:item]", "tp @e[type=minecraft:mannequin,name=Kai] " + at(-8, 13) + " " + (yaw + 200) + " 0",
				"tp @p " + at(0, -2) + " " + yaw + " 10");
			sleep(2500);
			client(() -> {
				reset();
				PingClient.TRACKER.add(Vec3.ZERO, sam, PingCategory.NEED_HELP, GOLD);
				PingClient.TRACKER.add(Vec3.ZERO, kai, PingCategory.MINING, BLUE);
				PingClient.TRACKER.add(groundAt(14, 32), PingRequestPayload.NO_ENTITY, PingCategory.LOOK_HERE, PINK);
				PingClient.TRACKER.add(groundAt(-22, 60), PingRequestPayload.NO_ENTITY, PingCategory.COME_HERE, BLUE);
				PingClient.TRACKER.add(groundAt(30, 110), PingRequestPayload.NO_ENTITY, PingCategory.DANGER, GREEN);
			});
			sleep(1500);
			shot("field");

			// 3: the wheel, hovering Danger (the top tile), without markers crowding the labels
			client(DemoShots::reset);
			client(() -> mc.gui.setScreen(new PingWheelScreen(PingClient.WHEEL_KEY, true)));
			sleep(800);
			client(() -> {
				var w = mc.getWindow();
				float scale = (float) w.getScreenWidth() / w.getWidth();
				SDLMouse.SDL_WarpMouseInWindow(w.handle(), w.getWidth() / 2f * scale, (w.getHeight() / 2f - 60 * w.getGuiScale()) * scale);
			});
			sleep(1200);
			shot("wheel");
			client(() -> mc.gui.setScreen(null));
			LOG.info("CALLOUTS-SHOT done");
		} catch (Exception e) {
			LOG.error("DEMO failed", e);
		}
	}

	// Scene coordinates: a is to the right of the camera, b is ahead of it.
	private static int ox, oz, y, yaw;
	// The scene sits on a plateau this many blocks above the ground, so the field shot looks out over the land.
	private static final int RAISE = 8;

	private static int wx(double a, double b) {
		double r = Math.toRadians(yaw);
		return (int) Math.floor(ox + 0.5 - Math.cos(r) * a - Math.sin(r) * b);
	}

	private static int wz(double a, double b) {
		double r = Math.toRadians(yaw);
		return (int) Math.floor(oz + 0.5 - Math.sin(r) * a + Math.cos(r) * b);
	}

	private static String at(double a, double b) {
		return (wx(a, b) + 0.5) + " " + y + " " + (wz(a, b) + 0.5);
	}

	private static Vec3 pos(double a, double b, double up) {
		return new Vec3(wx(a, b) + 0.5, y + up, wz(a, b) + 0.5);
	}

	private static Vec3 groundAt(double a, double b) {
		int x = wx(a, b), z = wz(a, b);
		return new Vec3(x + 0.5, height(x, z) + 0.5, z + 0.5);
	}

	private static int height(int x, int z) {
		return onServer(() -> server.overworld().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
	}

	// The direction with the lowest terrain ahead, so the field shot has a view.
	private static int openestYaw() {
		int best = 0, bestScore = Integer.MAX_VALUE, base = height(ox, oz);
		for (int candidate : new int[] {0, 90, 180, 270}) {
			yaw = candidate;
			int score = 0;
			for (int b = 15; b <= 120; b += 7) {
				for (int a = -30; a <= 30; a += 10) {
					score += Math.max(0, height(wx(a, b), wz(a, b)) - base);
				}
			}
			LOG.info("DEMO yaw {} score {}", candidate, score);
			if (score < bestScore) {
				bestScore = score;
				best = candidate;
			}
		}
		return best;
	}

	// Drops leftover markers and the "Saved screenshot" chat lines.
	private static void reset() {
		PingClient.TRACKER.ensureLevel(null);
		PingClient.TRACKER.ensureLevel(mc.level);
		mc.gui.hud.getChat().clearMessages(false);
	}

	private static int entity(String type, String... name) {
		AtomicReference<Integer> id = new AtomicReference<>(PingRequestPayload.NO_ENTITY);
		client(() -> {
			List<Entity> all = mc.level.getEntities((Entity) null, new AABB(mc.player.blockPosition()).inflate(40),
				e -> e.getType().toShortString().equals(type) && (name.length == 0 || e.getName().getString().equals(name[0])));
			if (!all.isEmpty()) id.set(all.get(0).getId());
		});
		sleep(300);
		LOG.info("DEMO entity {} -> {}", type, id.get());
		return id.get();
	}

	private static String fill(int a1, int up1, int b1, int a2, int up2, int b2, String block) {
		return "fill " + wx(a1, b1) + " " + (y + up1) + " " + wz(a1, b1) + " " + wx(a2, b2) + " " + (y + up2) + " " + wz(a2, b2) + " " + block;
	}

	private static String ints(UUID u) {
		long m = u.getMostSignificantBits(), l = u.getLeastSignificantBits();
		return (int) (m >>> 32) + "," + (int) m + "," + (int) (l >>> 32) + "," + (int) l;
	}

	private static String mannequin(UUID uuid, String name, double a, double b, int facing, String item) {
		return "summon mannequin " + at(a, b) + " {UUID:[I;" + ints(uuid) + "],profile:{id:[I;" + ints(uuid)
			+ "]},hide_description:1b,CustomName:\"" + name + "\",CustomNameVisible:1b,Rotation:[" + facing
			+ "f,0f],Tags:[\"demo\"],equipment:{mainhand:{id:\"minecraft:" + item + "\",count:1}}}";
	}

	private static void cmd(String... commands) {
		server.execute(() -> {
			var source = server.createCommandSourceStack();
			for (String c : commands) server.getCommands().performPrefixedCommand(source, c);
		});
		sleep(500);
	}

	private static <T> T onServer(java.util.function.Supplier<T> task) {
		return server.submit(task).join();
	}

	private static void client(Runnable task) {
		mc.execute(task);
		sleep(100);
	}

	private static void shot(String tag) {
		client(() -> mc.gui.hud.getChat().clearMessages(false));
		sleep(300);
		mc.execute(() -> Screenshot.grab(mc, false));
		LOG.info("CALLOUTS-SHOT {}", tag);
		sleep(1500);
	}

	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
