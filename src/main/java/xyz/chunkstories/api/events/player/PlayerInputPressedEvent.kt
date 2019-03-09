//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.events.player

import xyz.chunkstories.api.events.CancellableEvent
import xyz.chunkstories.api.events.EventListeners
import xyz.chunkstories.api.input.Input
import xyz.chunkstories.api.player.Player

class PlayerInputPressedEvent(player: Player, input: Input) : CancellableEvent() {

    override val listeners: EventListeners
        get() = listenersStatic

    // Specific event code

    var player: Player
        internal set
    var input: Input
        internal set

    init {
        this.player = player
        this.input = input
    }

    companion object {
        // Every event class has to have this

        var listenersStatic = EventListeners(PlayerInputPressedEvent::class.java)
            internal set
    }
}
