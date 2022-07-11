package org.mengyun.tcctransaction.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChannelGroupMap {

    private ConcurrentMap<String, ChannelGroup> channelGroup = new ConcurrentHashMap<>();

    public ChannelGroupMap() {

    }

    public void registerChannel(String key, Channel channel) {
        channelGroup.computeIfAbsent(key, key0 -> new DefaultChannelGroup(key0, GlobalEventExecutor.INSTANCE)).add(channel);
    }

    public Channel getChannel(String key) {
        Set<Channel> channels = channelGroup.get(key);
        if (channels != null && channels.size() > 0) {
            for (Channel channel : channels) {
                return channel;
            }
        }
        return null;
    }

    public Set<Channel> getAllChannels(String key) {
        return channelGroup.get(key);
    }
}
