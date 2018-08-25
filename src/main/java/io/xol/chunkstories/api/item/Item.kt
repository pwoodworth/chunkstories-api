//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.chunkstories.api.item

import io.xol.chunkstories.api.dsl.DynamicRepresentationBuildingContext
import io.xol.chunkstories.api.entity.Controller
import io.xol.chunkstories.api.entity.Entity
import io.xol.chunkstories.api.input.Input
import io.xol.chunkstories.api.item.inventory.ItemPile
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

open class Item(val definition: ItemDeclaration<out Item>) {
    var itemPile: ItemPile? = null

    open val name: String
        get() = internalName

    val internalName: String
        get() = definition.name


    open val representation: ItemRepresentationBuilder<out Item>
        get() = definition.representation

    /** Should be called when the owner has this item selected
     *
     * @param owner
     */
    open fun tickInHand(owner: Entity, itemPile: ItemPile) {

    }

    /** Handles some input from the user
     *
     * @param user
     * @param pile
     * @param input
     * @return false if the item doesn't handle the input, true if it does
     */
    open fun onControllerInput(owner: Entity, pile: ItemPile, input: Input, controller: Controller): Boolean {
        return false
    }

    /** Use : determine if two stacks can be merged together, should be overriden
     * when items have extra info.
     *
     * @return Returns true if the two items are similar and can share a stack
     * without loosing information.
     */
    open fun canStackWith(item: Item): Boolean {
        return definition == item.definition
    }

    open fun stack(other: Item) = Unit
    open fun unstack() = this.duplicate()

    open fun duplicate(): Item {
        return definition.newItem()
    }

    /** For Items not implementing a custom renderer, it just shows a dull icon and
     * thus require an icon texture.
     *
     * @return The full path to the image file.
     */
    open fun getTextureName(pile: ItemPile): String {
        return "./items/icons/$internalName.png"
    }

    /** Unsafe, called upon loading this item from a stream. If you do use it,
     * PLEASE ensure you remember how many bytes you read/write and be consistent,
     * else you break the savefile  */
    @Throws(IOException::class)
    open fun load(stream: DataInputStream) {
    }

    /** See load().  */
    @Throws(IOException::class)
    open fun save(stream: DataOutputStream) {
    }
}

/** SAM for building representations */
interface ItemRepresentationBuilder<T : Item> {
    fun build(context: ItemRepresentationBuildingContext<T>)
}

/** Extended context to provide the represented item as well */
interface ItemRepresentationBuildingContext<T : Item> : DynamicRepresentationBuildingContext {
    val item: T
}
