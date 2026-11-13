package com.misterd.encasedfans.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class EFConfig {
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;
    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<Server, ModConfigSpec> server = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = server.getLeft();
        SERVER_SPEC = server.getRight();
        Pair<Client, ModConfigSpec> client = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
    }

    public static class Server {
        public final ModConfigSpec.IntValue ticksPerBatch;
        public final ModConfigSpec.IntValue batchSize;
        public final ModConfigSpec.IntValue processInterval;
        public final ModConfigSpec.IntValue processingParticles;

        public final ModConfigSpec.IntValue minimumRange;
        public final ModConfigSpec.IntValue airflowRefreshInterval;
        public final ModConfigSpec.DoubleValue pushAcceleration;
        public final ModConfigSpec.DoubleValue maxFlowSpeed;
        public final ModConfigSpec.DoubleValue pushFalloff;

        public final ModConfigSpec.IntValue igniteTicks;
        public final ModConfigSpec.IntValue darknessTicks;

        Server(ModConfigSpec.Builder builder) {
            builder.push("processing");
            ticksPerBatch = builder
                    .comment("Ticks needed to process each started batch of items in a stack.")
                    .defineInRange("ticksPerBatch", 100, 1, 72000);
            batchSize = builder
                    .comment("Number of items per batch. A stack takes ticksPerBatch for every started batch.")
                    .defineInRange("batchSize", 16, 1, 99);
            processInterval = builder
                    .comment("How often, in ticks, a fan checks the items in its airflow.")
                    .defineInRange("processInterval", 5, 1, 100);
            processingParticles = builder
                    .comment("Smoke particles shown on an item each time it is checked while processing. 0 disables them.")
                    .defineInRange("processingParticles", 3, 0, 20);
            builder.pop();

            builder.push("airflow");
            minimumRange = builder
                    .comment("Airflow range in blocks for any redstone signal at or below this value. Stronger signals reach further, up to 15.")
                    .defineInRange("minimumRange", 3, 1, 15);
            airflowRefreshInterval = builder
                    .comment("How often, in ticks, a fan re-checks the blocks in its airflow path.")
                    .defineInRange("airflowRefreshInterval", 10, 1, 100);
            pushAcceleration = builder
                    .comment("Acceleration applied to entities in the airflow each tick, in blocks per tick squared.")
                    .defineInRange("pushAcceleration", 0.05, 0.0, 1.0);
            maxFlowSpeed = builder
                    .comment("Top speed the airflow pushes entities to, in blocks per tick.")
                    .defineInRange("maxFlowSpeed", 0.1, 0.0, 2.0);
            pushFalloff = builder
                    .comment("How much weaker the push is at the end of the range. 0 is no falloff, 1 fades to nothing.")
                    .defineInRange("pushFalloff", 0.5, 0.0, 1.0);
            builder.pop();

            builder.push("effects");
            igniteTicks = builder
                    .comment("Ticks entities stay on fire after airflow from fire, campfires or lava.")
                    .defineInRange("igniteTicks", 120, 0, 6000);
            darknessTicks = builder
                    .comment("Ticks of Darkness given to entities in airflow from soul fire or soul campfires.")
                    .defineInRange("darknessTicks", 60, 0, 6000);
            builder.pop();
        }
    }

    public static class Client {
        public final ModConfigSpec.DoubleValue spinBaseSpeed;
        public final ModConfigSpec.DoubleValue spinSpeedPerPower;
        public final ModConfigSpec.DoubleValue spinEasing;

        public final ModConfigSpec.DoubleValue particleChance;
        public final ModConfigSpec.IntValue blocksPerParticle;

        Client(ModConfigSpec.Builder builder) {
            builder.push("propeller");
            spinBaseSpeed = builder
                    .comment("Propeller speed in degrees per tick at any redstone signal, before adding spinSpeedPerPower.")
                    .defineInRange("spinBaseSpeed", 4.0, 0.0, 90.0);
            spinSpeedPerPower = builder
                    .comment("Extra propeller speed in degrees per tick for each level of redstone signal.")
                    .defineInRange("spinSpeedPerPower", 2.0, 0.0, 30.0);
            spinEasing = builder
                    .comment("How quickly the propeller speeds up and slows down. 1 changes speed instantly.")
                    .defineInRange("spinEasing", 0.2, 0.01, 1.0);
            builder.pop();

            builder.push("particles");
            particleChance = builder
                    .comment("Chance per attempt to spawn an airflow particle. 0 disables airflow particles.")
                    .defineInRange("particleChance", 0.5, 0.0, 1.0);
            blocksPerParticle = builder
                    .comment("One particle attempt is made each tick for every this many blocks of airflow.")
                    .defineInRange("blocksPerParticle", 4, 1, 15);
            builder.pop();
        }
    }
}