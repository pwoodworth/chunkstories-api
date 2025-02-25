//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.entity.traits

import xyz.chunkstories.api.entity.Entity

/** Any entity with this trait won't be saved in the chunks data  */
class TraitDontSave(entity: Entity) : Trait(entity)
