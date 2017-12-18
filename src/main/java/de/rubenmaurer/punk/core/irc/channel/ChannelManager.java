package de.rubenmaurer.punk.core.irc.channel;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import de.rubenmaurer.punk.core.irc.messages.Join;
import de.rubenmaurer.punk.core.reporter.Report;
import scala.Option;

import static de.rubenmaurer.punk.core.Guardian.reporter;

/**
 * Actor for managing all channels.
 * The channel feature is a EXPERIMENTAL feature and still WIP.
 *
 * @author Ruben Maurer
 * @version 1.0
 * @since 1.0
 */
public class ChannelManager extends AbstractActor {

    /**
     * Get needed props for creating a new actor.
     *
     * @return the props for creating
     */
    public static Props props() {
        return Props.create(ChannelManager.class);
    }

    /**
     * User enters or create a new channel (and enters then).
     *
     * @param joinMessage the join message
     */
    private void join(Join joinMessage) {
        String channel = getChannelName(joinMessage.getChannel());
        final Option<ActorRef> child = context().child(channel);
        if (!child.isDefined()) {
            context().actorOf(ChannelHandler.props(), channel).tell(joinMessage, sender());
            return;
        }

        child.get().tell(joinMessage, self());
    }

    /**
     * Get the name for the channel actor.
     *
     * @param base the original string
     * @return the new actor name
     */
    private String getChannelName(String base) {
        if (base.startsWith("#")) return base.substring(1);
        return base;
    }

    /**
     * Gets fired before startup.
     */
    @Override
    public void preStart() {
        reporter().tell(Report.create(Report.Type.ONLINE), self());
    }

    /**
     * Handles incoming messages and process them.
     *
     * @return a receiveBuilder object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Join.class, this::join)
                .build();
    }
}
