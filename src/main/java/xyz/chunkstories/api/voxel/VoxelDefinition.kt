//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.voxel

import xyz.chunkstories.api.content.Content
import xyz.chunkstories.api.content.Definition
import xyz.chunkstories.api.item.Item
import xyz.chunkstories.api.item.ItemDefinition
import xyz.chunkstories.api.util.kotlin.initOnce
import java.lang.reflect.Constructor

/** A Voxel definition defines a voxel type, one that can be placed in game, and is assignated an ID. */
class VoxelDefinition(val store: Content.Voxels, name: String, properties: Map<String, String>) : Definition(name, properties) {
    /** When added to the game content, either by being loaded explicitly or programatically, will be set to an integer
     * value between 1 and 65535. Attempting to manually override/set this identifier yourself will result in a house fire. */
    var assignedId : Int by initOnce()

    /** Shorthand for Java accesses */
    fun store() = store

    val clazz: Class<Voxel>
    private val constructor: Constructor<Voxel>

    init {
        clazz = this.resolveProperty("class")?.let {
            store.parent().modsManager().getClassByName(it)?.let {
                if(Voxel::class.java.isAssignableFrom(it))
                    it as Class<Voxel>
                else
                    throw Exception("The custom class has to extend the Voxel class !")
            }
        }  ?: Voxel::class.java

        constructor = try {
            clazz.getConstructor(VoxelDefinition::class.java)
        } catch (e: NoSuchMethodException) {
            throw Exception("Your custom class, $clazz, lacks the correct Voxel(VoxelDefinition) constructor.")
        }
    }

    fun <V : Voxel> create() = constructor.newInstance(this) as V

    override fun toString(): String {
        return "VoxelDefinition($name, $allProperties)"
    }
}