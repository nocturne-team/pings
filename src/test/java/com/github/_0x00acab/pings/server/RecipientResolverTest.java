package com.github._0x00acab.pings.server;

import java.util.List;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipientResolverTest {
    private static final String OVERWORLD = "overworld";
    private static final String NETHER = "nether";
    private static final TeamId RED = new TeamId("red");
    private static final TeamId BLUE = new TeamId("blue");

    private static RecipientResolver.Candidate<String> player(String name, TeamId team, String dimension, double x) {
        return new RecipientResolver.Candidate<>(name, team, dimension, new Vec3(x, 64, 0));
    }

    @Test
    void teamSenderReachesWholeTeamAtAnyDistance() {
        var sender = player("alice", RED, OVERWORLD, 0);
        var online = List.of(sender, player("bob", RED, OVERWORLD, 10_000), player("carol", BLUE, OVERWORLD, 1), player("dave", null, OVERWORLD, 1));

        assertEquals(List.of("alice", "bob"), RecipientResolver.resolve(sender, online, 128));
    }

    @Test
    void teamlessSenderReachesEveryoneInsideRadius() {
        var sender = player("alice", null, OVERWORLD, 0);
        var online = List.of(sender, player("bob", null, OVERWORLD, 100), player("carol", BLUE, OVERWORLD, 50), player("dave", null, OVERWORLD, 200));

        assertEquals(List.of("alice", "bob", "carol"), RecipientResolver.resolve(sender, online, 128));
    }

    @Test
    void radiusBoundaryIsInclusive() {
        var sender = player("alice", null, OVERWORLD, 0);
        var online = List.of(sender, player("bob", null, OVERWORLD, 128));

        assertEquals(List.of("alice", "bob"), RecipientResolver.resolve(sender, online, 128));
    }

    @Test
    void otherDimensionsAreExcludedInBothModes() {
        var teamSender = player("alice", RED, OVERWORLD, 0);
        var teamOnline = List.of(teamSender, player("bob", RED, NETHER, 0));
        assertEquals(List.of("alice"), RecipientResolver.resolve(teamSender, teamOnline, 128));

        var soloSender = player("alice", null, OVERWORLD, 0);
        var soloOnline = List.of(soloSender, player("bob", null, NETHER, 0));
        assertEquals(List.of("alice"), RecipientResolver.resolve(soloSender, soloOnline, 128));
    }

    @Test
    void senderIsIncludedEvenWhenMissingFromOnlineList() {
        var sender = player("alice", RED, OVERWORLD, 0);

        assertEquals(List.of("alice"), RecipientResolver.resolve(sender, List.of(), 128));
    }
}
