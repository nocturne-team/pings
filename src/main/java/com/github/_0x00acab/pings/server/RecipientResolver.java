package com.github._0x00acab.pings.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.phys.Vec3;

public final class RecipientResolver {
    public record Candidate<P>(P player, TeamId team, String dimension, Vec3 position) {}

    private RecipientResolver() {}

    public static <P> List<P> resolve(Candidate<P> sender, List<Candidate<P>> online, double noTeamRadius) {
        double radiusSqr = noTeamRadius * noTeamRadius;
        List<P> recipients = new ArrayList<>();
        boolean senderIncluded = false;
        for (Candidate<P> candidate : online) {
            if (!candidate.dimension().equals(sender.dimension())) {
                continue;
            }
            boolean isSender = Objects.equals(candidate.player(), sender.player());
            boolean reached = sender.team() != null
                ? sender.team().equals(candidate.team())
                : candidate.position().distanceToSqr(sender.position()) <= radiusSqr;
            if (isSender || reached) {
                recipients.add(candidate.player());
                senderIncluded |= isSender;
            }
        }
        if (!senderIncluded) {
            recipients.add(0, sender.player());
        }
        return recipients;
    }
}
